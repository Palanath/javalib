package pala.libs.generic.ml.ai.neuralnet4;

import java.util.Arrays;
import java.util.Stack;

import pala.libs.generic.JavaTools;

public interface Node {

	/**
	 * <p>
	 * This method receives a {@link ComputationContext} and an array of size
	 * {@link #outputs()} where each element is the derivative of the respective
	 * output of this node with respect to the loss of the computational graph that
	 * this {@link Node} is a part of. The {@link Node} is expected to utilize the
	 * {@link ComputationContext} and its own operation to return a
	 * <code>double</code> array, each element being:
	 * </p>
	 * <ul>
	 * <li>the derivative of the respective input with respect to the loss of the
	 * network that this {@link Node} is a part of.</li>
	 * </ul>
	 * <p>
	 * To do this, this node needs to be able to evaluate the derivative of each of
	 * its inputs with respect to each of its outputs (most generally resulting in a
	 * matrix, conceptually), and then utilize this and the provided gradient
	 * <code>outGrad</code> to compute the derivative of each of its inputs with
	 * respect to the loss.
	 * </p>
	 * <img src="doc-files/node.png" width=500px>
	 * <p>
	 * For the example computational node with two outputs (y<sub>1</sub> and
	 * y<sub>2</sub>), and three inputs (x<sub>1</sub>, x<sub>2</sub>, and
	 * x<sub>3</sub>), the result of the gradient with respect to loss will be a
	 * vector (<code>double</code> array) of size 3. It is a vector that contains
	 * the derivative of each of this {@link Node}'s inputs with respect to the
	 * loss. To compute such vector from the gradient of this {@link Node}'s
	 * outputs, most generally, a <code>grad</code> method will need to determine a
	 * Jacobian matrix representing the derivative of each input of this
	 * {@link Node} with respect to each output of this {@link Node}. This results
	 * in a matrix of size {@link #inputs()} by {@link #outputs()}.
	 * </p>
	 * <p>
	 * From a chain rule perspective, this method is provided with
	 * <code>dL/dy</code>, which is a vector since <code>L</code> (the loss) is a
	 * scalar and <code>y</code>, (the outputs), is a vector. It is expected to
	 * compute <code>dy/dx</code> which is a matrix, since <code>x</code> (the
	 * inputs) and <code>y</code> (the outputs) are both vectors. Finally, it is
	 * expected to return the vector <code>dL/dx</code>, which is obtainable by the
	 * matrix-vector multiplication:
	 * </p>
	 * 
	 * <pre>
	 * <code>dy/dx * dL/dy</code>
	 * </pre>
	 * 
	 * <p>
	 * That is, this method is expected to perform a matrix multiplication
	 * (<code>dy/dx</code>) to the vector it is provided (<code>dL/dy</code>), and
	 * return the result.
	 * </p>
	 * <p>
	 * The matrix multiplication this method is expected to perform can (very) often
	 * be done more efficiently without performing a full matrix multiplication. For
	 * simple {@link Node}s, such as those that perform simple operations (e.g.
	 * addition or multiplication) do not need to compute a full matrix and perform
	 * a matrix-vector multiplication to achieve the same result. Most often, a full
	 * matrix-vector multiplication is not needed.
	 * </p>
	 * 
	 * @param ctx     The context containing the information computed and stored by
	 *                this {@link Node}, if any, during the forward pass (during a
	 *                call to {@link #evaluate(ComputationContext, double...)}).
	 * @param outGrad The gradient of the loss with respect to each of the outputs
	 *                of this {@link Node}.
	 * @return The gradient of the loss with respect to each of the inputs of this
	 *         {@link Node}.
	 */
	double[] grad(ComputationContext ctx, double... outGrad);

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

	/**
	 * Returns the number of weights that this {@link Node} contains. It may be
	 * <code>0</code>.
	 * 
	 * @return The number of weights contained by this {@link Node}. Weights can be
	 *         set with {@link #setWeight(int, double)} and obtained with
	 *         {@link #getWeight(int)}.
	 */
	int weights();

	void setWeight(int weight, double value);

	double getWeight(int weight);

	double[] evaluate(ComputationContext ctx, double... input);

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
		return new SimpleNode() {

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

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
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

		return new SimpleNode() {

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

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}

	static Node add(int inputs) {
		return new VectorNode() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				return new double[] { JavaTools.sum(input) };
			}

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
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
		return new SimpleNode() {

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

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	static Node multiply(int inputs) {
		return new VectorNode() {

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

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
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
	static SimpleNode combine(Node... nodes) {
		int i = 0, o = 0;
		for (int j = 0; j < nodes.length; j++) {
			i += nodes[j].inputs();
			o += nodes[j].outputs();
		}
		int inputs = i, outputs = o;
		return new SimpleNode() {

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
				ComputationContext[] subcontexts = new ComputationContext[nodes.length];
				for (int i = 0; i < nodes.length; i++) {
					double[] subNodeOutput = nodes[i].evaluate(
							subcontexts[i] = ComputationContext.fromStack(new Stack<>()),
							Arrays.copyOfRange(input, iind, iind += nodes[i].inputs()));
					System.arraycopy(subNodeOutput, 0, o, oind, subNodeOutput.length);
					oind += subNodeOutput.length;
				}
				c.save(subcontexts);
				return o;
			}

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				ComputationContext[] subcontexts = ctx.pop();

				double[] inputGrad = new double[inputs];
				int iind = 0, oind = 0;
				for (int i = 0; i < nodes.length; i++) {
					double[] subNodeGrad = nodes[i].grad(subcontexts[i],
							Arrays.copyOfRange(outGrad, oind, oind += nodes[i].outputs()));
					System.arraycopy(subNodeGrad, 0, inputGrad, iind, subNodeGrad.length);
					iind += subNodeGrad.length;
				}
				return inputGrad;
			}
		};
	}

	/**
	 * Returns a {@link SimpleNode} that performs element-wise ReLU.
	 * 
	 * @param inputs The number of inputs to the {@link Node}.
	 * @return The new {@link SimpleNode}. The {@link SimpleNode} has the same
	 *         number of outputs as inputs.
	 */
	static SimpleNode relu(int inputs) {
		return new SimpleNode() {

			@Override
			public int outputs() {
				return inputs;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] grad(ComputationContext ctx, double... outGrad) {
				boolean[] data = ctx.pop();
				double[] res = new double[outGrad.length];
				for (int i = 0; i < res.length; i++)
					if (data[i])
						res[i] = outGrad[i];
				return res;
			}

			@Override
			public double[] evaluate(ComputationContext c, double... input) {
				double[] res = new double[input.length];
				boolean[] data = new boolean[input.length];
				for (int i = 0; i < input.length; i++)
					if (data[i] = (input[i] > 0))
						res[i] = input[i];
				c.save(data);
				return res;
			}
		};
	}

	default String evalToString(ComputationContext c, double... inputs) {
		return Arrays.toString(evaluate(c, inputs));
	}

}
