package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.List;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.Snapshot;
import pala.libs.generic.ml.ai.neuralnets.api.Snapshottable;

/**
 * A supertype of any {@link Computation} that utilizes other
 * {@link Computation}s within it. This is primarily used for the
 * {@link Snapshot} API, to allow
 * 
 * @author Palanath
 *
 */
public interface CompositeComputation extends Computation, Snapshottable {
	List<? extends Computation> getSubComputations();

	@Override
	default void save(Snapshot snapshot) {
		for (Computation c : getSubComputations())
			if (c instanceof Snapshottable)
				((Snapshottable) c).save(snapshot);
	}

	@Override
	default void restore(Snapshot snapshot) {
		for (Computation c : getSubComputations())
			if (c instanceof Snapshottable)
				((Snapshottable) c).restore(snapshot);
	}
}
