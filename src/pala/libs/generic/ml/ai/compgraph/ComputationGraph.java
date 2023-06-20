package pala.libs.generic.ml.ai.compgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComputationGraph implements Node {
	private final List<Node> inputs = new ArrayList<>();
	private final Node output;

	ComputationGraph(Node output, Node... inputs) {
		this.inputs.addAll(Arrays.asList(inputs));
		this.output = output;
	}

	@Override
	public int getInputs() {
		int tot = 0;
		for (Node node : inputs)
			tot += node.getInputs();
		return tot;
	}

	@Override
	public int getOutputs() {
		return 1;
	}

	/**
	 * Performs a forward pass on this {@link ComputationGraph} using the provided
	 * vector input. The vector input should match the input shape of this
	 * {@link ComputationGraph}. (It should be the same size as
	 * {@link #getInputs()}.)
	 * 
	 * @param inputs The input vector.
	 * @return The result of computing the {@link ComputationGraph}.
	 */
	public double compute(double... inputs) {
		int ind = 0;
		for (Node n : this.inputs) {
			int is = n.getInputs();
			double[] in = Arrays.copyOfRange(inputs, ind, ind + is);
			ind += is;

		}
	}

	private class CNode implements Node {
		private final Node node;

		public CNode(Node node) {
			this.node = node;
		}
	}
}
