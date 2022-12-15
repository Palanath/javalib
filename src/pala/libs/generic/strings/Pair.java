package pala.libs.generic.strings;

public class Pair {
	public final String string;
	public int value;

	private int length = -1;

	public Pair(final String string, final int value) {
		this.string = string;
		this.value = value;
	}

	public int getLength() {
		return length == -1 ? length = string.length() : length;
	}
}