package pala.libs.generic.ml.ai.neuralnet4.api;

import java.util.Arrays;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnet4.Snapshot;
import pala.libs.generic.ml.ai.neuralnet4.computations.WeightLayerNode;
import pala.libs.generic.util.Pair;

/**
 * <p>
 * Represents some kind of computation on some inputs to produce some outputs.
 * This is the supertype of both {@link Operation} (representing any stateless
 * computation, like addition of two inputs to produce a sum output) and
 * {@link Node} (representing a computation with <i>weights</i> that comprise
 * the computation's state).
 * </p>
 * <p>
 * All {@link Computation}s can have input values propagated through them in a
 * forward pass and can have gradients propagated through them in a backward
 * pass.
 * </p>
 * <p>
 * <b>{@link Computation}s should not be used more than once within the same
 * computational graph unless explicitly allowed</b> for the sake of
 * backpropagation. In particular, {@link Node}s, which contain state (weights)
 * should not be used more than once within the same computational graph,
 * otherwise {@link #grad(Container, WeightGradStorage, double...)} will recover
 * two sets of gradients for the {@link Node}'s weights, the former of which
 * will be overwritten by the latter.
 * </p>
 * <p>
 * Most premade implementations of stateless {@link Computation}s can be
 * obtained using the factory methods of this type. Most stateful
 * implementations will have their own class, like {@link WeightLayerNode}.
 * </p>
 * 
 * @author Palanath
 *
 */
public interface Computation {

	/**
	 * <p>
	 * This method receives a {@link ComputationContext} and an array of size
	 * {@link #outputs()} where each element is the derivative of the respective
	 * output of this node with respect to the loss of the computational graph that
	 * this {@link Computation} is a part of. The {@link Computation} is expected to
	 * utilize the {@link ComputationContext} and its own operation to return a
	 * <code>double</code> array, each element being:
	 * </p>
	 * <ul>
	 * <li>the derivative of the respective input with respect to the loss of the
	 * network that this {@link Computation} is a part of.</li>
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
	 * the derivative of each of this {@link Computation}'s inputs with respect to
	 * the loss. To compute such vector from the gradient of this
	 * {@link Computation}'s outputs, most generally, a <code>grad</code> method
	 * will need to determine a Jacobian matrix representing the derivative of each
	 * input of this {@link Computation} with respect to each output of this
	 * {@link Computation}. This results in a matrix of size {@link #inputs()} by
	 * {@link #outputs()}.
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
	 * simple {@link Computation}s, such as those that perform simple operations
	 * (e.g. addition or multiplication) do not need to compute a full matrix and
	 * perform a matrix-vector multiplication to achieve the same result. Most
	 * often, a full matrix-vector multiplication is not needed.
	 * </p>
	 * <p>
	 * If this {@link Computation} is a {@link Node} and thus, has weights, this
	 * method should also calculate the gradient of the weights with respect to the
	 * loss and store the result of that within the {@link WeightGradStorage}
	 * object.
	 * </p>
	 * 
	 * @param c             A {@link Container} that this method should retrieve
	 *                      information stored within a forward pass, for the
	 *                      purpose of computing the gradient, from. This could
	 *                      include values from the inputs or intermediary
	 *                      calculations. The {@link Container} can only store one
	 *                      item at a time, so if multiple data need to be stored,
	 *                      it must be packaged (e.g. in an instance of a local
	 *                      class).
	 * @param weightStorage An object used to collect and store the weights of each
	 *                      {@link Node} along the graph as gradients are
	 *                      calculated.
	 * @param outGrad       The gradient of the loss with respect to each of the
	 *                      outputs of this {@link Computation}.
	 * @return The gradient of the loss with respect to each of the inputs of this
	 *         {@link Computation}.
	 */
	double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad);

	/**
	 * <p>
	 * Returns the number of scalars that this {@link Computation} requires as
	 * input. The {@link Computation} can be considered to take a vector of this
	 * size as input.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} should only be called with this many input
	 * values.
	 * </p>
	 * 
	 * @return The number of input values that this {@link Computation} accepts.
	 */
	int inputs();

	/**
	 * <p>
	 * Returns the number of scalars that this {@link Computation} returns as output
	 * when evaluated.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} returns this many <code>double</code>s.
	 * </p>
	 * 
	 * @return The number of output values of this {@link Computation}.
	 */
	int outputs();

	double[] evaluate(Container c, double... input);

	/**
	 * Evaluates this {@link Computation} on the provided input vector. The provided
	 * <code>input</code> vector should be of size {@link #inputs()} and the method
	 * will return an array of length {@link #outputs()}.
	 * 
	 * @param input The inputs.
	 * @return The outputs of evaluating the {@link Computation} on the inputs.
	 */
	default double[] eval(double... input) {
		return evaluate(Container.DUMMY, input);
	}

	/**
	 * Creates a {@link Computation} whose result is the evaluation of each of the
	 * provided {@link Computation}s, in order. This is equivalent to function
	 * composition.
	 * 
	 * @param nodes The {@link Computation}s to chain together.
	 * @return The resulting chain {@link Computation}.
	 */
	static Computation chain(Computation... nodes) {
		for (int i = 0; i < nodes.length - 1; i++)
			assert nodes[i].outputs() == nodes[i + 1].inputs() : "Invalid size for provided nodes.";
		return new Operation() {

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
		};
	}

	static @Deprecated Computation map(Computation[] from, Computation[] to) {
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

		return new Operation() {

			@Override
			public int outputs() {
				return toOutputs_;
			}

			@Override
			public int inputs() {
				return fromInputs_;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
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
			public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
				// TODO Auto-generated method stub
				return null;
			}
		};

	}

	static Computation add(int inputs) {
		return new VectorComputation() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
				assert input.length == inputs : "Invalid input for add computation.";
				return new double[] { JavaTools.sum(input) };
			}

			@Override
			public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
				assert outGrad.length == 1 : "Invalid output gradient for add computation.";
				double[] res = new double[inputs];
				Arrays.fill(res, outGrad[0]);
				return res;
			}
		};
	}

	/**
	 * <p>
	 * Returns a map {@link Computation} that does nothing more than map inputs to
	 * outputs.
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
	 * The number of outputs the resulting {@link Computation} will have is equal to
	 * the length of the provided <code>mapping</code> array.
	 * </p>
	 * 
	 * @param inputs  The number of inputs of the returned {@link Computation}.
	 * @param mapping A mapping from each output to an input. Note that the array is
	 *                <b>not copied</b> and so subsequent changes to it will be
	 *                reflected by the map {@link Computation} that gets returned.
	 *                Care should be taken not to change the array during operation,
	 *                particularly between a forward and backward pass.
	 * @return The new {@link Computation}.
	 */
	static Computation map(int inputs, int... mapping) {
		return new Operation() {

			@Override
			public int outputs() {
				return mapping.length;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
				double[] res = new double[mapping.length];
				for (int i = 0; i < mapping.length; i++)
					res[i] = input[mapping[i]];
				return res;
			}

			@Override
			public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
				// Each output that a single input is sent to increases the gradient of that
				// input by the gradient of the output.
				// If one input points to two outputs, with derivatives 5 and 3, respectively,
				// the input's derivative will be 8.
				double[] g = new double[inputs];
				for (int i = 0; i < mapping.length; i++)
					g[mapping[i]] += outGrad[i];
				return g;
			}
		};
	}

	static Computation multiply(int inputs) {
		return new VectorComputation() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
				assert input.length == inputs : "Invalid input for multiply computation.";
				c.set(input);
				double res = 1;
				for (int i = 0; i < input.length; i++)
					res *= input[i];
				return new double[] { res };
			}

			@Override
			public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
				assert outGrad.length == 1 : "Invalid output gradient for multiply computation.";

				// Derivative of input is each of the other inputs.
				double[] ins = ctx.get();
				double m = outGrad[0];// outputDeriv times each input
				for (int i = 0; i < inputs; i++)
					m *= ins[i];

				double[] g = new double[inputs];
				for (int i = 0; i < g.length; i++)
					g[i] = m / ins[i];// division removes "this input" from the total multiplication
				return g;
			}
		};
	}

	/**
	 * Returns a single combination {@link Computation} that has one input for each
	 * of the inputs of the supplied {@link Computation}s and has one output for
	 * each of the outputs of the supplied nodes. The inputs to the returned
	 * combination {@link Computation} are passed directly to the supplied
	 * {@link Computation}s when the returned {@link Computation} is executed, and
	 * the outputs of the supplied {@link Computation}s are provided directly as the
	 * output of the returned {@link Computation}. The order of the supplied
	 * {@link Computation}s is used for the order of the inputs and outputs of this
	 * {@link Computation}.
	 * 
	 * @param nodes The {@link Computation}s to combine.
	 * @return The resulting combination {@link Computation}.
	 */
	static Operation combine(Computation... nodes) {
		int i = 0, o = 0;
		for (int j = 0; j < nodes.length; j++) {
			i += nodes[j].inputs();
			o += nodes[j].outputs();
		}
		int inputs = i, outputs = o;
		return new Operation() {

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
		};
	}

	/**
	 * Returns a {@link Operation} that performs element-wise ReLU.
	 * 
	 * @param inputs The number of inputs to the {@link Computation}.
	 * @return The new {@link Operation}. The {@link Operation} has the same number
	 *         of outputs as inputs.
	 */
	static Operation relu(int inputs) {
		return new Operation() {

			@Override
			public int outputs() {
				return inputs;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
				boolean[] data = ctx.get();
				double[] res = new double[outGrad.length];
				for (int i = 0; i < res.length; i++)
					if (data[i])
						res[i] = outGrad[i];
				return res;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
				double[] res = new double[input.length];
				boolean[] data = new boolean[input.length];
				for (int i = 0; i < input.length; i++)
					if (data[i] = (input[i] > 0))
						res[i] = input[i];
				c.set(data);
				return res;
			}
		};
	}

	default String evalToString(Container c, double... inputs) {
		return Arrays.toString(evaluate(c, inputs));
	}

	static Operation sigmoid(int inputs) {
		return new Operation() {

			@Override
			public int outputs() {
				return inputs;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
				double[] res = new double[inputs], sig = c.get();
				assert sig.length == res.length && sig.length == outGrad.length && sig.length == inputs;
				for (int i = 0; i < inputs; i++)
					res[i] = sig[i] * (1 - sig[i]) * outGrad[i];
				return res;
			}

			@Override
			public double[] evaluate(Container c, double... input) {
				double[] res = new double[inputs];
				for (int i = 0; i < res.length; i++)
					res[i] = 1 / (1 + Math.exp(input[i]));
				c.set(res);
				return res;
			}
		};
	}

	/**
	 * Saves the state of this {@link Computation} into the {@link Snapshot}. If
	 * this {@link Computation} does not, itself, have state, but is composed of
	 * {@link Computation}s that do, it will need to override this method to make
	 * sure that those nodes are snapshotted appropriately.
	 * 
	 * @param snapshot
	 */
	default void snapshot(Snapshot snapshot) {
	}

	default void restore(Snapshot snapshot) {
	}


	default WeightGradStorage calculateWeightGrads(LossFunction lossFunction, double[] correctAnswer, double... input) {
		ContainerImpl c = new ContainerImpl();
		double[] prediction = evaluate(c, input);
		WeightGradStorage store = new WeightGradStorage();

		ContainerImpl lc = new ContainerImpl();
		lossFunction.evaluateLoss(lc, correctAnswer, prediction);
		double[] lossGrad = lossFunction.grad(lc.disableModification());

		grad(c.disableModification(), store, lossGrad);
		return store;
	}

	default void train(LossFunction lossFunction, double learningRate, double[] correctAnswer, double... input) {
		WeightGradStorage wgs = calculateWeightGrads(lossFunction, correctAnswer, input);
		for (Pair<double[], double[]> x : wgs)
			for (int i = 0; i < x.first.length; i++)
				x.first[i] -= x.second[i] * learningRate;
	}

}
