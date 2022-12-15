package pala.libs.generic.taskloader;

import java.util.Collections;
import java.util.Stack;

public class TaskLoader<T> {
	private final Stack<T> tasks = new Stack<>();

	private final TaskHandler<T> handler;

	private boolean suppressWarnings = true;

	private String name;

	private Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				while (!tasks.isEmpty())
					handler.handle(tasks.pop());
			} catch (final Exception e) {
				if (!suppressWarnings)
					return;
				if (name != null)
					System.err.print(name + ": ");
				e.printStackTrace();
			}
			thread = new Thread(this);
		}
	});

	@SafeVarargs
	public TaskLoader(final TaskHandler<T> handler, final T... tasks) {
		this.handler = handler;
		addTasks(tasks);
	}

	public final void addTask(final T t) {
		tasks.add(t);
		start();
	}

	@SafeVarargs
	public final void addTasks(final T... tasks) {
		Collections.addAll(this.tasks, tasks);
		start();
	}

	public String getName() {
		return name;
	}

	public boolean isSuppressWarnings() {
		return suppressWarnings;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSuppressWarnings(final boolean suppressWarnings) {
		this.suppressWarnings = suppressWarnings;
	}

	private void start() {
		if (!thread.isAlive())
			thread.start();
	}
}
