package tk.stepcounter;

public class StepCounterFactory {

	private static DefaultStepCounter createJavaCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("//");
		counter.addAreaComment(new AreaComment("/*","*/"));
		counter.setFileType(name);
//		counter.addSkipPattern("^package .+;$");
//		counter.addSkipPattern("^import .+;$");
		return counter;
	}

	private static DefaultStepCounter createVBCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("'");
		counter.addLineComment("REM");
		counter.setFileType(name);
		return counter;
	}

	private static DefaultStepCounter createShellCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment("#");
		counter.setFileType(name);
		return counter;
	}

	private static DefaultStepCounter createXMLCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addAreaComment(new AreaComment("<!--","-->"));
		counter.setFileType(name);
		return counter;
	}

	private static DefaultStepCounter createListCounter(String name){
		DefaultStepCounter counter = new DefaultStepCounter();
		counter.addLineComment(";");
		counter.setFileType(name);
		return counter;
	}

	public static StepCounter getCounter(String fileName){
		fileName = fileName.toLowerCase();

		if(fileName.endsWith(".java")){
			return createJavaCounter("Java");

		} else if(fileName.endsWith(".scala")){
			return createJavaCounter("Scala");

		} else if(fileName.endsWith(".cpp") || fileName.endsWith(".c")){
			return createJavaCounter("C/C++");

		} else if(fileName.endsWith(".h")){
			return createJavaCounter("h");

		} else if(fileName.endsWith(".cs")){
			return createJavaCounter("C#");

		} else if(fileName.endsWith(".jsp")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("//");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.addAreaComment(new AreaComment("<%--","--%>"));
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("JSP");
			return counter;

		} else if(fileName.endsWith(".php") || fileName.endsWith(".php3")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("//");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("PHP");
			return counter;

		} else if(fileName.endsWith(".asp") || fileName.endsWith(".asa")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("'");
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.setFileType("ASP");
			return counter;

		} else if(fileName.endsWith(".html") || fileName.endsWith(".htm")){

			return createXMLCounter("HTML");

		} else if(fileName.endsWith(".xhtml")){

			return createXMLCounter("XHTML");

		} else if(fileName.endsWith(".js")){

			return createJavaCounter("js");

		} else if(fileName.endsWith(".vbs")){

			return createVBCounter("vbs");

		} else if(fileName.endsWith(".bas") || fileName.endsWith(".frm") || fileName.endsWith(".cls")){

			return createVBCounter("VB");

		} else if(fileName.endsWith(".vb")){

			return createVBCounter("VB.NET");

		} else if(fileName.endsWith(".pl") || fileName.endsWith(".pm")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addAreaComment(new AreaComment("=pod","=cut"));
			counter.setFileType("Perl");
			return counter;

		} else if(fileName.endsWith(".py")){

//			DefaultStepCounter counter = new DefaultStepCounter();
//			counter.addLineComment("#");
//			counter.setFileType("Python");
			return new PythonCounter();

		} else if(fileName.endsWith(".rb")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addAreaComment(new AreaComment("=begin","=end"));
			counter.setFileType("Ruby");
			return counter;

		} else if(fileName.endsWith(".tcl")){

			return createShellCounter("Tcl");

		} else if(fileName.endsWith(".sql")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("#");
			counter.addLineComment("--");
			counter.addLineComment("REM");
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.setFileType("SQL");
			return counter;

		} else if(fileName.endsWith(".cfm")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("<!--","-->"));
			counter.addAreaComment(new AreaComment("<!---","--->"));
			counter.setFileType("CFM");
			return counter;

		} else if(fileName.endsWith(".properties")) {

			return createShellCounter("Properties");

		} else if(fileName.endsWith(".xml") || fileName.endsWith(".dicon")) {

			return createXMLCounter("XML");

		} else if(fileName.endsWith(".xsl")) {

			return createXMLCounter("XSLT");

		} else if(fileName.endsWith(".xi")) {

			return createXMLCounter("Xi");

		} else if(fileName.endsWith(".dtd")) {

			return createXMLCounter("DTD");

		} else if(fileName.endsWith(".tld")) {

			return createXMLCounter("TLD");

		} else if(fileName.endsWith(".xsd")) {

			return createXMLCounter("XMLSchema");

		} else if(fileName.endsWith(".bat")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("REM");
			counter.setFileType("BAT");
			return counter;

		} else if(fileName.endsWith(".css")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("/*","*/"));
			counter.setFileType("CSS");
			return counter;

		} else if(fileName.endsWith(".l") || fileName.endsWith(".el") || fileName.endsWith(".cl")){

			return createListCounter("Lisp");

		} else if(fileName.endsWith(".clj")){

			return createListCounter("Clojure");

		} else if(fileName.endsWith(".scm")){

			return createListCounter("Scheme");

		} else if(fileName.endsWith(".st")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addAreaComment(new AreaComment("\"","\""));
			counter.setFileType("Smalltalk");
			return counter;

		} else if(fileName.endsWith(".vm") || fileName.endsWith(".vsl")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("##");
			counter.setFileType("Velocity");
			return counter;

		} else if(fileName.endsWith(".ini")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment(";");
			counter.setFileType("INI");
			return counter;

		} else if(fileName.endsWith(".lua")){

			DefaultStepCounter counter = new DefaultStepCounter();
			counter.addLineComment("--");
			counter.addAreaComment(new AreaComment("--[[","]]"));
			counter.addAreaComment(new AreaComment("--[===[","]===]"));
			counter.setFileType("Lua");
			return counter;

		} else if(fileName.endsWith(".hs")){

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