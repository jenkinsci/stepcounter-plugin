package tk.stepcounter;

/**
 * �X�e�b�v�J�E���^�̃C���X�^���X�𐶐�����t�@�N�g���B
 * ���̃N���X���C�����邱�ƂŊȒP�ɑΉ�����`����ǉ����邱�Ƃ��ł��܂��B
 *
 * <ul>
 *   <li>2.0.0 - Clojure, Scala</li>
 *   <li>1.16.0 - .htm(HTML), dicon</li>
 *   <li>1.15.0 - VB.NET, C#</li>
 *   <li>1.14.0 - Python, Lua, Haskell</li>
 * </ul>
 */
public class StepCounterFactory {

	/**
	 * Java�p�̃J�E���^���쐬���܂��B
	 */
	private static DefaultStepCounter createJavaCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("//");
		counter.addAreaComment(new AreaComment("/*","*/"));
		counter.setFileType(name);
//		counter.addSkipPattern("^package .+;$");
//		counter.addSkipPattern("^import .+;$");
		return counter;
	}

	/**
	 * VB�p�̃J�E���^���쐬���܂��B
	 */
	private static DefaultStepCounter createVBCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("'");
		counter.addLineComment("REM");
		counter.setFileType(name);
		return counter;
	}

	/**
	 * �V�F���X�N���v�g�p�̃J�E���^���쐬���܂��B
	 */
	private static DefaultStepCounter createShellCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("#");
		counter.setFileType(name);
		return counter;
	}

	/**
	 * XML�p�̃J�E���^���쐬���܂��B
	 */
	private static DefaultStepCounter createXMLCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addAreaComment(new AreaComment("<!--","-->"));
		counter.setFileType(name);
		return counter;
	}

	/**
	 * Lisp�p�̃J�E���^���쐬���܂��B
	 */
	private static DefaultStepCounter createListCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment(";");
		counter.setFileType(name);
		return counter;
	}

	/**
	 * �X�e�b�v�J�E���^�̃C���X�^���X���擾���܂��B
	 * ���Ή��̌`���̏ꍇ�Anull��Ԃ��܂��B
	 *
	 * @param fileName �t�@�C����
	 * @return �t�@�C�����ɑΉ������X�e�b�v�J�E���^�̃C���X�^���X�B���Ή��̏ꍇnull�B
	 */
	public static StepCounter getCounter(String fileName){
		// �������ɕϊ�
		fileName = fileName.toLowerCase();

		if(fileName.endsWith(".java")){
			// Java�p�J�E���^���쐬
			return createJavaCounter("Java");

		} else if(fileName.endsWith(".scala")){
			// Scala�p�J�E���^���쐬
			return createJavaCounter("Scala");

		} else if(fileName.endsWith(".cpp") || fileName.endsWith(".c")){
			// C/C++�p�J�E���^���쐬
			return createJavaCounter("C/C++");

		} else if(fileName.endsWith(".h")){
			//�w�b�_�t�@�C���p�J�E���^���쐬
			return createJavaCounter("h");

		} else if(fileName.endsWith(".cs")){
			// C#�p�J�E���^���쐬
			return createJavaCounter("C#");

		} else if(fileName.endsWith(".jsp")){
			// JSP�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("//");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.addAreaComment(new AreaComment("<%--","--%>"));
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("JSP");
			return counter;

		} else if(fileName.endsWith(".php") || fileName.endsWith(".php3")){
			// PHP�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("//");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("PHP");
			return counter;

		} else if(fileName.endsWith(".asp") || fileName.endsWith(".asa")){
			// ASP�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("'");
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("ASP");
			return counter;

		} else if(fileName.endsWith(".html") || fileName.endsWith(".htm")){
			// HTML�p�J�E���^���쐬
			return createXMLCounter("HTML");

		} else if(fileName.endsWith(".xhtml")){
			// XHTML�p�J�E���^���쐬
			return createXMLCounter("XHTML");

		} else if(fileName.endsWith(".js")){
			// JavaScript�p�J�E���^���쐬
			return createJavaCounter("js");

		} else if(fileName.endsWith(".vbs")){
			// VBScript�p�J�E���^���쐬
			return createVBCounter("vbs");

		} else if(fileName.endsWith(".bas") || fileName.endsWith(".frm") || fileName.endsWith(".cls")){
			// VB�p�J�E���^���쐬
			return createVBCounter("VB");

		} else if(fileName.endsWith(".vb")){
			// VB.NET�p�J�E���^���쐬
			return createVBCounter("VB.NET");

		} else if(fileName.endsWith(".pl") || fileName.endsWith(".pm")){
			// Perl�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addAreaComment(new AreaComment("=pod","=cut"));
			counter.setFileType("Perl");
			return counter;

		} else if(fileName.endsWith(".py")){
			// Python�p�J�E���^���쐬
//			DefaultStepCounter counter = new DefaultStepCounter();
//			counter.addLineComment("#");
//			counter.setFileType("Python");
			return new PythonCounter();

		} else if(fileName.endsWith(".rb")){
			// Ruby�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addAreaComment(new AreaComment("=begin","=end"));
			counter.setFileType("Ruby");
			return counter;

		} else if(fileName.endsWith(".tcl")){
			// Tcl�p�J�E���^���쐬
			return createShellCounter("Tcl");

		} else if(fileName.endsWith(".sql")){
			// SQL�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addLineComment("--");
			counter.addLineComment("REM");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.setFileType("SQL");
			return counter;

		} else if(fileName.endsWith(".cfm")){
			// ColdFusion�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.addAreaComment(new AreaComment("<!---","--->"));
			counter.setFileType("CFM");
			return counter;

		} else if(fileName.endsWith(".properties")) {
			// �v���p�e�B�t�@�C���p�J�E���^���쐬
			return createShellCounter("Properties");

		} else if(fileName.endsWith(".xml") || fileName.endsWith(".dicon")) {
			// XML�p�J�E���^���쐬
			return createXMLCounter("XML");

		} else if(fileName.endsWith(".xsl")) {
			// XSLT�p�J�E���^���쐬
			return createXMLCounter("XSLT");

		} else if(fileName.endsWith(".xi")) {
			// Xi�p�J�E���^���쐬
			return createXMLCounter("Xi");

		} else if(fileName.endsWith(".dtd")) {
			// DTD�p�J�E���^���쐬
			return createXMLCounter("DTD");

		} else if(fileName.endsWith(".tld")) {
			// TLD�p�J�E���^���쐬
			return createXMLCounter("TLD");

		} else if(fileName.endsWith(".xsd")) {
			// XMLSchema�p�J�E���^���쐬
			return createXMLCounter("XMLSchema");

		} else if(fileName.endsWith(".bat")){
			// BAT�t�@�C���p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("REM");
			counter.setFileType("BAT");
			return counter;

		} else if(fileName.endsWith(".css")){
			// CSS�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.setFileType("CSS");
			return counter;

		} else if(fileName.endsWith(".l") || fileName.endsWith(".el") || fileName.endsWith(".cl")){
			// Lisp�p�J�E���^���쐬
			return createListCounter("Lisp");

		} else if(fileName.endsWith(".clj")){
			// Clojure�p�J�E���^���쐬
			return createListCounter("Clojure");

		} else if(fileName.endsWith(".scm")){
			// Scheme�p�J�E���^���쐬
			return createListCounter("Scheme");

		} else if(fileName.endsWith(".st")){
			// Smalltalk�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("\"","\""));
			counter.setFileType("Smalltalk");
			return counter;

		} else if(fileName.endsWith(".vm") || fileName.endsWith(".vsl")){
			// Velocity�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("##");
			counter.setFileType("Velocity");
			return counter;

		} else if(fileName.endsWith(".ini")){
			// INI�p�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment(";");
			counter.setFileType("INI");
			return counter;

		} else if(fileName.endsWith(".lua")){
			// Lua�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("--");
			counter.addAreaComment(new AreaComment("--[[","]]"));
			counter.addAreaComment(new AreaComment("--[===[","]===]"));
			counter.setFileType("Lua");
			return counter;

		} else if(fileName.endsWith(".hs")){
			// Haskell�J�E���^���쐬
			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("--");
			counter.addAreaComment(new AreaComment("{-","-}"));
			counter.setFileType("Haskell");
			return counter;

		} else if(fileName.endsWith(".jelly")) {
			return createXMLCounter("Jelly");
		} else {
			return null;
		}
	}
}