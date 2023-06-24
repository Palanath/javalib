package pala.libs.generic.ml.ai.neuralnets.computations;

import java.util.Random;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

/**
 * A {@link Node} that introduces a bias to each of the inputs it receives. It
 * takes in as many elements as it outputs and it has the same number of weights
 * as well. Each weight is simply added to its respective input upon a forward
 * pass.
 * 
 * @author Palanath
 *
 */
public class ShiftNode extends Node {

	public ShiftNode(int size) {
		super(size, size, size);
	}

	public ShiftNode(Random weightRandomizer, int size) {
		super(weightRandomizer, size, size, size);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		// Addition gates duplicate the gradient backwards to every input. In this case,
		// the two inputs to each addition gate are the weight for that gate and the
		// input received for that gate. The output is the result of the gate.
		//
		// 1. Receive the output gradient for gate-n in outGrad[n].
		// 2. Store it for weight[n].
		// 3. Return it for input[n].
		weightStorage.put(this, outGrad);
		return outGrad;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = input.clone();
		for (int i = 0; i < input.length; i++)
			res[i] += weights[i];
		return res;
	}

}
