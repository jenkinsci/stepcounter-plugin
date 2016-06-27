package org.jenkinsci.plugins.stepcounter.parser;

import java.io.File;

import jp.sf.amateras.stepcounter.AreaComment;
import jp.sf.amateras.stepcounter.DefaultStepCounter;
import jp.sf.amateras.stepcounter.StepCounter;
import jp.sf.amateras.stepcounter.StepCounterFactory;

public class OriginalStepCounterFactory {

	public static StepCounter getCounter(File file) {
		StepCounter counter = StepCounterFactory.getCounter(file.getName());

		if (counter == null) {
			String fileName = file.getName();

			if(fileName.endsWith(".jspx")){
				DefaultStepCounter defaultCounter = new DefaultStepCounter();
				defaultCounter.addLineComment("//");
				defaultCounter.addAreaComment(new AreaComment("/*","*/"));
				defaultCounter.addAreaComment(new AreaComment("<%--","--%>"));
				defaultCounter.addAreaComment(new AreaComment("<!--","-->"));
				defaultCounter.setFileType("JSPX");
				counter = defaultCounter;

			}
		}
		return counter;
	}

}
