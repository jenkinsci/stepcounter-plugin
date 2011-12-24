package tk.stepcounter;

public class CountResult {

	private String fileName;
	private String fileType;
	private String category;
	private long step;
	private long non;
	private long comment;

	public CountResult(){ }


	public CountResult(String fileName,String fileType,String category,long step,long non,long comment){
		setFileName(fileName);
		setFileType(fileType);
		setStep(step);
		setNon(non);
		setComment(comment);
		setCategory(category);
	}


	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public void setFileType(String fileType){
		this.fileType = fileType;
	}

	
	public void setStep(long step){
		this.step = step;
	}

	
	public void setNon(long non){
		this.non = non;
	}

	
	public void setComment(long comment){
		this.comment = comment;
	}

	
	public String getFileName(){
		return this.fileName;
	}

	
	public String getFileType(){
		return this.fileType;
	}

	
	public long getStep(){
		return this.step;
	}

	
	public long getNon(){
		return this.non;
	}

	
	public long getComment(){
		return this.comment;
	}

	
	public String getResultString(){
		return toString();
	}

	
	public String getCategory() {
		return category;
	}

	
	public void setCategory(String category) {
		this.category = category;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getFileName()).append(" ");
		sb.append("���s:").append(Long.toString(getStep())).append(" ");
		sb.append("��s:").append(Long.toString(getNon())).append(" ");
		sb.append("�R�����g:").append(Long.toString(getComment()));
		return sb.toString();
	}
}