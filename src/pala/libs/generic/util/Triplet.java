package pala.libs.generic.util;

public class Triplet<F, S, T> extends Pair<F, S> {
	public T third;

	public Triplet(F first, S second, T third) {
		super(first, second);
		this.third = third;
	}

	public Triplet() {
	}

}
