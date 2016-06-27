package org.jenkinsci.plugins.stepcounter.format;

import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.format.CSVFormatter;
import jp.sf.amateras.stepcounter.format.ResultFormatter;

public class OriginalCSVFormatter extends CSVFormatter implements ResultFormatter {
	public byte[] format(CountResult[] results) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < results.length; i++) {
			OriginalCountResult result = (OriginalCountResult)results[i];
			// 未対応の形式をフォーマット
			if (result.getFileType() == null) {
				sb.append(result.getFileName());
				sb.append(",");
				sb.append("未対応");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append("\n");
				// 正常にカウントされたものをフォーマット
			} else {
				sb.append(result.getRelativePath());
				sb.append(",");
				sb.append(result.getFileName());
				sb.append(",");
				sb.append(result.getFileType());
				sb.append(",");
				sb.append(result.getCategory());
				sb.append(",");
				sb.append(result.getStep());
				sb.append(",");
				sb.append(result.getNon());
				sb.append(",");
				sb.append(result.getComment());
				sb.append(",");
				sb.append(result.getStep() + result.getNon() + result.getComment());
				sb.append("\n");
			}
		}
		return sb.toString().getBytes();
	}
}
