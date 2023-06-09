package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Addable;

public interface AddableVector<V extends Addable<V>> extends Vector<V>, Addable<AddableVector<? extends V>> {
	AddableVector<V> clone();

	@Override
	default AddableVector<? extends V> add(AddableVector<? extends V> other) {
		AddableVector<V> res = clone();
		for (int i = 0; i < len(); i++)
			res.set(i, get(i).add(other.get(i)));
		return null;
	}
}
