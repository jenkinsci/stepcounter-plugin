package org.jenkinsci.plugins.stepcounter.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.stepcounter.StepCounterFileParser;
import org.jenkinsci.plugins.stepcounter.model.FileStep;

import tk.stepcounter.Util;

import au.com.bytecode.opencsv.CSVReader;

public class CSVStepCounterFileParser implements StepCounterFileParser {

	public List<FileStep> parse(File file, String rootPath) {
		List<FileStep> steps = new ArrayList<FileStep>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				FileStep step = new FileStep();
				step.setFileName(nextLine[0]);
				step.setFileType(nextLine[1]);
				step.setRuns(Integer.valueOf(nextLine[3]));
				step.setBlanks(Integer.valueOf(nextLine[4]));
				step.setComments(Integer.valueOf(nextLine[5]));
				step.setTotal(Integer.valueOf(nextLine[6]));
				step.setFile(file);
				step.setParentDirRelativePath(Util.getParentDirRelativePath(
						file, rootPath));
				steps.add(step);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return steps;
	}

}
