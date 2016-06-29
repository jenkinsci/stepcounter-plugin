package org.jenkinsci.plugins.stepcounter.parser;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import jp.sf.amateras.stepcounter.AreaComment;

public class StepCounterParserSetting extends AbstractDescribableImpl<StepCounterParserSetting> {
	private List<AreaComment> areaComments = new ArrayList<AreaComment>();
	private List<String> fileExtensions = new ArrayList<String>();
	private List<String> lineComments = new ArrayList<String>();
	private String fileType;

	@Extension // TODO 不要？
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	@DataBoundConstructor
	public StepCounterParserSetting(String fileType, String fileExtension, String lineComment1, String lineComment2,
			String lineComment3, String areaComment1, String areaComment2, String areaComment3, String areaComment4) {
		this.fileType = fileType;

		if (fileExtension != null && !"".equals(fileExtension))
			setFileExtension(fileExtension);

		if (lineComment1 != null && !"".equals(lineComment1))
			addLineComment(lineComment1);

		if (lineComment2 != null && !"".equals(lineComment2))
			addLineComment(lineComment2);

		if (lineComment3 != null && !"".equals(lineComment3))
			addLineComment(lineComment3);

		if (areaComment1 != null && !"".equals(areaComment1))
			addAreaComment(areaComment1);

		if (areaComment2 != null && !"".equals(areaComment2))
			addAreaComment(areaComment2);

		if (areaComment3 != null && !"".equals(areaComment3))
			addAreaComment(areaComment3);

		if (areaComment4 != null && !"".equals(areaComment4))
			addAreaComment(areaComment4);
	}

	public List<AreaComment> getAreaComments() {
		return areaComments;
	}

	public void addAreaComment(String start, String end) {
		this.areaComments.add(new AreaComment(start, end));
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void addAreaComment(String areaComment) {
		String start;
		String end;

		String[] s = areaComment.split(",", 2);
		start = s[0];
		end = s[1];
		addAreaComment(start, end);
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<StepCounterParserSetting> {
		@Override
		public String getDisplayName() {
			return "";
		}

		@Override
		public hudson.model.Descriptor.PropertyType getPropertyType(Object instance, String field) {
			return super.getPropertyType(instance, field);
		}
	}

	public void addLineComment(String lineComment) {
		this.lineComments.add(lineComment);
	}

	public void setFileExtension(String fileExtension) {
		if(fileExtension.contains(",")){
			String[] s = fileExtension.split(",");
			for (int i = 0; i < s.length; i++) {
				String ext = s[i];
				this.fileExtensions.add(ext);
			}
		}else{
			this.fileExtensions.add(fileExtension);
		}
	}

	public List<String> getFileExtensions() {
		return fileExtensions;
	}

	public void setFileExtensions(List<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	public List<String> getLineComments() {
		return lineComments;
	}

}
