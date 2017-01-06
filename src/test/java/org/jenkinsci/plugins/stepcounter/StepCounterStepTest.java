package org.jenkinsci.plugins.stepcounter;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.Run;

public class StepCounterStepTest {
	@Rule
    public JenkinsRule j = new JenkinsRule() {

	};

	@Test
	public void simpleStepCount() throws Exception{
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("node { stepcounter settings: [[encoding: 'UTF-8', filePattern: 'src/main/**/*.java', filePatternExclude: '', key: 'Java']] }"));
        Run<?,?> run = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(run);

	}
}
