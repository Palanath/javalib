package pala.libs.generic.ml.ai.neuralnet4.api;

import java.util.HashMap;
import java.util.Map;

/**
 * An object used to store and save the weights for {@link Node}s in a network.
 * 
 * @author Palanath
 *
 */
public class Snapshot {
	private final Map<Node, double[]> weightMapping = new HashMap<>();

	public Map<Node, double[]> getWeightMapping() {
		return weightMapping;
	}
}
