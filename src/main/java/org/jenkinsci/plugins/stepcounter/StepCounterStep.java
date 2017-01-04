package org.jenkinsci.plugins.stepcounter;

import java.util.List;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.google.inject.Inject;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;

public class StepCounterStep extends AbstractStepImpl {

	private List<StepCounterSetting> settings;

	private String outputFile;

	private String outputFormat;

	@DataBoundConstructor
	public StepCounterStep() {
	}

	public List<StepCounterSetting> getSettings() {
		return settings;
	}

	@DataBoundSetter
	public void setSettings(List<StepCounterSetting> settings) {
		this.settings = settings;
	}

	public String getOutputFile() {
		return outputFile;
	}

	@DataBoundSetter
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	@DataBoundSetter
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}



	public static class StepCounterExecution extends AbstractSynchronousStepExecution<Void>{

        @Inject
        private transient StepCounterStep step;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Launcher launcher;

        @StepContextParameter
        private transient Run<?,?> run;

        @StepContextParameter
        private transient FilePath workspace;

        @StepContextParameter
        private transient EnvVars envVars;

		@Override
		protected Void run() throws Exception {
			FileSetting fileSetting = null;
			if(step.outputFile != null && step.outputFormat != null){
				fileSetting = new FileSetting(step.outputFile, step.outputFormat);
			}
			StepCounter stepCounter = new StepCounter(fileSetting,step.settings);
			stepCounter.perform(run, launcher, workspace, listener, envVars);
			return null;
		}

	}

	   @Extension(optional=true)
	    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

	        public DescriptorImpl() {
	            super(StepCounterExecution.class);
	        }

	        @Override
	        public String getFunctionName() {
	            return "stepcounter";
	        }

	        @Override
	        public String getDisplayName() {
	            return "Count steps";
	        }

	        public ListBoxModel doFillOutputFormatItems() {
				return StepCounter.outputFormatItems;
			}
	    }
}
