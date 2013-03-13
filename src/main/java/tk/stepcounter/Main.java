package tk.stepcounter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

	private File[] files;

	public void setFiles(File[] files){
		this.files = files;
	}

	/** ・ｽJ・ｽE・ｽ・ｽ・ｽg・ｽ・ｽ・ｽ・ｽ・ｽs・ｽ・ｽ・ｽﾜゑｿｽ */
	public void executeCount(String encoding) throws IOException {
		ArrayList<CountResult> list = new ArrayList<CountResult>();
		for(int i=0;i<files.length;i++){
			CountResult[] results = count(files[i], encoding);
			for(int j=0;j<results.length;j++){
				list.add(results[j]);
			}
		}
		CountResult[] results = (CountResult[])list.toArray(new CountResult[list.size()]);
	}

	/** ・ｽP・ｽt・ｽ@・ｽC・ｽ・ｽ・ｽ・ｽ・ｽJ・ｽE・ｽ・ｽ・ｽg */
	private CountResult[] count(File file, String encoding) throws IOException {
		if(file.isDirectory()){
			File[] files = file.listFiles();
			ArrayList<CountResult> list = new ArrayList<CountResult>();
			for(int i=0;i<files.length;i++){
				CountResult[] results = count(files[i], encoding);
				for(int j=0;j<results.length;j++){
					list.add(results[j]);
				}
			}
			return (CountResult[])list.toArray(new CountResult[list.size()]);
		} else {
			StepCounter counter = StepCounterFactory.getCounter(file.getName());
			if(counter!=null){
				CountResult result = counter.count(file, encoding);
				return new CountResult[]{result};
			} else {
				// ・ｽ・ｽ・ｽﾎ会ｿｽ・ｽﾌ形・ｽ・ｽ・ｽﾌ場合・ｽﾍ形・ｽ・ｽ・ｽ・ｽnull・ｽ・ｽﾝ定し・ｽﾄ返ゑｿｽ
				return new CountResult[]{
					new CountResult(file.getName(), null, null, 0, 0, 0)
				};
			}
		}
	}

	/** ・ｽR・ｽ}・ｽ・ｽ・ｽh・ｽ・ｽ・ｽC・ｽ・ｽ・ｽN・ｽ・ｽ・ｽp・ｽ・ｽ・ｽ\・ｽb・ｽh */
	public static void main(String[] args) throws IOException {

		if(args==null || args.length==0){
			System.exit(0);
		}
//		String format = null;
//		String output = null;
		String encoding = null;
		ArrayList<File> fileList = new ArrayList<File>();
		for(int i=0;i<args.length;i++){
			if(args[i].startsWith("-format=")){
//				String[] dim = Util.split(args[i],"=");
//				format = dim[1];
			} else if(args[i].startsWith("-output=")){
//				String[] dim = Util.split(args[i],"=");
//				output = dim[1];
			} else if(args[i].startsWith("-encoding=")){
				String[] dim = Util.split(args[i],"=");
				encoding = dim[1];
			} else {
				fileList.add(new File(args[i]));
			}
		}

		Main main = new Main();
		main.setFiles((File[])fileList.toArray(new File[fileList.size()]));

		if(encoding == null){
			encoding = System.getProperty("file.encoding");
		}

		main.executeCount(encoding);
	}

}