package pala.libs.generic.vectors.operations;

public interface Dividable<E> {
	/**
	 * Divides this item by the provided item and returns the result.
	 * 
	 * @param other The item to split this item by. If this item is <code>6</code>
	 *              and the provided, <code>other</code> item is <code>2</code>, the
	 *              result will be <code>3</code> (which would be returned from this
	 *              method).
	 * @return The result of the division.
	 */
	E divide(E other);
}
