package pala.libs.generic.ml.ai.neuralnets.api;

public interface Snapshottable {
	void save(Snapshot snapshot);

	void restore(Snapshot snapshot);
}
