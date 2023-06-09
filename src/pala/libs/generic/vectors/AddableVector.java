package pala.libs.generic.vectors;

import pala.libs.generic.vectors.operations.Addable;

public interface AddableVector<V extends Addable<V>> extends Vector<V>, Addable<AddableVector<? extends V>> {

}
