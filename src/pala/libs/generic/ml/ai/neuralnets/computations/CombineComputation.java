package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.ContainerImpl;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class CombineComputation implements CompositeComputation {

	private final int inputs, outputs;
	private final Computation[] nodes;

	public CombineComputation(Computation... nodes) {
		this.nodes = nodes;
		int i = 0, o = 0;
		for (int j = 0; j < nodes.length; j++) {
			i += nodes[j].inputs();
			o += nodes[j].outputs();
		}
		inputs = i;
		outputs = o;
	}

	@Override
	public int outputs() {
		return outputs;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] o = new double[outputs];
		int iind = 0, oind = 0;
		Container[] subcontexts = new ContainerImpl[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			double[] subNodeOutput = nodes[i].evaluate(subcontexts[i] = new ContainerImpl(),
					Arrays.copyOfRange(input, iind, iind += nodes[i].inputs()));
			System.arraycopy(subNodeOutput, 0, o, oind, subNodeOutput.length);
			oind += subNodeOutput.length;
		}
		c.set(subcontexts);
		return o;
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		ContainerImpl[] subcontexts = ctx.get();

		double[] inputGrad = new double[inputs];
		int iind = 0, oind = 0;
		for (int i = 0; i < nodes.length; i++) {
			double[] subNodeGrad = nodes[i].grad(subcontexts[i].disableModification(), weightStorage,
					Arrays.copyOfRange(outGrad, oind, oind += nodes[i].outputs()));
			System.arraycopy(subNodeGrad, 0, inputGrad, iind, subNodeGrad.length);
			iind += subNodeGrad.length;
		}
		return inputGrad;
	}

	@Override
	public List<? extends Computation> getSubComputations() {
		return Collections.unmodifiableList(Arrays.asList(nodes));
	}

}
