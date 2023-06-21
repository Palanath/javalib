package pala.libs.generic.ml.ai.neuralnet4;

import java.util.Arrays;

import pala.libs.generic.JavaTools;

public interface Node {
	/**
	 * <p>
	 * Returns the number of scalars that this {@link Node} requires as input. The
	 * {@link Node} can be considered to take a vector of this size as input.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} should only be called with this many input
	 * values.
	 * </p>
	 * 
	 * @return The number of input values that this {@link Node} accepts.
	 */
	int inputs();

	/**
	 * <p>
	 * Returns the number of scalars that this {@link Node} returns as output when
	 * evaluated.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} returns this many <code>double</code>s.
	 * </p>
	 * 
	 * @return The number of output values of this {@link Node}.
	 */
	int outputs();

	double[] evaluate(ComputationContext c, double... input);

	/**
	 * Evaluates this {@link Node} on the provided input vector. The provided
	 * <code>input</code> vector should be of size {@link #inputs()} and the method
	 * will return an array of length {@link #outputs()}.
	 * 
	 * @param input The inputs.
	 * @return The outputs of evaluating the {@link Node} on the inputs.
	 */
	default double[] eval(double... input) {
		return evaluate(ComputationContext.DUMMY, input);
	}

	/**
	 * Creates a {@link Node} whose result is the evaluation of each of the provided
	 * {@link Node}s, in order. This is equivalent to function composition.
	 * 
	 * @param nodes The {@link Node}s to chain together.
	 * @return The resulting chain {@link Node}.
	 */
	static Node chain(Node... nodes) {
		for (int i = 0; i < nodes.length - 1; i++)
			assert nodes[i].outputs() == nodes[i + 1].inputs() : "Invalid size for provided nodes.";
		return new Node() {

			@Override
			public int outputs() {
				return nodes[nodes.length - 1].outputs();
			}

			@Override
			public int inputs() {
				return nodes[0].inputs();
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				for (Node node : nodes)
					input = node.evaluate(c, input);// The sizes must match for this to work.
				return input;
			}
		};
	}

	static @Deprecated Node map(Node[] from, Node[] to) {
		int fromOutputs = 0, toInputs = 0, fromInputs = 0, toOutputs = 0;
		for (int i = 0; i < from.length; i++) {
			fromOutputs += from[i].outputs();
			fromInputs += from[i].inputs();
		}
		for (int i = 0; i < to.length; i++) {
			toInputs += to[i].inputs();
			toOutputs += to[i].outputs();
		}

		assert fromOutputs == toInputs : "From nodes total output (" + fromOutputs
				+ ") does not match To nodes total input (" + toInputs + ").";

		final int toOutputs_ = toOutputs, fromInputs_ = fromInputs, inside = fromOutputs;

		return new Node() {

			@Override
			public int outputs() {
				return toOutputs_;
			}

			@Override
			public int inputs() {
				return fromInputs_;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				double[] f = new double[inside];
				int iind = 0, oind = 0;
				for (int i = 0; i < from.length; i++) {
					double[] temp = from[i].evaluate(c, Arrays.copyOfRange(input, iind, iind += from[i].inputs()));
					System.arraycopy(temp, 0, f, oind, temp.length);
					oind += temp.length;
				}

				double[] res = new double[toOutputs_];
				iind = 0;
				oind = 0;
				for (int i = 0; i < to.length; i++) {
					double[] temp = to[i].evaluate(c, Arrays.copyOfRange(f, iind, iind += to[i].inputs()));
					System.arraycopy(temp, 0, res, oind, temp.length);
					oind += temp.length;
				}

				return res;
			}
		};

	}

	static Node add(int inputs) {
		return new SimpleNode() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				return new double[] { JavaTools.sum(input) };
			}
		};
	}

	/**
	 * <p>
	 * Returns a map {@link Node} that does nothing more than map inputs to outputs.
	 * </p>
	 * <p>
	 * The <code>n<sup>th</sup></code> element of the provided <code>mapping</code>
	 * array is the index of the input to map from. The <code>mapping</code> array
	 * therefore represents a map from output to input, where:
	 * </p>
	 * <ul>
	 * <li><i>The index used to access an element in <code>mapping</code></i> is the
	 * output node to map to, and</li>
	 * <li><i>the value stored in <code>mapping</code> at that index</i> is the
	 * input node to map from.</li>
	 * </ul>
	 * <p>
	 * For example, if every element in <code>mapping</code> was <code>0</code>,
	 * then every output in a call to {@link #evaluate(double...)} would be the
	 * input element at index <code>0</code>.
	 * </p>
	 * <p>
	 * The number of outputs the resulting {@link Node} will have is equal to the
	 * length of the provided <code>mapping</code> array.
	 * </p>
	 * 
	 * @param inputs  The number of inputs of the returned {@link Node}.
	 * @param mapping A mapping from each output to an input. Note that the array is
	 *                <b>not copied</b> and so subsequent changes to it will be
	 *                reflected by the map {@link Node} that gets returned. Care
	 *                should be taken not to change the array during operation,
	 *                particularly between a forward and backward pass.
	 * @return The new {@link Node}.
	 */
	static Node map(int inputs, int... mapping) {
		return new Node() {

			@Override
			public int outputs() {
				return mapping.length;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				double[] res = new double[mapping.length];
				for (int i = 0; i < mapping.length; i++)
					res[i] = input[mapping[i]];
				return res;
			}
		};
	}

	static Node multiply(int inputs) {
		return new SimpleNode() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				double res = 1;
				for (int i = 0; i < input.length; i++)
					res *= input[i];
				return new double[] { res };
			}
		};
	}

	/**
	 * Returns a single combination {@link Node} that has one input for each of the
	 * inputs of the supplied {@link Node}s and has one output for each of the
	 * outputs of the supplied nodes. The inputs to the returned combination
	 * {@link Node} are passed directly to the supplied {@link Node}s when the
	 * returned {@link Node} is executed, and the outputs of the supplied
	 * {@link Node}s are provided directly as the output of the returned
	 * {@link Node}. The order of the supplied {@link Node}s is used for the order
	 * of the inputs and outputs of this {@link Node}.
	 * 
	 * @param nodes The {@link Node}s to combine.
	 * @return The resulting combination {@link Node}.
	 */
	static Node combine(Node... nodes) {
		int i = 0, o = 0;
		for (int j = 0; j < nodes.length; j++) {
			i += nodes[j].inputs();
			o += nodes[j].outputs();
		}
		int inputs = i, outputs = o;
		return new Node() {

			@Override
			public int outputs() {
				return outputs;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				double[] o = new double[outputs];
				int iind = 0, oind = 0;
				for (int i = 0; i < nodes.length; i++) {
					double[] subNodeOutput = nodes[i].evaluate(c,
							Arrays.copyOfRange(input, iind, iind += nodes[i].inputs()));
					System.arraycopy(subNodeOutput, 0, o, oind, subNodeOutput.length);
					oind += subNodeOutput.length;
				}
				return o;
			}
		};
	}

	default String evalToString(ComputationContext c, double... inputs) {
		return Arrays.toString(evaluate(c, inputs));
	}

}
