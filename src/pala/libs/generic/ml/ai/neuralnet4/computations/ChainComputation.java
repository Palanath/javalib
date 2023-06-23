package pala.libs.generic.ml.ai.neuralnet4.computations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.Container;
import pala.libs.generic.ml.ai.neuralnet4.api.ContainerImpl;
import pala.libs.generic.ml.ai.neuralnet4.api.Operation;
import pala.libs.generic.ml.ai.neuralnet4.api.WeightGradStorage;

public class ChainComputation implements Operation, CompositeComputation {

	private final Computation[] nodes;

	/**
	 * Creates a {@link Computation} whose result is the evaluation of each of the
	 * provided {@link Computation}s, in order. This {@link Computation} is a
	 * composition of the provided {@link Computation}s.
	 * 
	 * @param nodes The {@link Computation}s to chain together.
	 */
	public ChainComputation(Computation... nodes) {
		for (int i = 0; i < nodes.length - 1; i++)
			assert nodes[i].outputs() == nodes[i + 1].inputs() : "Invalid size for provided nodes.";
		this.nodes = nodes;
	}

	@Override
	public int outputs() {
		return nodes[nodes.length - 1].outputs();
	}

	@Override
	public int inputs() {
		return nodes[0].inputs();
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		Container[] subcontexts = new ContainerImpl[nodes.length];
		for (int i = 0; i < nodes.length; i++)
			// The sizes must match for this to work. (Previous node's output = next node's
			// input.)
			input = nodes[i].evaluate(subcontexts[i] = new ContainerImpl(), input);
		c.set(subcontexts);
		return input;
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		ContainerImpl[] subcontexts = ctx.get();
		for (int i = nodes.length - 1; i >= 0; i--)
			outGrad = nodes[i].grad(subcontexts[i].disableModification(), weightStorage, outGrad);
		return outGrad;
	}

	@Override
	public List<? extends Computation> getSubComputations() {
		return Collections.unmodifiableList(Arrays.asList(nodes));
	}

}
