package org.jenkinsci.plugins.stepcounter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.jenkinsci.plugins.stepcounter.model.StepCounterResult;
import org.jenkinsci.plugins.stepcounter.parser.StepCounterParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class StepCounter extends Publisher {

    public List<StepCounterSetting> settings;
    
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @SuppressWarnings("deprecation")
    @DataBoundConstructor
    public StepCounter(List<StepCounterSetting> settings) {
        this.settings = settings;
    }

    public List<StepCounterSetting> getSettings() {
        return settings;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try {
            StepCounterProjectAction projectAction = new StepCounterProjectAction(build.getProject());
            StepCounterResultAction resultAction = new StepCounterResultAction(build);
            build.addAction(projectAction);
            projectAction.setResult(resultAction);

            for (StepCounterSetting setting : getSettings()) {
                String encoding = setting.getEncoding();
                listener.getLogger().println("[stepcounter] カテゴリは[" + setting.getKey() + "]");
                listener.getLogger().println("[stepcounter] ファイルのパターンは[" + setting.getFilePattern() + "]");
                listener.getLogger().println("[stepcounter] ファイルの除外パターンは[" + setting.getFilePatternExclude() + "]");
                listener.getLogger().println("[stepcounter] ファイルのエンコーディングは[" + encoding + "]");
                StepCounterParser finder = new StepCounterParser(setting.getFilePattern(),
                        setting.getFilePatternExclude(), encoding, listener);
                StepCounterResult result = build.getWorkspace().act(finder);
                resultAction.putStepsMap(setting.getKey(), result);
            }
        } catch (Exception e) {
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

        public FormValidation doCheckSettings(@QueryParameter String key) throws IOException, ServletException {
            return FormValidation.ok();
        }

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
        return Collections.<Action> singleton(new StepCounterProjectAction(project));
    }
}
