package org.jenkinsci.plugins.stepcounter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.jenkinsci.plugins.stepcounter.format.OriginalFormatterFactory;
import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jenkinsci.plugins.stepcounter.parser.StepCounterParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.format.ResultFormatter;

public class StepCounter extends Publisher {

	public List<StepCounterSetting> settings;

	public boolean isOutput = false;

	public String outputFile;
	public String outputFormat;

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
		if(settings == null){
			throw new Exception("no step count setting exists.");
		}
		this.settings = settings;
	}

	public List<StepCounterSetting> getSettings() {
		return this.settings;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
		try {
			StepCounterProjectAction projectAction = new StepCounterProjectAction(build.getProject());
			StepCounterResultAction resultAction = new StepCounterResultAction(build);
			build.addAction(projectAction);
			projectAction.setResult(resultAction);

			List<StepCounterParser> parsers = new ArrayList<StepCounterParser>();
			if (getSettings() == null) {
				return false;
			}
			EnvVars vars = build.getEnvironment(listener);

			for (StepCounterSetting setting : getSettings()) {
				String encoding = vars.expand(setting.getEncoding());
				String category = vars.expand(setting.getKey());
				String includes = vars.expand(setting.getFilePattern());
				String excludes = vars.expand(setting.getFilePatternExclude());
				listener.getLogger().println("[stepcounter] category [" + category + "]");
				listener.getLogger().println("[stepcounter] includes [" + includes + "]");
				listener.getLogger().println("[stepcounter] excludes [" + excludes + "]");
				listener.getLogger().println("[stepcounter] encoding [" + encoding + "]");
				StepCounterParser finder = new StepCounterParser(includes,
						excludes, encoding, listener, category);
				StepCounterResult result = build.getWorkspace().act(finder);
				resultAction.putStepsMap(category, result);
				parsers.add(finder);
			}

			if (isOutput &&  getOutputFile() != null && !"".equals(getOutputFile())) {
				listener.getLogger().println("[stepcounter] output to file");
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
					File file = new File(new File(build.getWorkspace().toURI()), filename);
					listener.getLogger()
							.println("[stepcounter] output to [" + file.getAbsolutePath() +"] in ["+ format + "] format");
					out = new BufferedOutputStream(new FileOutputStream(file));
					out.write(output);
					out.flush();

				} catch (Exception e) {
					listener.error(e.getMessage());
					e.printStackTrace();
				} finally {
					out.close();
				}
			}

		} catch (Exception e) {
			build.setResult(Result.FAILURE);
			listener.error(e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	public static final class DescriptorImpl extends Descriptor<Publisher> {

		public boolean isApplicable(Class<? extends AbstractProject<?, ?>> aClass) {
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "Step Counter";
		}

		public FormValidation doCheckSettings(@QueryParameter List<StepCounterSetting> settings) throws IOException, ServletException {
			if (settings == null) {
				// TODO try to add check.
				return FormValidation.error("情報");
			}
			return FormValidation.ok();
		}

		public ListBoxModel doFillOutputFormatItems() {
			ListBoxModel items = new ListBoxModel();
			items.add("Excel(xls)", "excel");
			items.add("CSV", "csv");
			items.add("XML", "xml");
			items.add("JSON", "json");
			return items;
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
