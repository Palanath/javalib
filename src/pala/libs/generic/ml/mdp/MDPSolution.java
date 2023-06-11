package pala.libs.generic.ml.mdp;

import java.util.Map;

public final class MDPSolution<S, A> {
	private final Map<S, Double> valueFunction;
	private final Map<S, A> policy;

	public Map<S, Double> getValueFunction() {
		return valueFunction;
	}

	public Map<S, A> getPolicy() {
		return policy;
	}

	public MDPSolution(Map<S, Double> valueFunction, Map<S, A> policy) {
		this.valueFunction = valueFunction;
		this.policy = policy;
	}

	public double value(S state) {
		return valueFunction.get(state);
	}
}