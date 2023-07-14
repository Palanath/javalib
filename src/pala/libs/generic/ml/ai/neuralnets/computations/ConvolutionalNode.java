package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.Random;
import java.util.function.IntToDoubleFunction;

public class ConvolutionalNode extends WeightLayerNode {

	private final int inputWidth, kernelWidth;

	public ConvolutionalNode(int inputWidth, int inputHeight, int kernelWidth, int kernelHeight) {
		this((IntToDoubleFunction) null, inputWidth, inputHeight, kernelWidth, kernelHeight);
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
		super(populator, inputWidth * inputHeight, (inputWidth - kernelWidth + 1) * (inputHeight - kernelHeight + 1));
		this.inputWidth = inputWidth;
		this.kernelWidth = kernelWidth;
	}

	public ConvolutionalNode(Random weightRandomizer, int inputWidth, int inputHeight, int kernelWidth,
			int kernelHeight) {
		super(weightRandomizer, inputWidth * inputHeight,
				(inputWidth - kernelWidth + 1) * (inputHeight - kernelHeight + 1));
		this.inputWidth = inputWidth;
		this.kernelWidth = kernelWidth;
	}

}
