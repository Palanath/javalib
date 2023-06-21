package pala.libs.generic.ml.ai.neuralnet4;

/**
 * A {@link Computation} with only one output, but (generally) more than one
 * input.
 * 
 * @author Palanath
 *
 */
public interface VectorNode extends Computation {
	@Override
	default int outputs() {
		return 1;
	}
}
