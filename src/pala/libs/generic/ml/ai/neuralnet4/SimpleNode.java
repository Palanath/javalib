package pala.libs.generic.ml.ai.neuralnet4;

/**
 * A {@link SimpleNode} is a {@link Node} that has no state. It does not possess
 * weights and can be used multiple times at arbitrary points along a
 * computational graph.
 * 
 * @author Palanath
 *
 */
public interface SimpleNode extends Node {
	@Override
	default int weights() {
		return 0;
	}
}
