package pala.libs.generic.util;

public class Pair<F, S> {
	public F first;
	public S second;

	public Pair() {
	}

	public Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}
}
