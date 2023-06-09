package pala.libs.generic.vectors.operations;

public interface Subtractable<E> {
	/**
	 * Subtracts the provided item from this item.
	 * 
	 * @param other The item to subtract from this.
	 * @return The result of the subtraction.
	 */
	E subtract(E other);
}
