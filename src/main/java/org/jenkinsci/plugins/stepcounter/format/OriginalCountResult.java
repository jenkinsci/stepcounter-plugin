package org.jenkinsci.plugins.stepcounter.format;

import java.io.File;

import jp.sf.amateras.stepcounter.CountResult;

public class OriginalCountResult extends CountResult {

	private String relativePath;

	public OriginalCountResult(File file, String fileName, String fileType, String category, long step, long non,
			long comment, String relativePath) {
		super(file, fileName, fileType, category, step, non, comment);
		setRelativePath(relativePath);
	}

	public OriginalCountResult(CountResult result, String relativePath){
		setFileName(result.getFileName());
		setFileType(result.getFileType());
		setStep(result.getStep());
		setNon(result.getNon());
		setComment(result.getComment());
		setCategory(result.getCategory());
		setFile(result.getFile());
		setRelativePath(relativePath);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

}
