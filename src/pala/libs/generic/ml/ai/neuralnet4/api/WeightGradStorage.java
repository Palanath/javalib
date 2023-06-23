package pala.libs.generic.ml.ai.neuralnet4.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import pala.libs.generic.JavaTools;
import pala.libs.generic.util.Pair;

/**
 * A simple class used to store the gradients of the weights of {@link Node}s in
 * a map-like form. This is used during a backward pass for {@link Node}s to
 * store the derivatives of the loss w.r.t. their weights.
 * 
 * @author Palanath
 *
 */
public class WeightGradStorage implements Iterable<Pair<double[], double[]>>, Cloneable {
	private final Map<Node, double[]> weightGrads = new HashMap<>();

	public void put(Node node, double... weightGrads) {
		assert node.weights() == weightGrads.length;
		this.weightGrads.put(node, weightGrads);
	}

	public double[] get(Node node) {
		return weightGrads.get(node);
	}

	public Iterable<Pair<Node, double[]>> all() {
		return JavaTools.mask(weightGrads.entrySet(), a -> new Pair<>(a.getKey(), a.getValue()));
	}

	/**
	 * Returns an {@link Iterator} over {@link Pair}s of <code>node weights</code>
	 * against <code>calculated gradients</code> for those weights. To obtain an
	 * {@link Iterable} of each {@link Node} against its weight-gradients, use
	 * {@link #all()}.
	 */
	@Override
	public Iterator<Pair<double[], double[]>> iterator() {
		return JavaTools.mask(weightGrads.entrySet().iterator(), a -> new Pair<>(a.getKey().weights, a.getValue()));
	}

	/**
	 * Clones this {@link WeightGradStorage}, creating and returning a new
	 * {@link WeightGradStorage} whose {@link #weightGrads} {@link Map} and the
	 * <code>double</code> arrays it contains are all new.
	 */
	public @Override WeightGradStorage clone() {
		WeightGradStorage res = new WeightGradStorage();
		for (Entry<Node, double[]> e : weightGrads.entrySet())
			res.weightGrads.put(e.getKey(), e.getValue().clone());
		return res;
	}

	public static WeightGradStorage average(WeightGradStorage... items) {
		WeightGradStorage res = items[0].clone();
		for (int i = 1; i < items.length; i++) {
			final int ind = i;
			res.all().forEach(a -> {
				assert items[ind].weightGrads.get(a.first).length == a.second.length : "The " + ind
						+ "th WeightGradStorage has a weight gradient array that does not match the length of the previous WeightGradStorages ("
						+ a.second.length + ").";
				for (int j = 0; j < a.second.length; j++)
					a.second[j] += items[ind].weightGrads.get(a.first)[j];
			});
		}
		res.all().forEach(a -> {
			for (int i = 0; i < a.second.length; i++)
				a.second[i] /= items.length;
		});
		return res;
	}
}
