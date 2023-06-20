package pala.libs.generic.ml.ai.compgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ComputationGraphBuilder {
	private final List<Node> inputs = new ArrayList<>();

	public ComputationGraphBuilder(Node... inputNodes) {
		this(Arrays.asList(inputNodes));
	}

	public ComputationGraphBuilder(Collection<? extends Node> nodes) {
		inputs.addAll(nodes);
	}

	/**
	 * <p>
	 * Connects the <code>output</code><sup>th</sup> output of the <code>from</code>
	 * node to the <code>input</code><sup>th</sup> input of the <code>to</code>
	 * node.
	 * </p>
	 * 
	 * @param from   The {@link Node} that data will pass from.
	 * @param output The index of the scalar of the from {@link Node} to get data
	 *               from. Zero-indexed.
	 * @param to     The {@link Node} that data will pass to.
	 * @param input  The index of the input in the <code>to</code> {@link Node}.
	 *               Zero-indexed.
	 */
	public void chain(Node from, int output, Node to, int input) {
		
	}
	
	public ComputationGraph build() {
		
	}
}
