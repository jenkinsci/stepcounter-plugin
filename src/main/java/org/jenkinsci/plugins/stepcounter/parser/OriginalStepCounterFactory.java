package org.jenkinsci.plugins.stepcounter.parser;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import jp.sf.amateras.stepcounter.AreaComment;
import jp.sf.amateras.stepcounter.DefaultStepCounter;
import jp.sf.amateras.stepcounter.StepCounter;
import jp.sf.amateras.stepcounter.StepCounterFactory;

public class OriginalStepCounterFactory {

	public static StepCounter getCounter(File file, List<StepCounterParserSetting> setting) {
		StepCounter counter =null;
		String fileName = file.getName();

		counter = getCounter(fileName,setting);

		if(fileName.endsWith(".jspx")){
			DefaultStepCounter defaultCounter = new DefaultStepCounter();
			defaultCounter.addLineComment("//");
			defaultCounter.addAreaComment(new AreaComment("/*","*/"));
			defaultCounter.addAreaComment(new AreaComment("<%--","--%>"));
			defaultCounter.addAreaComment(new AreaComment("<!--","-->"));
			defaultCounter.setFileType("JSPX");
			counter = defaultCounter;
		}

		if(counter == null){
			counter = StepCounterFactory.getCounter(file.getName());
		}

		return counter;
	}

	private static StepCounter getCounter(String fileName, List<StepCounterParserSetting> setting) {
		for (Iterator<StepCounterParserSetting> it = setting.iterator(); it.hasNext();) {
			StepCounterParserSetting parser = (StepCounterParserSetting) it.next();
			List<String> exts = parser.getFileExtensions();
			if(exts == null) continue;

			for (Iterator<String> extIt = exts.iterator(); extIt.hasNext();) {
				String ext = (String) extIt.next();
				if(fileName.endsWith(ext)){
					return getCounter(parser);
				}

			}
		}
		return null;
	}

	private static DefaultStepCounter getCounter(StepCounterParserSetting parser) {
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.setFileType(parser.getFileType());
		for (Iterator<String> it = parser.getLineComments().iterator(); it.hasNext();) {
			String lineComment = (String) it.next();
			counter.addLineComment(lineComment);
		}
		for (Iterator<AreaComment> it = parser.getAreaComments().iterator(); it.hasNext();) {
			AreaComment areaComment = (AreaComment) it.next();
			counter.addAreaComment(areaComment);
		}
		return counter;
	}

}
