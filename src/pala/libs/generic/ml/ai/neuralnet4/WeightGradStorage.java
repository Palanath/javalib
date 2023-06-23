package pala.libs.generic.ml.ai.neuralnet4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class WeightGradStorage implements Iterable<Pair<double[], double[]>> {
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
}
