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

	private String fileExtension;
	private String lineComment1;
	private String lineComment2;
	private String lineComment3;
	private String areaComment1;
	private String areaComment2;
	private String areaComment3;
	private String areaComment4;

	@Extension // TODO 不要？
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	//TODO できればareaCommentsやfileExtensionsの形で持たせて、Stringは排除したい
	@DataBoundConstructor
	public StepCounterParserSetting(String fileType, String fileExtension, String lineComment1, String lineComment2,
			String lineComment3, String areaComment1, String areaComment2, String areaComment3, String areaComment4) {
		this.fileType = fileType;

		if (fileExtension != null && !"".equals(fileExtension))
			setFileExtension(fileExtension);

		if (lineComment1 != null && !"".equals(lineComment1))
			setLineComment1(lineComment1);

		if (lineComment2 != null && !"".equals(lineComment2))
			setLineComment2(lineComment2);

		if (lineComment3 != null && !"".equals(lineComment3))
			setLineComment3(lineComment3);

		if (areaComment1 != null && !"".equals(areaComment1))
			setAreaComment1(areaComment1);

		if (areaComment2 != null && !"".equals(areaComment2))
			setAreaComment2(areaComment2);

		if (areaComment3 != null && !"".equals(areaComment3))
			setAreaComment3(areaComment3);

		if (areaComment4 != null && !"".equals(areaComment4))
			setAreaComment4(areaComment4);
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
		this.fileExtension = fileExtension;
		if (fileExtension.contains(",")) {
			String[] s = fileExtension.split(",");
			for (int i = 0; i < s.length; i++) {
				String ext = s[i];
				this.fileExtensions.add(ext);
			}
		} else {
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

	public String getLineComment1() {
		return lineComment1;
	}

	public void setLineComment1(String lineComment1) {
		this.lineComment1 = lineComment1;
		this.addLineComment(lineComment1);
	}

	public String getLineComment2() {
		return lineComment2;
	}

	public void setLineComment2(String lineComment2) {
		this.lineComment2 = lineComment2;
		this.addLineComment(lineComment2);
	}

	public String getLineComment3() {
		return lineComment3;
	}

	public void setLineComment3(String lineComment3) {
		this.lineComment3 = lineComment3;
		this.addLineComment(lineComment3);
	}

	public String getAreaComment1() {
		return areaComment1;
	}

	public void setAreaComment1(String areaComment1) {
		this.areaComment1 = areaComment1;
		addAreaComment(areaComment1);
	}

	public String getAreaComment2() {
		return areaComment2;
	}

	public void setAreaComment2(String areaComment2) {
		this.areaComment2 = areaComment2;
		addAreaComment(areaComment2);
	}

	public String getAreaComment3() {
		return areaComment3;
	}

	public void setAreaComment3(String areaComment3) {
		this.areaComment3 = areaComment3;
		addAreaComment(areaComment3);
	}

	public String getAreaComment4() {
		return areaComment4;
	}

	public void setAreaComment4(String areaComment4) {
		this.areaComment4 = areaComment4;
		addAreaComment(areaComment4);
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setAreaComments(List<AreaComment> areaComments) {
		this.areaComments = areaComments;
	}

	public void setLineComments(List<String> lineComments) {
		this.lineComments = lineComments;
	}

}
