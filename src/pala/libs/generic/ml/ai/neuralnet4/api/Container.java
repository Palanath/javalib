package pala.libs.generic.ml.ai.neuralnet4.api;

public interface Container {
	void set(Object v);

	<O> O get();

	Container DUMMY = new Container() {

		@Override
		public void set(Object v) {
		}

		@Override
		public <O> O get() {
			return null;
		}
	};
}
