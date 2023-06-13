package pala.libs.generic.ml.mdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import pala.libs.generic.JavaTools;
import pala.libs.generic.util.Triplet;
import pala.libs.generic.util.functions.TriDoubleFunction;

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
 * @param <S> The type of the state.
 * @param <A> The type of the action.
 */
public final class TransitionFunction<S, A> implements TriDoubleFunction<S, A, S>, BiFunction<S, A, S> {

	public TransitionFunction(Map<S, Map<A, Map<S, Double>>> jumps) {
		this.transitions = jumps;
		for (Map<A, Map<S, Double>> v : jumps.values())
			for (Map<S, Double> v2 : v.values()) {
				double prob = 0;
				for (double d : v2.values())
					prob += d;
				for (Entry<S, Double> e : v2.entrySet())
					e.setValue(e.getValue() / prob);
			}

	}

	public static final class Builder<S, A> {
		private final Map<S, Map<A, Map<S, Double>>> transitions = new HashMap<>();

		public void putTransition(S fromState, A action, S toState, double prob) {
			if (prob > 1 || prob < 0)
				throw new IllegalArgumentException("Provided probability is out of bounds.");
			JavaTools.putIntoTripleMap(transitions, fromState, action, toState, prob);
		}

		@SafeVarargs
		public final void putTransitions(double prob, Triplet<? extends S, ? extends A, ? extends S>... transitions) {
			for (Triplet<? extends S, ? extends A, ? extends S> t : transitions)
				putTransition(t.first, t.second, t.third, prob);
		}

		public TransitionFunction<S, A> build() {
			return new TransitionFunction<>(transitions);
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
		Map<A, Map<S, Double>> m1 = transitions.get(fromState);
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

	private final Map<S, Map<A, Map<S, Double>>> transitions;

	public List<Transition<S>> getTransitions(S fromState, A action) {
		Map<A, Map<S, Double>> m1 = transitions.get(fromState);
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

	@Override
	public String toString() {
		return transitions.toString();
	}

	@Override
	public double run(S first, A second, S third) {
		return getProb(first, second, third);
	}

	/**
	 * Samples a transition from this {@link TransitionFunction} going from the
	 * provided <code>fromState</code> by taking the provided <code>action</code>.
	 * This method returns one of the possible states that can be arrived at by
	 * taking the provided action while in the provided states, with probability in
	 * accordance with the likelihood of arriving in the resulting state.
	 */
	@Override
	public S apply(S fromState, A action) {
		double r = Math.random();
		List<Transition<S>> list = getTransitions(fromState, action);
		for (Transition<S> tr : list)
			if ((r -= tr.prob) <= 0)
				return tr.resultingState;
		return list.get(list.size() - 1).resultingState;
	}
}