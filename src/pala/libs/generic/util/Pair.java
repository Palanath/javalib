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

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	public Pair<S, F> swap() {
		return new Pair<>(second, first);
	}

	public Pair<F, S> setFirst(F first) {
		this.first = first;
		return this;
	}

	public Pair<F, S> setSecond(S second) {
		this.second = second;
		return this;
	}
}
