package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Addable;
import pala.libs.generic.vectors.operations.Dividable;
import pala.libs.generic.vectors.operations.Multipliable;
import pala.libs.generic.vectors.operations.Subtractable;

public interface ArithmeticVector<V extends Addable<V> & Subtractable<V> & Multipliable<V> & Dividable<V>>
		extends Vector<V>, AddableVector<V>, SubtractableVector<V>, MultipliableVector<V>, DividableVector<V> {
	ArithmeticVector<V> clone();

	@Override
	default ArithmeticVector<? extends V> add(AddableVector<? extends V> other) {
		return (ArithmeticVector<? extends V>) AddableVector.super.add(other);
	}

	@Override
	default ArithmeticVector<? extends V> multiply(MultipliableVector<? extends V> other) {
		return (ArithmeticVector<? extends V>) MultipliableVector.super.multiply(other);
	}

	@Override
	default ArithmeticVector<? extends V> divide(DividableVector<? extends V> other) {
		return (ArithmeticVector<? extends V>) DividableVector.super.divide(other);
	}

	@Override
	default ArithmeticVector<? extends V> subtract(SubtractableVector<? extends V> other) {
		return (ArithmeticVector<? extends V>) SubtractableVector.super.subtract(other);
	}
	
	@Override
	default ArithmeticVector<? extends V> square() {
		return (ArithmeticVector<? extends V>) MultipliableVector.super.square();
	}
}
