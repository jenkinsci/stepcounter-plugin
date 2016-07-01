package org.jenkinsci.plugins.stepcounter.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.stepcounter.Messages;
import org.jenkinsci.plugins.stepcounter.format.OriginalCountResult;
import org.jenkinsci.plugins.stepcounter.model.FileStep;
import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jenkinsci.plugins.stepcounter.util.FileFinder;
import org.jenkinsci.plugins.stepcounter.util.PathUtil;
import org.jenkinsci.remoting.RoleChecker;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.StepCounter;


public class StepCounterParser implements FileCallable<StepCounterResult> {
    private static final long serialVersionUID = -6415863872891783891L;

    /** Ant file-set pattern to scan for. */
    private final String filePattern;

    private final String filePatternExclude;

    private String encoding;

    private BuildListener listener;

    private List<OriginalCountResult> _results = new ArrayList<OriginalCountResult>();

	private String category;

	private List<StepCounterParserSetting> setting;

	public StepCounterParser(final String filePattern, final String filePatternExclude, final String encoding,
            final BuildListener listener, String category,List<StepCounterParserSetting> setting) {
        this.filePattern = filePattern;
        this.filePatternExclude = filePatternExclude;
        this.encoding = encoding;
        this.listener = listener;
        this.category = category;
        this.setting = setting;
    }

	public StepCounterResult invoke(final File workspace, final VirtualChannel channel) throws IOException {
        StepCounterResult result = new StepCounterResult();
        try {
            String[] fileNames = new FileFinder(filePattern, filePatternExclude).find(workspace);

            if (fileNames.length == 0) {
            	listener.getLogger().println("[stepcounter] "+ Messages.filenotfound());
            } else {
                listener.getLogger().println("[stepcounter] Parsing " + fileNames.length + " files in " + workspace.getAbsolutePath());
                parseFiles(workspace, fileNames, result);
            }
        } catch (InterruptedException exception) {
            listener.getLogger().println("Parsing has been canceled.");
        }

        listener.getLogger().println("[stepcounter] Parse completed:" + result.getFileSteps().size() + " files");

        long total = 0;
        for (Object oStep : result.getFileSteps()) {
            FileStep step = (FileStep) oStep;
            total += step.getRuns();
        }
        listener.getLogger().println("[stepcounter] total[" + total + "]");

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
                String message = "can't load [" + file.getAbsolutePath() + "]";
                result.addErrorMessage(message);
                continue;
            }
            if (file.length() <= 0) {
                String message = "ilegal file path [" + file.getAbsolutePath() + "]";
                result.addErrorMessage(message);
                continue;
            }

            parseFile(file, result, workspace.getAbsolutePath());
        }
    }

    private void parseFile(final File file, final StepCounterResult result, final String rootPath) throws IOException {
        listener.getLogger().println("[stepcounter] " + file.getAbsolutePath());
        StepCounter counter = OriginalStepCounterFactory.getCounter(file,this.setting);
        if (counter != null) {

            CountResult countResult = counter.count(file, encoding);
            FileStep step = getFileStep(countResult);
            step.setFile(file);

            String relativePath = PathUtil.getParentDirRelativePath(file, rootPath);
            step.setParentDirRelativePath(relativePath);
            result.addFileStep(step);
            countResult.setCategory(category);

            OriginalCountResult originalCountResult = new OriginalCountResult(countResult, relativePath);
            _results.add(originalCountResult);
        } else {
            listener.getLogger().println("[stepcounter] no applicable file type [" + file.getName() + "]");
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

	public void checkRoles(RoleChecker checker) throws SecurityException {
		// TODO 自動生成されたメソッド・スタブ

	}

    public List<OriginalCountResult> getCountResults() {
		return _results;
	}


}