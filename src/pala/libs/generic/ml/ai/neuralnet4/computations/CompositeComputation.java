package pala.libs.generic.ml.ai.neuralnet4.computations;

import java.util.List;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;

public interface CompositeComputation extends Computation {
	List<? extends Computation> getSubComputations();
}
