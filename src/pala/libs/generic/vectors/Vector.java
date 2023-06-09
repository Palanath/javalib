package pala.libs.generic.vectors;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An ordered collection of objects, much like a {@link List} or an array, that
 * additionally supports operations as its member elements do. Used primarily to
 * abstractly represent mathematical vectors.
 * </p>
 * <p>
 * {@link Vector}s are {@link Cloneable} and so {@link #clone()} should be
 * implemented by subtypes.
 * </p>
 * 
 * @author Palanath
 *
 * @param <V> The type of element in the vector.
 */
public interface Vector<V> extends Iterable<V>, Cloneable {
	Vector<V> clone();

	/**
	 * Gets the element at the specified index.
	 * 
	 * @param index The index of the element.
	 * @return The element.
	 * @throws IndexOutOfBoundsException If the provided index is negative or
	 *                                   <code>&gt;=</code> <code>len()</code>
	 */
	V get(int index) throws IndexOutOfBoundsException;

	/**
	 * Sets the specified index of this vector to be the provided element.
	 * 
	 * @param index   The index of the element.
	 * @param element The new element.
	 * @throws IndexOutOfBoundsException If the provided index is negative or
	 *                                   <code>&gt;=</code> <code>len()</code>
	 */
	void set(int index, V element) throws IndexOutOfBoundsException;

	/**
	 * Returns the length of this {@link Vector}.
	 * 
	 * @return The length of this vector.
	 */
	int len();

	@Override
	default Iterator<V> iterator() {
		return new Iterator<V>() {
			int ind;

			@Override
			public boolean hasNext() {
				return ind < len();
			}

			@Override
			public V next() {
				return get(ind++);
			}
		};
	}
}
