package org.jenkinsci.plugins.stepcounter;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class StepCounterProjectAction implements Action {

    public static final String STEPCOUNTERPROJECTACTION_PATH = "stepResult";

    private StepCounterResultAction result;

    private AbstractProject<?, ?> project;

    public StepCounterProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public StepCounterResultAction getResult() {
        return result;
    }

    public void setResult(StepCounterResultAction result) {
        this.result = result;
    }

    public String getDisplayName() {
        return Messages.steps();
    }

    public String getIconFileName() {
        return "graph.png";
    }

    public String getUrlName() {
        return STEPCOUNTERPROJECTACTION_PATH;
    }

    public void doTrend(StaplerRequest req, StaplerResponse rsp) throws IOException {
        StepCounterResultAction a = getPreviousResult();
        if (a != null)
            a.createGraph(req, rsp);
        else
            rsp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public void doTrendMap(StaplerRequest req, StaplerResponse rsp) throws IOException {
        StepCounterResultAction a = getPreviousResult();
        if (a != null)
            a.createClickableMap(req, rsp);
        else
            rsp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public StepCounterResultAction getPreviousResult() {
        final AbstractBuild<?, ?> tb = project.getLastSuccessfulBuild();

        AbstractBuild<?, ?> b = project.getLastBuild();
        while (b != null) {
            StepCounterProjectAction a = b.getAction(StepCounterProjectAction.class);
            if (a != null)
                return a.getResult();
            if (b == tb)
                // if even the last successful build didn't produce the test
                // result,
                // that means we just don't have any tests configured.
                return null;
            b = b.getPreviousBuild();
        }

        return null;
    }

    public AbstractBuild<?, ?> getLastFinishedBuild(AbstractBuild<?, ?> lastBuild) {
        while (lastBuild != null
                && (lastBuild.isBuilding() || lastBuild.getAction(StepCounterProjectAction.class) == null)) {
            lastBuild = lastBuild.getPreviousBuild();
        }
        return lastBuild;
    }

    public void doIndex(final StaplerRequest request, final StaplerResponse response) throws IOException {
        if (getResult() == null) {
            AbstractBuild<?, ?> build = getLastFinishedBuild(project.getLastBuild());
            if (build != null) {
                response.sendRedirect2(String.format("../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH
                        + "/result"));
            }
        } else {
            AbstractBuild<?, ?> build = getLastFinishedBuild(getResult().getOwner());
            if (build != null) {
                response.sendRedirect2(String.format("../../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH
                        + "/result"));
            }
        }
    }

}
