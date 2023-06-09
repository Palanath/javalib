package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Subtractable;

public interface SubtractableVector<V extends Subtractable<V>>
		extends Vector<V>, Subtractable<SubtractableVector<? extends V>> {
	SubtractableVector<V> clone();

	@Override
	default SubtractableVector<? extends V> subtract(SubtractableVector<? extends V> other) {
		SubtractableVector<V> res = clone();
		for (int i = 0; i < len(); i++)
			res.set(i, get(i).subtract(other.get(i)));
		return res;
	}
}
