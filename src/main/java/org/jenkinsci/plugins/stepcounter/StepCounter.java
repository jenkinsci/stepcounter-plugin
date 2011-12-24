package org.jenkinsci.plugins.stepcounter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
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
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link StepCounter} is created. The created instance is persisted to the
 * project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #filePattern}) to remember the configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class StepCounter extends Publisher {

    private List<StepCounterSetting> settings;

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
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

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link StepCounter}. Used as a singleton. The class is
     * marked as public so that it can be accessed from views.
     * 
     * <p>
     * See
     * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends Descriptor<Publisher> {
        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         * 
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        // private boolean useFrench;

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

        // @Override
        // public boolean configure(StaplerRequest req, JSONObject formData)
        // throws FormException {
        // // To persist global configuration information,
        // // set that to properties and call save().
        // useFrench = formData.getBoolean("useFrench");
        // // ^Can also use req.bindJSON(this, formData);
        // // (easier when there are many fields; need set* methods for this,
        // like setUseFrench)
        // save();
        // return super.configure(req,formData);
        // }

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
        return Collections.<Action> singleton(new StepCounterProjectAction(project));
    }
}
