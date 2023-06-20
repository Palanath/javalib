package pala.libs.generic.ml.ai.neuralnet4;

/**
 * A {@link Node} with only one output.
 * 
 * @author Palanath
 *
 */
public interface SimpleNode extends Node {
	@Override
	default int outputs() {
		return 1;
	}
}
