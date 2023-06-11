package pala.libs.generic.ml.mdp;

import java.util.HashMap;
import java.util.Map;

import pala.libs.generic.JavaTools;

public final class TransitionFunction<S, A> {

	private TransitionFunction(Map<S, Map<A, TransitionFunction.Transition<S>>> jumps) {
		this.jumps = jumps;
	}

	public static final class Builder<S, A> {
		private final Map<S, Map<A, TransitionFunction.Transition<S>>> jumps = new HashMap<>();

		public void putProb(S fromState, A action, S toState, double prob) {
			JavaTools.putIntoDoubleMap(jumps, fromState, action, new TransitionFunction.Transition<S>(toState, prob));
		}
	}

	private final Map<S, Map<A, TransitionFunction.Transition<S>>> jumps;

	private static class Transition<S> {
		private final S resultingState;
		private final double prob;

		public Transition(S resultingState, double prob) {
			this.resultingState = resultingState;
			this.prob = prob;
		}
	}
}