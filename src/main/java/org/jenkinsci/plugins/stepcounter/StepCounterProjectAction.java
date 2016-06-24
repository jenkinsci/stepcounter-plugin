package org.jenkinsci.plugins.stepcounter;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;

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
		if (resultExists()) {
			return "ステップ数";

		} else {
			return null;
		}

	}

	private boolean resultExists() {
		StepCounterResultAction previousResult = getPreviousResult();

		if(previousResult == null || previousResult.getStepsMap().isEmpty()) return false;

		return true;
	}

	public String getIconFileName() {
		if (resultExists()) {
			return "graph.png";
		} else {
			return null;
		}
	}

	public String getUrlName() {
		if (resultExists()) {
			return STEPCOUNTERPROJECTACTION_PATH;
		} else {
			return null;
		}
	}

	public void doTrend(StaplerRequest req, StaplerResponse rsp) throws IOException {
		StepCounterResultAction a = getPreviousResult();
		if (a != null)
			a.createGraph(req, rsp);
		else
			new StepCounterResultAction(null).createGraph(req, rsp);
	}

	public void doTrendMap(StaplerRequest req, StaplerResponse rsp) throws IOException {
		StepCounterResultAction a = getPreviousResult();
		if (a != null)
			a.createClickableMap(req, rsp);
		else
			new StepCounterResultAction(null).createGraph(req, rsp);
	}

	public StepCounterResultAction getPreviousResult() {
		final AbstractBuild<?, ?> tb = project.getLastSuccessfulBuild();
		AbstractBuild<?, ?> b = project.getLastBuild();
		while (b != null) {
			StepCounterProjectAction a = b.getAction(StepCounterProjectAction.class);
			if (a != null) {
				return a.getResult();
			}
			if (b == tb) {
				System.out.println("if even the last successful build didn't produce "
						+ "the test result, that means we just don't have any tests configured.");
				return null;
			}
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
				response.sendRedirect2(
						String.format("../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH + "/result"));
			}
		} else {
			AbstractBuild<?, ?> build = getLastFinishedBuild(getResult().getOwner());
			if (build != null) {
				response.sendRedirect2(
						String.format("../../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH + "/result"));
			}
		}
	}

}
