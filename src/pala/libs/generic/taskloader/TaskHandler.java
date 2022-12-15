package pala.libs.generic.taskloader;

public interface TaskHandler<T> {
	void handle(T task);
}
