package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.Random;
import java.util.function.IntToDoubleFunction;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class ConvolutionalNode extends Node {

	private final int inputWidth, kernelWidth;

	public ConvolutionalNode(int inputWidth, int inputHeight, int kernelWidth, int kernelHeight) {
		super(inputWidth, inputHeight, kernelWidth, kernelHeight);
		this.inputWidth = inputWidth;
		this.kernelWidth = kernelWidth;
	}

	public int getInputWidth() {
		return inputWidth;
	}

	public int getKernelWidth() {
		return kernelWidth;
	}

	public int getInputHeight() {
		return inputs() / inputWidth;
	}

	public int getKernelHeight() {
		return -(outputs() / (inputWidth - kernelWidth + 1) - 1 - getInputHeight());
	}

	public ConvolutionalNode(IntToDoubleFunction populator, int inputWidth, int inputHeight, int kernelWidth,
			int kernelHeight) {
		this(inputWidth, inputHeight, kernelWidth, kernelHeight);
		populateWeights(populator);
	}

	public ConvolutionalNode(Random weightRandomizer, int inputWidth, int inputHeight, int kernelWidth,
			int kernelHeight) {
		super(weightRandomizer, inputWidth * inputHeight,
				(inputWidth - kernelWidth + 1) * (inputHeight - kernelHeight + 1));
		this.inputWidth = inputWidth;
		this.kernelWidth = kernelWidth;
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		// TODO Auto-generated method stub
		return null;
	}

}
