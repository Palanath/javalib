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

	public int getOutputWidth() {
		return inputWidth - kernelWidth + 1;
	}

	public int getOutputHeight() {
		return outputs() / getOutputWidth();
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
		double[] inputs = c.get();
		double[] weightgrads = new double[weights()], ingrads = new double[inputs()];
		weightStorage.put(this, weightgrads);

		// Calculate weight gradients.
		int owidth = getOutputWidth(), oheight = getOutputHeight(), kernelHeight = getKernelHeight();
		for (int ox = 0; ox < owidth; ox++)
			for (int oy = 0; oy < oheight; oy++)
				// Convolve
				for (int kx = 0; kx < kernelWidth; kx++)
					for (int ky = 0; ky < kernelHeight; ky++) {
						weightgrads[kx + ky * kernelWidth] += inputs[ox + kx + (oy + ky) * inputWidth]
								* outGrad[ox + oy * owidth];
						ingrads[ox + kx + (oy + ky) * getInputHeight()] += weights[kx + ky * kernelWidth]
								* outGrad[ox + oy * owidth];
					}
		return ingrads;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = new double[outputs()];
		int owidth = getOutputWidth(), oheight = getOutputHeight(), kernelHeight = getKernelHeight();

		for (int ox = 0; ox < owidth; ox++)
			for (int oy = 0; oy < oheight; oy++)
				// Convolve
				for (int kx = 0; kx < kernelWidth; kx++)
					for (int ky = 0; ky < kernelHeight; ky++)
						res[ox + oy * owidth] += weights[kx + ky * kernelWidth]
								* input[ox + kx + (oy + ky) * inputWidth];
		c.set(input);
		return res;
	}

}
