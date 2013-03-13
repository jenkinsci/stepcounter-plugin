package tk.stepcounter.diffcount;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tk.stepcounter.diffcount.object.AbstractDiffResult;
import tk.stepcounter.diffcount.object.DiffFileResult;
import tk.stepcounter.diffcount.object.DiffFolderResult;

public class DiffCounterUtil {

	public static boolean isIgnore(File file) {
		String name = file.getName();
		if (name.equals("CVS")) {
			return true;
		}
		if (name.equals(".svn")) {
			return true;
		}
		if (name.equals(".hg")) {
			return true;
		}
		if (name.equals(".git")) {
			return true;
		}
		return false;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(date);
	}

	public static String removeEmptyLines(String source) {
		StringBuilder sb = new StringBuilder();
		String[] lines = DiffCounterUtil.split(source);
		for (String line : lines) {
			if (!line.matches("\\s*")) {
				sb.append(line).append("\n");
			}
		}

		return sb.toString();
	}

	public static String[] split(String source) {
		List<String> lines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '\n') {
				lines.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}

		if (sb.length() > 0) {
			lines.add(sb.toString());
		}

		return lines.toArray(new String[lines.size()]);
	}

	public static String getSource(File file, String charset) {
		if (file == null) {
			return "";
		}

		try {
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			byte[] buf = new byte[size];
			in.read(buf);
			in.close();
			String source = new String(buf, charset);

			source = source.replaceAll("\r\n", "\n");
			source = source.replaceAll("\r", "\n");

			return source;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String getFileEncoding(File file) {
		String encoding = null;;

		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}

		return System.getProperty("file.encoding");
	}

	public static List<DiffFileResult> convertToList(
			DiffFolderResult folderResult) {
		return new ArrayList<DiffFileResult>(
				convertToMap(folderResult).values());
	}

	private static Map<String, DiffFileResult> convertToMap(
			DiffFolderResult folderResult) {
		Map<String, DiffFileResult> map = new TreeMap<String, DiffFileResult>();

		List<AbstractDiffResult> children = folderResult.getChildren();
		for (AbstractDiffResult child : children) {
			if (child instanceof DiffFolderResult) {
				Map<String, DiffFileResult> childMap = convertToMap((DiffFolderResult)child);
				map.putAll(childMap);
			} else if (child instanceof DiffFileResult) {
				DiffFileResult fileResult = (DiffFileResult)child;
				map.put(fileResult.getPath(), fileResult);
			}
		}

		return map;
	}

}
