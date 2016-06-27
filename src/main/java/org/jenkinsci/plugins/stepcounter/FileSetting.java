package org.jenkinsci.plugins.stepcounter;

import org.kohsuke.stapler.DataBoundConstructor;

public class FileSetting {

	private String outputFormat;
	private String outputFile;

	@DataBoundConstructor
	public FileSetting(String outputFile, String outputFormat) {
		this.outputFile = outputFile;
		this.outputFormat = outputFormat;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public String getOutputFile() {
		return outputFile;
	}

}
