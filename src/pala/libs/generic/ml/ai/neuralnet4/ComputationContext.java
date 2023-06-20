package pala.libs.generic.ml.ai.neuralnet4;

public interface ComputationContext {
	/**
	 * Pushes the provided {@link Object} onto the context.
	 * 
	 * @param o The context to push.
	 */
	void save(Object o);

	/**
	 * Pops the next object off of the context. This should be called in
	 * coordination with {@link #save(Object)} for a {@link Node} to recover the
	 * data it needs for a backward pass. The {@link Node} should always pop off all
	 * the data it previously {@link #save(Object) saved} in the corresponding
	 * forward evaluation.
	 * 
	 * @param <O> The type of the value to pop off. An unchecked cast is performed
	 *            to this type before this method returns the value.
	 * @return The item highest on the context.
	 */
	@SuppressWarnings("unchecked")
	default <O> O pop() {
		return (O) popImpl();
	}

	/**
	 * Should return the item on the top of this {@link ComputationContext}.
	 * 
	 * @return The next item in the context.
	 */
	Object popImpl();

}
