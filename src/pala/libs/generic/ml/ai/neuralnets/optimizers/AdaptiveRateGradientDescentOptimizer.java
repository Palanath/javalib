package pala.libs.generic.ml.ai.neuralnets.optimizers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Snapshot;
import pala.libs.generic.ml.ai.neuralnets.api.Snapshottable;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
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

	public static class LearningRateRecord {
		public final double learningRate, resultingLoss;

		public LearningRateRecord(double learningRate, double resultingLoss) {
			this.learningRate = learningRate;
			this.resultingLoss = resultingLoss;
		}

		@Override
		public String toString() {
			return "[rate=" + learningRate + ", loss=" + resultingLoss + ']';
		}

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
		 * @param net                The {@link Computation} that was optimized.
		 * @param sample             The sample that the {@link Computation} was
		 *                           optimized on.
		 * @param learningRateRecord A, possibly empty, {@link List} of records of the
		 *                           learning rates and the network losses during
		 *                           optimization.
		 */
		void handle(Computation net, Pair<? extends double[], ? extends double[]> sample,
				List<? extends LearningRateRecord> learningRateRecord);
	}

	public static class BestRateSaver implements AdaptiveRateGDResultHandler {

		private double bestRate;

		public double getBestRate() {
			return bestRate;
		}

		@Override
		public void handle(Computation net, Pair<? extends double[], ? extends double[]> sample,
				List<? extends LearningRateRecord> learningRateRecord) {
			bestRate = learningRateRecord.get(learningRateRecord.size() - 1).learningRate;
		}

	}

	@Override
	public void optimize(Computation networkToOptimize,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSampleGenerator) {
		assert networkToOptimize instanceof Snapshottable : "Network can't be optimized.";
		NEXT_SAMPLE: while (labeledSampleGenerator.hasNext()) {
			Pair<? extends double[], ? extends double[]> sample = labeledSampleGenerator.next();
			Snapshottable net = (Snapshottable) networkToOptimize;

			List<LearningRateRecord> records = new ArrayList<>();

			double rate = Math.pow(10, -rateGranularity);
			if (rate == 0)
				rate = Double.MIN_NORMAL;

			// Original state.
			Snapshot s = new Snapshot();
			net.save(s);
			Snapshot bestSnapshot = s;
			double bestLoss = getLossFunction().evalLoss(sample.first,
					networkToOptimize.eval((double[]) sample.second));

			// Network is optimal right now for this sample.
			if (bestLoss == 0)
				continue;

			// Calculate gradients.
			WeightGradStorage grads = networkToOptimize.calculateWeightGrads(getLossFunction(), sample.first,
					(double[]) sample.second);

			for (int i = 0; i < rateGranularity; i++) {
				// Attempt optimization at rate.
				subtractGrads(grads, rate);

				// Gauge performance.
				double newLoss = getLossFunction().evalLoss(sample.first,
						networkToOptimize.eval((double[]) sample.second));

				// Check performance.
				if (newLoss < bestLoss) {
					bestLoss = newLoss;
					bestSnapshot = new Snapshot();
					net.save(bestSnapshot);

					// Try the next learning rate from the same state we were in when we started on
					// this sample.
					net.restore(s);
					records.add(new LearningRateRecord(rate, newLoss));
					rate *= 10;
				} else {
					// Best loss found already. It's at the end of the records list.
					if (resultHandler != null)
						resultHandler.handle(networkToOptimize, sample, records);
					net.restore(bestSnapshot);
					continue NEXT_SAMPLE;
				}
			}
			net.restore(bestSnapshot);
			if (resultHandler != null)
				resultHandler.handle(networkToOptimize, sample, records);
		}
	}

}
