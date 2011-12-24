package org.jenkinsci.plugins.stepcounter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jenkinsci.plugins.stepcounter.exception.PaserNotFoundException;
import org.jenkinsci.plugins.stepcounter.parser.CSVStepCounterFileParser;
import org.jenkinsci.plugins.stepcounter.parser.XMLStepCounterFileParser;

public class StepCounterFileParserFactory {

	enum Ext {
		csv, xml
	};

	private Map<Ext, StepCounterFileParser> _cache = new HashMap<Ext, StepCounterFileParser>();

	private static final StepCounterFileParserFactory instance = new StepCounterFileParserFactory();

	private StepCounterFileParserFactory() {
	}

	public static StepCounterFileParserFactory getInstance() {
		return instance;
	}

	public StepCounterFileParser createParser(File a_fFile) {
		String name = a_fFile.getName();

		for (Ext ext : Ext.values()) {
			if (name.endsWith(ext.name())) {
				if(_cache.containsKey(ext)){
					return _cache.get(ext);
				} else{
					StepCounterFileParser parser = create(ext);
					return parser;
				}
			}
		}

		throw new PaserNotFoundException("適合するパーサーがありません。");
	}

	private StepCounterFileParser create(Ext ext) {
		switch (ext) {
		case csv:
			return new CSVStepCounterFileParser();
		case xml:
			return new XMLStepCounterFileParser();
		default:
			break;
		}
		throw new PaserNotFoundException("適合するパーサーがありません。");
	}

}
