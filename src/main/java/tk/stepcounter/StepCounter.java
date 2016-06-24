package tk.stepcounter;

import java.io.File;
import java.io.IOException;

/** ステップカウンタのインターフェース */
public interface StepCounter {

	/** カウントします */
	public CountResult count(File file, String encoding) throws IOException;

	/** ファイルタイプを取得します */
	public String getFileType();

}