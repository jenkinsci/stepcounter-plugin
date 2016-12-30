package org.jenkinsci.plugins.stepcounter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Action;
import hudson.model.Run;

public class StepCounterProjectAction implements Action {

	public static final String STEPCOUNTERPROJECTACTION_PATH = "stepResult";

	private StepCounterResultAction result;

	private Run<?, ?> run;

	public StepCounterProjectAction(Run<?, ?> run) {
		this.run = run;
	}

	public StepCounterResultAction getResult() {
		return result;
	}

	public void setResult(StepCounterResultAction result) {
		this.result = result;
	}

	public String getDisplayName() {
		if (resultExists()) {
			return Messages.steps();
		} else {
			return null;
		}

	}

	private boolean resultExists() {
		if (result != null){
			return true;
		}else{
			StepCounterResultAction previousResult = getPreviousResult();
			if(previousResult == null || previousResult.getStepsMap().isEmpty()) return false;
		}
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
			return STEPCOUNTERPROJECTACTION_PATH;
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
            rsp.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	public StepCounterResultAction getPreviousResult() {
		final Run<?, ?> tb = run.getPreviousSuccessfulBuild();
		Run<?, ?> b = run.getPreviousBuild();
		while (b != null) {
			StepCounterProjectAction a = run.getAction(StepCounterProjectAction.class);
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

	public Run<?, ?> getLastFinishedBuild(Run<?, ?> lastBuild) {
		while (lastBuild != null
				&& (lastBuild.isBuilding() || lastBuild.getAction(StepCounterProjectAction.class) == null)) {
			lastBuild = lastBuild.getPreviousBuild();
		}
		return lastBuild;
	}

	public void doIndex(final StaplerRequest request, final StaplerResponse response) throws IOException {
		if (getResult() == null) {
			Run<?, ?> build = getLastFinishedBuild(run.getPreviousBuild());
			if (build != null) {
				response.sendRedirect2(
						String.format("../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH + "/result"));
			}
		} else {
			Run<?, ?> build = getLastFinishedBuild(getResult().getOwner());
			if (build != null) {
				response.sendRedirect2(
						String.format("../../%d/%s", build.getNumber(), STEPCOUNTERPROJECTACTION_PATH + "/result"));
			}
		}
	}

}
