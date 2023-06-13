package pala.libs.generic.ml.ai.neuralnet;

public class Neuron {

	private DifferentiableFunction activationFunction;

	/**
	 * Constructs a {@link Neuron} with the provided differentiable activation
	 * function. The activation function should expect to receive each input that
	 * this {@link Neuron} gets; <span style="color: red;">this neuron does not sum
	 * its inputs before providing them to the {@link #activationFunction}</span>.
	 * 
	 * @param activationFunction The activation function to use.
	 */
	public Neuron(DifferentiableFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	public double propagate(double... inputs) {
		return activationFunction.evaluate(inputs);
	}

}
