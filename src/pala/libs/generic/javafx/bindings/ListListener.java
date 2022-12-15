package pala.libs.generic.javafx.bindings;

import java.util.List;

import javafx.collections.ListChangeListener;

public interface ListListener<T> extends ListChangeListener<T> {
	void added(List<? extends T> items, int startpos);

	@Override
	default void onChanged(final Change<? extends T> c) {
		while (c.next())
			if (c.wasAdded())
				added(c.getAddedSubList(), c.getFrom());
			else if (c.wasRemoved())
				removed(c.getRemoved(), c.getFrom());
	}

	void removed(List<? extends T> items, int startpos);
}
