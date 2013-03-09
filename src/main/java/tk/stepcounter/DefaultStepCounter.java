package tk.stepcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.stepcounter.diffcount.Cutter;
import tk.stepcounter.diffcount.DiffCounterUtil;
import tk.stepcounter.diffcount.DiffSource;

/** �J�X�^�}�C�Y���Ďg�p�ł���W���̃X�e�b�v�J�E���^�ł� */
public class DefaultStepCounter implements StepCounter, Cutter {

	private static Pattern CATEGORY_PATTERN = Pattern.compile("\\[\\[(.*?)\\]\\]");
	private static Pattern IGNORE_PATTERN = Pattern.compile("\\[\\[IGNORE\\]\\]");

	private List<String> lineComments = new ArrayList<String>();
	private List<AreaComment> areaComments = new ArrayList<AreaComment>();
	private List<String> skipPatterns = new ArrayList<String>();
	private String fileType = "UNDEF";

	/**
	 * �X�L�b�v����p�^�[���i���K�\���j��ǉ����܂��B
	 *
	 * @param pattern �X�L�b�v����p�^�[���i���K�\���j
	 */
	public void addSkipPattern(String pattern){
		this.skipPatterns.add(pattern);
	}

	/**
	 * �X�L�b�v����p�^�[�����擾���܂��B
	 *
	 * @return �X�L�b�v����p�^�[���i���K�\���j�̔z��
	 */
	public String[] getSkipPatterns(){
		return (String[])skipPatterns.toArray(new String[skipPatterns.size()]);
	}

	/** �t�@�C���̎�ނ�ݒ肵�܂� */
	public void setFileType(String fileType){
		this.fileType = fileType;
	}

	/** �t�@�C���̎�ނ��擾���܂� */
	public String getFileType(){
		return this.fileType;
	}

	/** �P��s�R�����g�̊J�n�������ǉ����܂� */
	public void addLineComment(String str){
		this.lineComments.add(str);
	}

	/** �����s�R�����g��ǉ����܂� */
	public void addAreaComment(AreaComment area){
		this.areaComments.add(area);
	}

	/** �J�E���g���܂� */
	public CountResult count(File file, String charset) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), charset));

		String line     = null;
		String category = "";
		long step    = 0;
		long non     = 0;
		long comment = 0;
		boolean areaFlag = false;
		AreaComment lastAreaComment = new AreaComment();

		while((line = reader.readLine()) != null){
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
			if(areaFlag==false){
				if(nonCheck(trimedLine)){
					non++;
				} else if(lineCommentCheck(trimedLine)){
					comment++;
				} else if(skipPatternCheck(trimedLine)){
					non++;
				} else if((lastAreaComment = areaCommentStartCheck(line))!=null){
					comment++;
					areaFlag = true;
				} else {
					step++;
				}
			} else {
				comment++;
				if(areaCommentEndCheck(line, lastAreaComment)){
					areaFlag = false;
				}
			}
		}
		reader.close();
		return new CountResult(file.getName(), getFileType(), category, step, non, comment);
	}

	/** �X�L�b�v�p�^�[���Ƀ}�b�`���邩�`�F�b�N */
	private boolean skipPatternCheck(String line){
		for(int i=0;i<skipPatterns.size();i++){
			if(Pattern.matches((String) skipPatterns.get(i), line)){
				return true;
			}
		}
		return false;
	}

	/** ��s���ǂ������`�F�b�N */
	private boolean nonCheck(String line){
		if(line.equals("")){
			return true;
		}
		return false;
	}

	/** �P��s�R�����g���ǂ������`�F�b�N */
	private boolean lineCommentCheck(String line){
		for(int i=0;i<lineComments.size();i++){
			if(line.startsWith((String) lineComments.get(i))){
				return true;
			}
		}
		for(int i=0;i<areaComments.size();i++){
			AreaComment area = (AreaComment) areaComments.get(i);
			String start = area.getStartString();
			String end   = area.getEndString();

			int index = line.indexOf(start);
			if(index==0 && line.indexOf(end,index)==line.length()-end.length()){
				return true;
			}
		}
		return false;
	}

	/**
	 * �����s�R�����g���J�n���Ă��邩�`�F�b�N
	 */
	private AreaComment areaCommentStartCheck(String line){
		for(int i=0;i<areaComments.size();i++){
			AreaComment area = (AreaComment) areaComments.get(i);
			String start = area.getStartString();
			String end   = area.getEndString();

			int index = line.indexOf(start);
			if(index>=0 && line.indexOf(end,index)<0){
				return area;
			}
		}
		return null;
	}

	/** �����s�R�����g���I�����Ă��邩�`�F�b�N */
	private boolean areaCommentEndCheck(String line,AreaComment area){
		String end = area.getEndString();
		if(line.indexOf(end)>=0){
			return true;
		}
		return false;
	}

	public DiffSource cut(String source) {
		String line = null;
		String category = "";
		boolean isIgnore = false;
		BufferedReader reader = new BufferedReader(new StringReader(source));

		try {
			while((line = reader.readLine()) != null){
				if(category.length() == 0){
					Matcher matcher = CATEGORY_PATTERN.matcher(line);
					if(matcher.find()){
						category = matcher.group(1);
					}
				}
				if(IGNORE_PATTERN.matcher(line).find()){
					isIgnore = true;
				}
			}
		} catch(IOException ex){
			ex.printStackTrace();
		} finally {
			Util.close(reader);
		}

		// �P��R�����g���폜
		for(String lineComment: this.lineComments){
			Pattern	pattern = Pattern.compile(Pattern.quote(lineComment) + ".+");
			Matcher matcher = pattern.matcher(source);
			source = matcher.replaceAll("");
		}

		// �����s�R�����g���폜
		for(AreaComment areaComment: this.areaComments){
			Pattern	pattern = Pattern.compile(
					Pattern.quote(areaComment.getStartString()) + ".+?" + Pattern.quote(areaComment.getEndString()),
					Pattern.DOTALL);
			Matcher matcher = pattern.matcher(source);
			source = matcher.replaceAll("");
		}

		// ��s���폜���ĕԋp
		return new DiffSource(DiffCounterUtil.removeEmptyLines(source), isIgnore, category);
	}

//    /**
//     * �����񒆂̔C�ӂ̕�������w�肵��������ɒu�����܂��B
//     *
//     * @param s �ϊ��Ώۂ̕�����B
//     * @param s1 s2�ɒu������镶����B
//     * @param s2 s1�ɒu�������镶����B
//     * @return �ϊ���̕�����Bs��null�̏ꍇ�͋󕶎����Ԃ��܂��B
//     */
//    private static String replace(String s,String s1,String s2){
//
//        // s ��NULL�������ꍇ�A�󕶎����Ԃ�
//        if(s==null){ return ""; }
//
//        StringBuffer sb = new StringBuffer();
//        for(int i=0;i<s.length();i++){
//            if(s.indexOf(s1,i)==i){
//                sb.append(s2);
//                i = i + s1.length() - 1;
//            } else {
//                sb.append(s.charAt(i));
//            }
//        }
//        return sb.toString();
//    }
}