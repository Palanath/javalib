package pala.libs.generic.ml.ai.neuralnet4;

public interface WeightedNode extends Node {
	int weights();

	void setWeight(int weight, double value);

	double getWeight(int weight);

	double[] gradWeights(ComputationContext ctx, double... inputs);
}
