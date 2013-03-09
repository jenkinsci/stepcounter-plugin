package org.jenkinsci.plugins.stepcounter.parser;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.plugins.stepcounter.model.FileStep;
import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jenkinsci.plugins.stepcounter.util.FileFinder;

import tk.stepcounter.CountResult;
import tk.stepcounter.StepCounter;
import tk.stepcounter.StepCounterFactory;
import tk.stepcounter.Util;

public class StepCounterParser implements FileCallable<StepCounterResult> {
    private static final long serialVersionUID = -6415863872891783891L;

    /** Ant file-set pattern to scan for. */
    private final String filePattern;

    private final String filePatternExclude;

    private String encoding;

    private BuildListener listener;

    public StepCounterParser(final String filePattern, final String filePatternExclude, final String encoding,
            final BuildListener listener) {
        this.filePattern = filePattern;
        this.filePatternExclude = filePatternExclude;
        this.encoding = encoding;
        this.listener = listener;
    }

    public StepCounterResult invoke(final File workspace, final VirtualChannel channel) throws IOException {
        StepCounterResult result = new StepCounterResult();
        try {
            String[] fileNames = new FileFinder(filePattern, filePatternExclude).find(workspace);

            if (fileNames.length == 0) {
            	listener.getLogger().println("ファイルが見つかりませんでした。");
            } else {
                listener.getLogger().println("Parsing " + fileNames.length + " files in " + workspace.getAbsolutePath());
                parseFiles(workspace, fileNames, result);
            }
        } catch (InterruptedException exception) {
            listener.getLogger().println("Parsing has been canceled.");
        }

        listener.getLogger().println("解析完了:" + result.getFileSteps().size() + "ファイル");

        long total = 0;
        for (Object oStep : result.getFileSteps()) {
            FileStep step = (FileStep) oStep;
            total += step.getRuns();
        }
        listener.getLogger().println("実行行合計[" + total + "]");

        return result;
    }

    /**
     * Parses the specified collection of files and appends the results to the
     * provided container.
     * 
     * @param workspace
     *            the workspace root
     * @param fileNames
     *            the names of the file to parse
     * @param result
     *            the result of the parsing
     * @throws InterruptedException
     *             if the user cancels the parsing
     * @throws IOException
     */
    private void parseFiles(final File workspace, final String[] fileNames, final StepCounterResult result)
            throws InterruptedException, IOException {
        for (String fileName : fileNames) {
            File file = new File(workspace, fileName);

            if (!file.canRead()) {
                String message = "対象ファイルが読み込めません[" + file.getAbsolutePath() + "]";
                result.addErrorMessage(message);
                continue;
            }
            if (file.length() <= 0) {
                String message = "対象のファイルのパスが不正です。[" + file.getAbsolutePath() + "]";
                result.addErrorMessage(message);
                continue;
            }

            parseFile(file, result, workspace.getAbsolutePath());
        }
    }

    private void parseFile(final File file, final StepCounterResult result, final String rootPath) throws IOException {
        listener.getLogger().println("[stepcounter] " + file.getAbsolutePath());
        StepCounter counter = StepCounterFactory.getCounter(file.getName());
        if (counter != null) {

            CountResult countResult = counter.count(file, encoding);
            FileStep step = getFileStep(countResult);
            step.setFile(file);
            step.setParentDirRelativePath(Util.getParentDirRelativePath(file, rootPath));
            result.addFileStep(step);
        } else {
            listener.getLogger().println("対応していないファイル形式[" + file.getName() + "]");
        }
    }

    private FileStep getFileStep(CountResult countResult) {
        FileStep step = new FileStep();
        step.setBlanks(countResult.getNon());
        step.setComments(countResult.getComment());
        step.setFileName(countResult.getFileName());
        step.setFileType(countResult.getFileType());
        step.setRuns(countResult.getStep());
        step.setTotal(countResult.getNon() + countResult.getComment() + countResult.getStep());
        return step;
    }
}