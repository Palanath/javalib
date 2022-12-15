package pala.libs.generic.javafx.bindings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ObservableList;
import pala.libs.generic.QuickList;

public class FilteredObservableListView<T> extends ObservableListView<T> {
	private final List<Function<? super T, Boolean>> filters;

	FilteredObservableListView() {
		filters = new ArrayList<>();
	}

	@SafeVarargs
	public FilteredObservableListView(final ObservableList<? extends T> list,
			final Function<? super T, Boolean>... filters) {
		super(list);
		this.filters = new QuickList<>(filters);
	}

	@SafeVarargs
	public FilteredObservableListView(final ObservableListView<? extends T> view,
			final Function<? super T, Boolean>... filters) {
		super(view);
		this.filters = new QuickList<>(filters);
	}

	@Override
	public synchronized void added(final List<? extends T> items, final int startpos) {
		super.added(filter(items), -1);
	}

	public void addFilter(final Function<? super T, Boolean> filter) {
		filters.add(filter);
	}

	private synchronized List<T> filter(final List<? extends T> items) {
		final List<T> prop = new ArrayList<>(items.size());
		NEXT_ITEM: for (final T t : items) {
			for (final Function<? super T, Boolean> f : filters)
				if (!f.apply(t))
					continue NEXT_ITEM;
			prop.add(t);
		}
		return prop;
	}

	@Override
	public void removed(final List<? extends T> items, final int startpos) {
		super.removed(filter(items), -1);
	}

	public synchronized void removeFilter(final Function<? super T, Boolean> filter) {
		filters.remove(filter);
	}

}
