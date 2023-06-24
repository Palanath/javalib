package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;

/**
 * A {@link Computation} that has the same number of {@link #inputs()} as
 * {@link #outputs()}.
 * 
 * @author Palanath
 *
 */
public abstract class OneToOneComputation implements Computation {
	private final int inputs;

	public OneToOneComputation(int inputs) {
		this.inputs = inputs;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public int outputs() {
		return inputs;
	}
}
