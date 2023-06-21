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
	double[] EMPTY_DOUBLE_ARRAY = {};

	@Override
	default double[] weights() {
		return EMPTY_DOUBLE_ARRAY;
	}

	class IllegalOperationException extends RuntimeException {

		/**
		 * Serial UID
		 */
		private static final long serialVersionUID = 1L;

		private IllegalOperationException(String message) {
			super(message);
		}

	}

	@Override
	default double getWeight(int weight) {
		throw new IllegalOperationException(
				"Can't get weights from a SimpleNode; SimpleNodes have no weights. Weight ind: " + weight);
	}

	@Override
	default void setWeight(int weight, double value) {
		throw new IllegalOperationException("Can't set weights of a SimpleNode; SimpleNodes have no weights. Index: "
				+ weight + " Value: " + value);
	}
}
