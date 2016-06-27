package org.jenkinsci.plugins.stepcounter.format;

import jp.sf.amateras.stepcounter.format.DefaultFormatter;
import jp.sf.amateras.stepcounter.format.FormatterFactory;
import jp.sf.amateras.stepcounter.format.ResultFormatter;

public class OriginalFormatterFactory extends FormatterFactory {
	public static ResultFormatter getFormatter(String format){
		if(format==null){
			return new DefaultFormatter();
		}
		String name = format.toLowerCase();

		if(name.equals("csv")){
			return new OriginalCSVFormatter();
		}	else if(name.equals("excel")){
			return new OriginalXLSFormatter();
		}


		return FormatterFactory.getFormatter(format);
	}
}
