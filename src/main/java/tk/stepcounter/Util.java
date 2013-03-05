package tk.stepcounter;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;

public class Util {

	public static String[] split(String str,String del){
		ArrayList<String> list = new ArrayList<String>();
		int pos   = 0;
		int index = 0;
		while((index=str.indexOf(del,pos))!=-1){
			list.add(str.substring(pos,index));
			pos = index + del.length();
		}
		list.add(str.substring(pos,str.length()));
		return (String[])list.toArray(new String[list.size()]);
	}

	public static String substring(String str,int length){
		String resultStr = null;
		int zenCnt      = 0;
//		int kisuuFlg    = 0;
		int loopCnt     = length;
		byte[] resBytes = new byte[length];
		byte[] bytes    = str.getBytes();
		if(bytes.length <= length) {
			return str;
		}
		for (int i=0; i < length; i++) {
			if (bytes[i] < 0) {
				zenCnt ++;
			}
		}
		// �S�p�o�C�g�̐�����̏ꍇ
		if(zenCnt % 2 == 1) {
			loopCnt--;
		}
		for(int i=0; i < loopCnt ; i++) {
			resBytes[i] = bytes[i];
		}
		resultStr = new String(resBytes);
		return resultStr;
	}

	public static int getByteLength(String str){
		try {
			byte[] bytes = str.getBytes();
			return bytes.length;
		} catch(Exception ex){
			return str.getBytes().length;
		}
	}
	public static String escapeXML(String str){
		str.replaceAll("&" ,"&amp;");
		str.replaceAll("<" ,"&gt;");
		str.replaceAll(">" ,"&lt;");
		str.replaceAll("\"","&quot;");
		return str;
	}

	public static void close(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch(Exception ex){
				;
			}
		}
	}

    public static String getParentDirRelativePath(File file, String rootPath) {
        String filePath = file.getParentFile().getAbsolutePath();
        filePath = filePath.replaceAll("\\\\", "/");
        rootPath = rootPath.replaceAll("\\\\", "/");
        String result = filePath.replaceFirst(rootPath, "");
        if(result.startsWith("/")){
            result = result.replaceFirst("/", "");
        }
        return result;
    }
}
