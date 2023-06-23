package pala.libs.generic.ml.ai.neuralnet4.api;

public interface Snapshottable {
	void save(Snapshot snapshot);

	void restore(Snapshot snapshot);
}
