package pala.libs.generic.ml.ai.neuralnets.computations;

public class DenseLayerNode extends ChainComputation {

	public WeightLayerNode getWeightLayerNode() {
		return (WeightLayerNode) getSubComputations().get(0);
	}

	public OneToOneComputation getActivationFunction() {
		return (OneToOneComputation) getSubComputations().get(1);
	}

	public DenseLayerNode(int inputs, OneToOneComputation activationFunction) {
		super(new WeightLayerNode(inputs, activationFunction.inputs()), activationFunction);
	}

	public static DenseLayerNode withRelu(int inputs, int outputs) {
		return new DenseLayerNode(inputs, new ReluComputation(outputs));
	}

	public static DenseLayerNode withSigmoid(int inputs, int outputs) {
		return new DenseLayerNode(inputs, new SigmoidComputation(outputs));
	}

	public static DenseLayerNode withTanh(int inputs, int outputs) {
		return new DenseLayerNode(inputs, new TanhComputation(outputs));
	}
}
