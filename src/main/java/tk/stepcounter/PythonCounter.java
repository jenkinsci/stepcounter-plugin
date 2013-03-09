package tk.stepcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.stepcounter.diffcount.Cutter;
import tk.stepcounter.diffcount.DiffCounterUtil;
import tk.stepcounter.diffcount.DiffSource;

/**
 * docstring���R�����g�Ƃ݂Ȃ�Python�p�̃X�e�b�v�J�E���^�ł��B
 */
public class PythonCounter implements StepCounter, Cutter {

	private static Pattern CATEGORY_PATTERN = Pattern.compile("\\[\\[(.*?)\\]\\]");
	private static Pattern IGNORE_PATTERN = Pattern.compile("\\[\\[IGNORE\\]\\]");
	private static Pattern SINGLE_LINE_COMMENT_PATTERN = Pattern.compile("#.+");
	private static final String DOCSTRING_DELIMITER = "\"\"\""; // docstring delimiter

	/**
	 * �J�E���g���܂��B
	 *
	 * @param file �J�E���g�Ώۂ̃t�@�C��
	 */
	public CountResult count(File file, String charset) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), charset));

		String line     = null;
		String category = "";
		long step    = 0;
		long non     = 0;
		long comment = 0;
		boolean areaFlag = false;
		boolean objectStartingFlag = true;

		while((line = reader.readLine())!=null){
			if(category.length() == 0){
				Matcher matcher = CATEGORY_PATTERN.matcher(line);
				if(matcher.find()){
					category = matcher.group(1);
				}
			}
			if(IGNORE_PATTERN.matcher(line).find()){
				reader.close();
				return null;
			}

			String trimedLine = line.trim();
			if(areaFlag) {
				comment++;
				if(trimedLine.endsWith(DOCSTRING_DELIMITER)) { // docstring finished
					areaFlag=false;
				}
				continue;
			}
			if(objectStartingFlag && trimedLine.startsWith(DOCSTRING_DELIMITER)) { // docstring started
				comment++;
				areaFlag = !trimedLine.endsWith(DOCSTRING_DELIMITER); // avoid one line docstring
				continue;
			}
			objectStartingFlag = false;
			if(trimedLine.length()==0){
				non++;
				continue;
			}
			if(trimedLine.indexOf('#')>=0){
				comment++;
				continue;
			}
			step++;
			if(trimedLine.startsWith("def ") || trimedLine.startsWith("class ")){
				objectStartingFlag = true;
			}
		}
		reader.close();
		return new CountResult(file.getName(), getFileType(), category, step, non, comment);
	}

	public String getFileType(){
		return "Python";
	}

	public DiffSource cut(String source) {
		String category = "";
		boolean isIgnore = false;

		// docstring���폜
		String[] lines = source.split("\n");
		StringBuilder sb = new StringBuilder();

		boolean objectStartingFlag = false;
		boolean docStringFlag = false;

		for(String line: lines){
			String trimedLine = line.trim();

			if(category.length() == 0){
				Matcher matcher = CATEGORY_PATTERN.matcher(line);
				if(matcher.find()){
					category = matcher.group(1);
				}
			}

			if(IGNORE_PATTERN.matcher(line).find()){
				isIgnore = true;
			}

			if(docStringFlag == true){
				if(trimedLine.endsWith(DOCSTRING_DELIMITER)){
					docStringFlag = false;
				}
				continue;
			}

			if(objectStartingFlag == true && trimedLine.startsWith(DOCSTRING_DELIMITER)){
				docStringFlag = !trimedLine.endsWith(DOCSTRING_DELIMITER);
				continue;
			}

			objectStartingFlag = false;

			if(trimedLine.startsWith("def ") || trimedLine.startsWith("class ")){
				objectStartingFlag = true;
			}
			sb.append(line).append("\n");
		}

		source = sb.toString();

		// 1�s�R�����g���폜
		Matcher matcher = SINGLE_LINE_COMMENT_PATTERN.matcher(source);
		source = matcher.replaceAll("");

		// ��s���폜���ĕԋp
		return new DiffSource(DiffCounterUtil.removeEmptyLines(source), isIgnore, category);
	}
}