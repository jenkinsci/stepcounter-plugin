package org.jenkinsci.plugins.stepcounter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepCounterResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<FileStep> _steps = new ArrayList<FileStep>();

	List<String> errorMessages = new ArrayList<String>();

	long blankSum = 0;
	long runSum = 0;
	long commentSum = 0;
	long totalSum = 0;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void addErrorMessage(String errorMessage) {
		this.errorMessages.add(errorMessage);
	}

	public List<FileStep> getFileSteps() {
		return _steps;
	}

	public void setFileSteps(List<FileStep> steps) {
		this._steps = steps;
		totalSum = 0;
		commentSum = 0;
		blankSum = 0;
		runSum = 0;
		for (FileStep fileStep : steps) {
			totalSum += fileStep.getTotal();
			commentSum += fileStep.getComments();
			blankSum += fileStep.getBlanks();
			runSum += fileStep.getRuns();
		}
	}

	public void addFileStep(FileStep fileStep) {
		_steps.add(fileStep);
		totalSum += fileStep.getTotal();
		commentSum += fileStep.getComments();
		blankSum += fileStep.getBlanks();
		runSum += fileStep.getRuns();
	}

	public long getTotalSum() {
		return totalSum;
	}

	public long getCommentsSum() {
		return commentSum;
	}

	public long getBlanksSum() {
		return blankSum;
	}

	public long getRunsSum() {
		return runSum;
	}
}
