package pala.libs.generic.ml.ai.neuralnet4;

public interface Container {
	void set(Object v);

	<O> O get();
}
