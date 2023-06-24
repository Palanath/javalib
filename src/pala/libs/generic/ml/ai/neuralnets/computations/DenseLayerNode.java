package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.function.IntToDoubleFunction;

public class DenseLayerNode extends ChainComputation {

	public WeightLayerNode getWeightLayerNode() {
		return (WeightLayerNode) getSubComputations().get(0);
	}

	public ShiftNode getBiasNode() {
		return (ShiftNode) getSubComputations().get(1);
	}

	public OneToOneComputation getActivationFunction() {
		return (OneToOneComputation) getSubComputations().get(2);
	}

	public DenseLayerNode(int inputs, OneToOneComputation activationFunction) {
		super(new WeightLayerNode(inputs, activationFunction.inputs()), new ShiftNode(activationFunction.inputs()),
				activationFunction);
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

	public static DenseLayerNode withRelu(IntToDoubleFunction weightPopulator, int inputs, int outputs) {
		DenseLayerNode node = new DenseLayerNode(inputs, new ReluComputation(outputs));
		node.populateWeights(weightPopulator);
		return node;
	}

	public static DenseLayerNode withSigmoid(IntToDoubleFunction weightPopulator, int inputs, int outputs) {
		DenseLayerNode node = new DenseLayerNode(inputs, new SigmoidComputation(outputs));
		node.populateWeights(weightPopulator);
		return node;
	}

	public static DenseLayerNode withTanh(IntToDoubleFunction weightPopulator, int inputs, int outputs) {
		DenseLayerNode node = new DenseLayerNode(inputs, new TanhComputation(outputs));
		node.populateWeights(weightPopulator);
		return node;
	}

	public void populateWeights(IntToDoubleFunction weightPopulator) {
		getWeightLayerNode().populateWeights(weightPopulator);
		getBiasNode().populateWeights(weightPopulator);
	}
}
