package pala.libs.generic.ml.ai.neuralnet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import pala.libs.generic.JavaTools;

public class NeuralNetwork {

	private final List<Layer> layers = new ArrayList<>();
	private final List<double[][]> connections = new ArrayList<>();

	public NeuralNetwork(Neuron... firstLayer) {
		Layer l = new Layer();
		layers.add(l);
		for (Neuron n : firstLayer)
			l.neurons.add(n);
	}

	public Layer addLayer(Neuron... neurons) {
		connections.add(new double[layers.get(layers.size() - 1).neurons.size()][neurons.length]);
		Layer l = new Layer();
		layers.add(l);
		for (Neuron n : neurons)
			l.neurons.add(n);
		return l;
	}

	public class Layer {
		private final List<Neuron> neurons = new ArrayList<>();

		public double[] propagate(double... inputs) {
			double[] res = new double[neurons.size()];
			for (int i = 0; i < res.length; i++)
				res[i] = neurons.get(i).propagate(inputs);
			return res;
		}

		private Layer() {
		}
	}

	/**
	 * Propagates through this network. The size of the input vector should be the
	 * same size as the input layer of the network; each neuron in the input layer
	 * will get one of the input scalars in the provided <code>inputs</code> vector.
	 * The scalars produced by each output neuron (in the last layer) are returned
	 * from this method.
	 * 
	 * @param inputs The input scalars to send to the network.
	 * @return The outputs of the output layer.
	 */
	public double[] propagate(double... inputs) {
		if (!layers.isEmpty()) {
			inputs = layers.get(0).propagate(inputs);
			for (int i = 0; i < connections.size(); i++) {
				double[] res = new double[inputs.length];
				for (int j = 0; j < res.length; j++)
					res[j] = JavaTools.dotProduct(inputs, connections.get(i)[j]);
				inputs = layers.get(i + 1).propagate(res);
			}
		}
		return inputs;
	}

	public double evaluate(Function<double[], Double> lossFunction, double... inputs) {
		return lossFunction.apply(propagate(inputs));
	}

	private static final DifferentiableFunction AVG = new DifferentiableFunction() {

		@Override
		public double evaluateDerivative(double... input) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double evaluate(double... input) {
			return 1 / (1 + Math.pow(Math.E, -JavaTools.sum(input)));
		}
	};

	public static Neuron createNeuron() {
		return new Neuron(new DifferentiableFunction() {

			@Override
			public double evaluateDerivative(double... input) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double evaluate(double... input) {
				return JavaTools.sum(input);
			}
		});
	}

	public static void main(String[] args) {
		NeuralNetwork nn;
	}

}
