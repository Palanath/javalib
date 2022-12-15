package pala.libs.generic.javafx.bindings;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class ObservableListView<T> implements ListListener<T> {

	private final List<ListListener<? super T>> listeners = new ArrayList<>();

	ObservableListView() {
	}

	public ObservableListView(final ObservableList<? extends T> list) {
		list.addListener(this);
	}

	public ObservableListView(final ObservableListView<? extends T> view) {
		view.addListener(this);
	}

	@Override
	public void added(final List<? extends T> items, final int startpos) {
		propAdd(items, startpos);
	}

	public void addListener(final ListListener<? super T> listener) {
		listeners.add(listener);
	}

	protected final List<ListListener<? super T>> getListeners() {
		return listeners;
	}

	protected final void propAdd(final List<? extends T> items, final int startpos) {
		for (final ListListener<? super T> ll : listeners)
			ll.added(items, startpos);
	}

	protected final void propRem(final List<? extends T> items, final int startpos) {
		for (final ListListener<? super T> ll : listeners)
			ll.removed(items, startpos);
	}

	@Override
	public void removed(final List<? extends T> items, final int startpos) {
		propRem(items, startpos);
	}

	public void removeListener(final ListListener<? super T> listener) {
		listeners.remove(listener);
	}

}
