package pala.libs.generic.ml.ai.neuralnet4;

/**
 * A {@link Node} with only one output, but (generally) more than one input.
 * 
 * @author Palanath
 *
 */
public interface VectorNode extends SimpleNode {
	@Override
	default int outputs() {
		return 1;
	}
}
