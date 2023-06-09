package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Multipliable;

public interface MultipliableVector<V extends Multipliable<V>>
		extends Vector<V>, Multipliable<MultipliableVector<? extends V>> {
	MultipliableVector<V> clone();

	@Override
	default MultipliableVector<? extends V> multiply(MultipliableVector<? extends V> other) {
		MultipliableVector<V> res = clone();
		for (int i = 0; i < len(); i++)
			res.set(i, get(i).multiply(other.get(i)));
		return res;
	}

	default MultipliableVector<? extends V> square() {
		return multiply(this);
	}
}
