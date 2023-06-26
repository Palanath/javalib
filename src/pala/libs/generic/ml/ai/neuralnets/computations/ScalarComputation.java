package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.ContainerImpl;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public abstract class ScalarComputation extends OneToOneComputation {

	public ScalarComputation(int inputs) {
		super(inputs);
	}

	@Override
	public final double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == outputs();

		ContainerImpl[] subcontainers = c.get();
		double[] res = outGrad.clone();
		for (int i = 0; i < res.length; i++)
			res[i] *= grad(subcontainers[i].disableModification());
		return res;
	}

	@Override
	public final double[] evaluate(Container c, double... input) {
		assert input.length == inputs();
		ContainerImpl[] subcontainers = new ContainerImpl[input.length];
		c.set(subcontainers);
		double[] res = new double[input.length];
		for (int i = 0; i < input.length; i++)
			res[i] = eval(subcontainers[i] = new ContainerImpl(), input[i]);
		return res;
	}

	public abstract double grad(Container c);

	public abstract double eval(Container c, double input);

}
