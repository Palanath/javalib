package pala.libs.generic.ml.mdp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pala.libs.generic.JavaTools;

/**
 * <p>
 * Represents a nondeterministic transition function for a Markov Decision
 * Process. This class stores a set of transitions and probabilities.
 * </p>
 * <p>
 * Each transition is a triplet containing a <code>fromState</code>, an
 * <code>action</code>, and a <code>toState</code>. This class stores, and
 * allows obtaining the probability that taking a certain <code>action</code> in
 * a certain <code>fromState</code> will result in a certain
 * <code>toState</code>. It also allows one to recover the full set of states,
 * and their transition probabilities that can be transitioned to by taking an
 * <code>action</code> while in a <code>fromState</code>. (This does not return
 * the full set of available states that can be transitioned to; just the ones
 * with non-zero probability.)
 * </p>
 * 
 * @author Palanath
 *
 * @param <S>
 * @param <A>
 */
public final class TransitionFunction<S, A> {

	private TransitionFunction(Map<S, Map<A, Map<S, Double>>> jumps) {
		this.jumps = jumps;
	}

	public static final class Builder<S, A> {
		private final Map<S, Map<A, Transition<S>>> jumps = new HashMap<>();

		public void putProb(S fromState, A action, S toState, double prob) {
			if (prob > 1 || prob < 0)
				throw new IllegalArgumentException("Provided probability is out of bounds.");
			JavaTools.putIntoDoubleMap(jumps, fromState, action, new Transition<>(toState, prob));
		}
	}

	/**
	 * Returns the probability that taking action <code>action</code> while in state
	 * <code>fromState</code> will result in transitioning into
	 * <code>toState</code>.
	 * 
	 * @param fromState The initial state.
	 * @param action    The action to take.
	 * @param toState   The transitioned state.
	 * @return The probability that this specific transition occurs.
	 */
	public double getProb(S fromState, A action, S toState) {
		Map<A, Map<S, Double>> m1 = jumps.get(fromState);
		Map<S, Double> m2;
		return m1 != null && (m2 = m1.get(action)) != null && m2.containsKey(toState) ? m2.get(toState) : 0;
	}

	/**
	 * Gets a {@link Transition} object representing the transition from the
	 * provided <code>fromState</code> with the specified <code>action</code> being
	 * taken, to the provided <code>toState</code>.
	 * 
	 * @param fromState The state to transition from.
	 * @param action    The action to take.
	 * @param toState   The state to transition to.
	 * @return A {@link Transition} containing the probability of the transition.
	 */
	public Transition<S> getTransition(S fromState, A action, S toState) {
		return new Transition<>(toState, getProb(fromState, action, toState));
	}

	private final Map<S, Map<A, Map<S, Double>>> jumps;

	public List<Transition<S>> getTransitions(S fromState, A action) {
		Map<A, Map<S, Double>> m1 = jumps.get(fromState);
		Map<S, Double> m2;
		if (m1 != null)
			if ((m2 = m1.get(action)) != null) {
				List<Transition<S>> transitions = new ArrayList<>();
				for (Entry<S, Double> e : m2.entrySet())
					transitions.add(new Transition<>(e.getKey(), e.getValue()));
				return transitions;
			}
		return new ArrayList<>();
	}

	public final static class Transition<S> {
		private final S resultingState;
		private final double prob;

		private Transition(S resultingState, double prob) {
			this.resultingState = resultingState;
			this.prob = prob;
		}

		public S getResultingState() {
			return resultingState;
		}

		public double getProb() {
			return prob;
		}
	}
}