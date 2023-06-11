package pala.libs.generic.util;

public class Triplet<F, S, T> extends Pair<F, S> {
	public T third;

	public Triplet(F first, S second, T third) {
		super(first, second);
		this.third = third;
	}

	public Triplet() {
	}

	public Triplet<F, S, T> setThird(T third) {
		this.third = third;
		return this;
	}

	@Override
	public Triplet<F, S, T> setFirst(F first) {
		super.setFirst(first);
		return this;
	}

	@Override
	public Triplet<F, S, T> setSecond(S second) {
		super.setSecond(second);
		return this;
	}

}
