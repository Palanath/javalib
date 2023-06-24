package pala.libs.generic.ml.ai.neuralnet4.optimizers;

import java.util.Iterator;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnet4.api.Snapshot;
import pala.libs.generic.ml.ai.neuralnet4.api.Snapshottable;
import pala.libs.generic.util.Pair;

/**
 * The {@link AdaptiveRateGradientDescentOptimizer} attempts to find a good
 * learning rate parameter by assessing the effect of various learning rates on
 * the training cycle's ability to lower loss. A call to
 * {@link #optimize(Computation, Iterator)} will, for each input pair, begin
 * with a small learning rate (set according to {@link #rateGranularity}) and
 * will quickly increase that learning rate and measure the effect on the
 * network's loss. If increase in the learning rate causes divergence, the
 * method restores the previous, loss-decreasing assignment to the weights of
 * the network, and calls the {@link #resultHandler} with the good learning
 * rate, if any is specified.
 * 
 * @author Palanath
 *
 */
public class AdaptiveRateGradientDescentOptimizer extends Optimizer {

	/**
	 * The granularity of the starting learning rate to assess. The learning rate
	 * starts at <code>10^-rateGranularity</code> and is adjusted until an
	 * inappropriate learning rate is found. The last good learning rate is provided
	 * to any {@link AdaptiveRateGDResultHandler} specified.
	 */
	private int rateGranularity = 12;
	private AdaptiveRateGDResultHandler resultHandler;

	public int getRateGranularity() {
		return rateGranularity;
	}

	public void setRateGranularity(int rateGranularity) {
		this.rateGranularity = rateGranularity;
	}

	public AdaptiveRateGDResultHandler getResultHandler() {
		return resultHandler;
	}

	public void setResultHandler(AdaptiveRateGDResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public AdaptiveRateGradientDescentOptimizer(LossFunction lossFunction, int rateGranularity) {
		super(lossFunction);
		this.rateGranularity = rateGranularity;
	}

	public AdaptiveRateGradientDescentOptimizer(LossFunction lossFunction, int rateGranularity,
			AdaptiveRateGDResultHandler resultHandler) {
		super(lossFunction);
		this.rateGranularity = rateGranularity;
		this.resultHandler = resultHandler;
	}

	public AdaptiveRateGradientDescentOptimizer(LossFunction lossFunction) {
		super(lossFunction);
	}

	/**
	 * <p>
	 * Handler that receives some additional information regardnig the result of an
	 * {@link AdaptiveRateGradientDescentOptimizer} optimization attempt.
	 * </p>
	 * 
	 * @author Palanath
	 *
	 */
	public interface AdaptiveRateGDResultHandler {
		/**
		 * <p>
		 * Called after a network is optimized with this {@link Optimizer} on a single
		 * sample. If the <code>learningRate</code> is negative, it's indicative of the
		 * initial learning rate that the optimizer attempted to use resulting in worse
		 * loss than before any optimization attempt.
		 * </p>
		 * 
		 * @param net          The {@link Computation} that was optimized.
		 * @param sample       The sample that the {@link Computation} was optimized on.
		 * @param learningRate The learning rate that ended up being best, or a negative
		 *                     value if no good learning rate was found (and the network
		 *                     was restored to the state it was in upon the call to
		 *                     {@link AdaptiveRateGradientDescentOptimizer#optimize(Computation, Iterator)}).
		 */
		void handle(Computation net, Pair<? extends double[], ? extends double[]> sample, double learningRate);
	}

	@Override
	public void optimize(Computation networkToOptimize,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSampleGenerator) {
		assert networkToOptimize instanceof Snapshottable : "Network can't be optimized.";
		NEXT_SAMPLE: while (labeledSampleGenerator.hasNext()) {
			Pair<? extends double[], ? extends double[]> sample = labeledSampleGenerator.next();
			Snapshottable net = (Snapshottable) networkToOptimize;

			double previousRate = -1;
			double rate = Math.pow(10, -rateGranularity);
			if (rate == 0)
				rate = Double.MIN_NORMAL;

			double initialLoss = getLossFunction().evalLoss(sample.first, (double[]) sample.second);

			for (int i = 0; i < rateGranularity; i++) {
				Snapshot s = new Snapshot();
				net.save(s);// Save current model.

				// Attempt optimization at rate.
				subtractGrads(networkToOptimize.calculateWeightGrads(getLossFunction(), sample.first,
						(double[]) sample.second), rate);

				// Gauge performance.
				double newLoss = getLossFunction().evalLoss(sample.first, (double[]) sample.second);

				// Check performance.
				if (newLoss < initialLoss) {
					previousRate = rate;
					rate *= 10;
				} else {
					net.restore(s); // If performance was worse, save the iteration count.
					if (resultHandler != null)
						resultHandler.handle(networkToOptimize, sample, previousRate);
					continue NEXT_SAMPLE;
				}
			}
			if (resultHandler != null)
				resultHandler.handle(networkToOptimize, sample, previousRate);
		}
	}

}
