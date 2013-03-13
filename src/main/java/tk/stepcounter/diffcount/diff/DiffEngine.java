package tk.stepcounter.diffcount.diff;

import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.diff.DifferentiationFailedException;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.diff.delta.AddDelta;
import org.suigeneris.jrcs.diff.delta.ChangeDelta;
import org.suigeneris.jrcs.diff.delta.DeleteDelta;
import org.suigeneris.jrcs.diff.delta.Delta;

/**
 * ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽﾌ搾ｿｽ・ｽ・ｽ・ｽ謫ｾ・ｽ・ｽ・ｽs・ｽ・ｽ・ｽ・ｽ・ｽﾟゑｿｽDiff・ｽG・ｽ・ｽ・ｽW・ｽ・ｽ・ｽﾅゑｿｽ・ｽB
 *
 * @author Naoki Takezoe
 */
public class DiffEngine {

	private IDiffHandler	handler;

	private String[]		text1;

	private String[]		text2;

	/**
	 * ・ｽR・ｽ・ｽ・ｽX・ｽg・ｽ・ｽ・ｽN・ｽ^・ｽB
	 *
	 * @param handler ・ｽn・ｽ・ｽ・ｽh・ｽ・ｽ
	 * @param text1   ・ｽﾒ集・ｽO・ｽﾌ包ｿｽ・ｽ・ｽ・ｽ・ｽinull・ｽﾌ場合・ｽﾍ空文趣ｿｽ・ｽ・ｽﾆゑｿｽ・ｽﾄ茨ｿｽ・ｽ・ｽ・ｽﾜゑｿｽ・ｽj
	 * @param text2   ・ｽﾒ集・ｽ・ｽﾌ包ｿｽ・ｽ・ｽ・ｽ・ｽinull・ｽﾌ場合・ｽﾍ空文趣ｿｽ・ｽ・ｽﾆゑｿｽ・ｽﾄ茨ｿｽ・ｽ・ｽ・ｽﾜゑｿｽ・ｽj
	 */
	public DiffEngine(IDiffHandler handler, String text1, String text2) {
		if (text1 == null) {
			text1 = "";
		}
		if (text2 == null) {
			text2 = "";
		}
		this.handler = handler;
		this.text1 = splitLine(text1);
		this.text2 = splitLine(text2);
	}

	/**
	 * ・ｽ・ｽ・ｽ・ｽ・ｽ謫ｾ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽs・ｽ・ｽ・ｽﾜゑｿｽ・ｽB
	 */
	public void doDiff() {

		try {
			Revision rev = Diff.diff(this.text1, this.text2);

			int count1 = 0;
			int count2 = 0;

			for (int i = 0; i < rev.size(); i++) {
				Delta delta = rev.getDelta(i);

				Range orgRange = new Range(delta.getOriginal().rangeString());
				Range revRange = new Range(delta.getRevised().rangeString());

				while (count1 != orgRange.getFrom() - 1) {
					this.handler.match(this.text1[count1]);
					count1++;
				}
				count1 = orgRange.getFrom() - 1;
				count2 = revRange.getFrom() - 1;

				if (delta instanceof AddDelta) {
					while (count2 != revRange.getTo()) {
						this.handler.add(this.text2[count2]);
						count2++;
					}

				} else if (delta instanceof DeleteDelta) {
					while (count1 != orgRange.getTo()) {
						this.handler.delete(this.text1[count1]);
						count1++;
					}

				} else if (delta instanceof ChangeDelta) {
					while (count1 != orgRange.getTo()) {
						this.handler.delete(this.text1[count1]);
						count1++;
					}
					while (count2 != revRange.getTo()) {
						this.handler.add(this.text2[count2]);
						count2++;
					}

				}
				count1 = orgRange.getTo();
				count2 = revRange.getTo();
			}

			while (this.text2.length > count2) {
				this.handler.match(this.text2[count2]);
				count2++;
			}
		} catch (DifferentiationFailedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private class Range {
		private int	from;

		private int	to;

		public Range(String rangeString) {
			if (rangeString.indexOf(",") != -1) {
				String[] dim = rangeString.split(",");
				this.from = Integer.parseInt(dim[0]);
				this.to = Integer.parseInt(dim[1]);
			} else {
				this.from = Integer.parseInt(rangeString);
				this.to = Integer.parseInt(rangeString);
			}
		}

		public int getFrom() {
			return this.from;
		}

		public int getTo() {
			return this.to;
		}
	}

	/**
	 * ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽP・ｽs・ｽ・ｽ・ｽﾆに包ｿｽ・ｽ・ｽ・ｽ・ｽ・ｽﾜゑｿｽ・ｽB
	 *
	 * @param text ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ
	 * @return ・ｽP・ｽs・ｽ・ｽ・ｽﾆに包ｿｽ・ｽ・ｽ・ｽ・ｽ・ｽ黷ｽ・ｽ・ｽ・ｽ・ｽ・ｽ・ｽ
	 */
	private static String[] splitLine(String text) {
		String result = text;
		result = result.replaceAll("\r\n", "\n");
		result = result.replaceAll("\r", "\n");
		return result.split("\n");
	}
}
