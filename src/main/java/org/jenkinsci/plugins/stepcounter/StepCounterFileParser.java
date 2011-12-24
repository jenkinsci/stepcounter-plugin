package org.jenkinsci.plugins.stepcounter;

import java.io.File;
import java.util.List;

import org.jenkinsci.plugins.stepcounter.model.FileStep;

public interface StepCounterFileParser {

	List<FileStep> parse(File file,String rootPath);
}
