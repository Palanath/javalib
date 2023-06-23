package pala.libs.generic.ml.ai.neuralnet4.computations;

import java.util.List;

import pala.libs.generic.ml.ai.neuralnet4.Snapshot;
import pala.libs.generic.ml.ai.neuralnet4.api.Computation;

/**
 * A supertype of any {@link Computation} that utilizes other
 * {@link Computation}s within it. This is primarily used for the
 * {@link Snapshot} API, to allow
 * 
 * @author Palanath
 *
 */
public interface CompositeComputation extends Computation {
	List<? extends Computation> getSubComputations();

}
