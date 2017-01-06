package org.jenkinsci.plugins.stepcounter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.jenkinsci.plugins.stepcounter.format.OriginalFormatterFactory;
import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jenkinsci.plugins.stepcounter.parser.StepCounterParser;
import org.jenkinsci.plugins.stepcounter.parser.StepCounterParserSetting;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.format.ResultFormatter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StepCounter extends Publisher {

	public List<StepCounterSetting> settings;

	public boolean isOutput = false;

	public String outputFile;
	public String outputFormat;

	public final static ListBoxModel outputFormatItems = new ListBoxModel();

	static {
		outputFormatItems.add("Excel(xls)", "excel");
		outputFormatItems.add("CSV", "csv");
		outputFormatItems.add("XML", "xml");
		outputFormatItems.add("JSON", "json");
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	@SuppressWarnings("deprecation")
	@DataBoundConstructor
	public StepCounter(FileSetting fileSetting, List<StepCounterSetting> settings) throws Exception {
		if (fileSetting != null) {
			this.isOutput = true;
			this.outputFile = fileSetting.getOutputFile();
			this.outputFormat = fileSetting.getOutputFormat();
		} else {
			this.isOutput = false;
			this.outputFile = "";
			this.outputFormat = null;
		}
		if (settings == null) {
			throw new Exception("no step count setting exists.");
		}
		this.settings = settings;
	}

	public List<StepCounterSetting> getSettings() {
		return this.settings;
	}

	public boolean perform(Run<?, ?> run, Launcher launcher, FilePath workspace, TaskListener listener, EnvVars vars) {

		StepCounterProjectAction projectAction = new StepCounterProjectAction(run.getParent());
		StepCounterResultAction resultAction = new StepCounterResultAction(run);
		run.addAction(projectAction);
		projectAction.setResult(resultAction);
		boolean result = false;
		try {
			result = _perform(resultAction, workspace, vars, listener.getLogger());
		} catch (IOException e) {
			run.setResult(Result.FAILURE);
			listener.error(e.getMessage());
		} catch (InterruptedException e) {
			run.setResult(Result.FAILURE);
			listener.error(e.getMessage());
		}
		return result;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
		StepCounterProjectAction projectAction = new StepCounterProjectAction(build.getProject());
		StepCounterResultAction resultAction = new StepCounterResultAction(build);
		build.addAction(projectAction);
		projectAction.setResult(resultAction);
		boolean result = false;
		try {
			EnvVars vars = build.getEnvironment(listener);
			FilePath workspace = build.getWorkspace();
			result = _perform(resultAction, workspace, vars, listener.getLogger());
		} catch (IOException e) {
			build.setResult(Result.FAILURE);
			listener.error(e.getMessage());
		} catch (InterruptedException e) {
			build.setResult(Result.FAILURE);
			listener.error(e.getMessage());
		}
		return result;
	}

	public boolean _perform(StepCounterResultAction resultAction, FilePath workspace, EnvVars vars, PrintStream logger)
			throws IOException, InterruptedException {

		List<StepCounterParser> parsers = new ArrayList<StepCounterParser>();
		if (getSettings() == null) {
			return false;
		}

		for (StepCounterSetting setting : getSettings()) {
			String encoding = vars.expand(setting.getEncoding());
			String category = vars.expand(setting.getKey());
			String includes = vars.expand(setting.getFilePattern());
			String excludes = vars.expand(setting.getFilePatternExclude());
			logger.println("[stepcounter] category [" + category + "]");
			logger.println("[stepcounter] includes [" + includes + "]");
			logger.println("[stepcounter] excludes [" + excludes + "]");
			logger.println("[stepcounter] encoding [" + encoding + "]");
			StepCounterParser finder = new StepCounterParser(includes, excludes, encoding, logger, category,
					DESCRIPTOR.getCountFormats());
			StepCounterResult result = workspace.act(finder);
			resultAction.putStepsMap(category, result);
			parsers.add(finder);
		}

		if (isOutput && getOutputFile() != null && !"".equals(getOutputFile())) {
			logger.println("[stepcounter] output to file");
			List<CountResult> results = new ArrayList<CountResult>();
			for (Iterator<StepCounterParser> iterator = parsers.iterator(); iterator.hasNext();) {
				StepCounterParser stepCounterParser = (StepCounterParser) iterator.next();
				results.addAll(stepCounterParser.getCountResults());
			}

			String format = vars.expand(getOutputFormat());
			String filename = vars.expand(getOutputFile());
			ResultFormatter formatter = OriginalFormatterFactory.getFormatter(format);
			byte[] output = formatter.format(results.toArray(new CountResult[results.size()]));
			OutputStream out = null;
			try {
				File file = new File(new File(workspace.toURI()), filename);
				logger.println("[stepcounter] output to [" + file.getAbsolutePath() + "] in [" + format + "] format");
				out = new BufferedOutputStream(new FileOutputStream(file));
				out.write(output);
				out.flush();

			} catch (Exception e) {
				logger.println("[stepcounter] " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (out != null)
					out.close();
			}
		}

		return true;
	}

	public static final class DescriptorImpl extends Descriptor<Publisher> {

		private List<StepCounterParserSetting> countFormats;

		public boolean isApplicable(Class<? extends AbstractProject<?, ?>> aClass) {
			return true;
		}

		public DescriptorImpl() {
			super();
			load();
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
			countFormats = new ArrayList<StepCounterParserSetting>();

			if (json.containsKey("countFormats")) {

				if (json.optJSONArray("countFormats") != null) {
					JSONArray list = json.getJSONArray("countFormats");
					for (int i = 0; i < list.size(); i++) {
						JSONObject o = (JSONObject) list.get(i);
						countFormats.add(getSetting(o));
					}
				} else {
					JSONObject countFormat = json.getJSONObject("countFormats");
					countFormats.add(getSetting(countFormat));
				}
			}
			save();
			return super.configure(req, json);
		}

		private StepCounterParserSetting getSetting(JSONObject o) {
			String fileType = null;
			String fileExtension = null;
			String lineComment1 = null;
			String lineComment2 = null;
			String lineComment3 = null;
			String areaComment1 = null;
			String areaComment2 = null;
			String areaComment3 = null;
			String areaComment4 = null;
			if (o.containsKey("fileType")) {
				fileType = o.getString("fileType");
			}
			if (o.containsKey("fileExtension")) {
				fileExtension = o.getString("fileExtension");
			}
			if (o.containsKey("lineComment1")) {
				lineComment1 = o.getString("lineComment1");
			}
			if (o.containsKey("lineComment2")) {
				lineComment2 = o.getString("lineComment2");
			}
			if (o.containsKey("lineComment3")) {
				lineComment3 = o.getString("lineComment3");
			}
			if (o.containsKey("areaComment1")) {
				areaComment1 = o.getString("areaComment1");
			}
			if (o.containsKey("areaComment2")) {
				areaComment2 = o.getString("areaComment2");
			}
			if (o.containsKey("areaComment3")) {
				areaComment3 = o.getString("areaComment3");
			}
			if (o.containsKey("areaComment4")) {
				areaComment4 = o.getString("areaComment4");
			}
			return new StepCounterParserSetting(fileType, fileExtension, lineComment1, lineComment2, lineComment3,
					areaComment1, areaComment2, areaComment3, areaComment4);
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "Step Counter";
		}

		public FormValidation doCheckSettings(@QueryParameter List<StepCounterSetting> settings)
				throws IOException, ServletException {
			if (settings == null) {
				// TODO try to add check.
				return FormValidation.error("情報");
			}
			return FormValidation.ok();
		}

		public ListBoxModel doFillOutputFormatItems() {
			return outputFormatItems;
		}

		// @Override
		// public hudson.model.Descriptor.PropertyType getPropertyType(Object
		// instance, String field) {
		// return super.getPropertyType(instance, field);
		// }

		public List<StepCounterParserSetting> getCountFormats() {
			return countFormats;
		}

		public void setCountFormats(List<StepCounterParserSetting> countFormats) {
			this.countFormats = countFormats;
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.STEP;
	}

	@Override
	public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
		return Collections.<Action> singleton(new StepCounterProjectAction(project));
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public boolean isOutput() {
		return isOutput;
	}

}
