package pala.libs.generic.ml.ai.neuralnets.computations;

public class MapComputation implements GenericMapComputation {

	private final int inputs, mapping[];

	public MapComputation(int inputs, int... mapping) {
		this.inputs = inputs;
		this.mapping = mapping;
	}

	@Override
	public int outputs() {
		return mapping.length;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public int map(int output) {
		return mapping[output];
	}

}
