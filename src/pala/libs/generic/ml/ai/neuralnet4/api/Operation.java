package pala.libs.generic.ml.ai.neuralnet4.api;

import pala.libs.generic.ml.ai.neuralnet4.Snapshot;

public interface Operation extends Computation {
	@Override
	default void restore(Snapshot snapshot) {
	}

	@Override
	default void snapshot(Snapshot snapshot) {
	}
}
