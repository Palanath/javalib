package pala.libs.generic.ml.ai.neuralnet;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

	private final List<Layer> layers = new ArrayList<>();

	public Layer addLayer(Neuron... neurons) {
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
		for (Layer l : layers)
			inputs = l.propagate(inputs);
		return inputs;
	}
}