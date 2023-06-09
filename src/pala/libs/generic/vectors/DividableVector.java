package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Dividable;

public interface DividableVector<V extends Dividable<V>> extends Vector<V>, Dividable<DividableVector<? extends V>> {

	DividableVector<V> clone();

	@Override
	default DividableVector<? extends V> divide(DividableVector<? extends V> other) {
		DividableVector<V> res = clone();
		for (int i = 0; i < len(); i++)
			res.set(i, get(i).divide(other.get(i)));
		return res;
	}
}
