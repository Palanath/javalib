package pala.libs.generic.javafx.bindings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pala.libs.generic.JavaTools;
import pala.libs.generic.QuickList;
import pala.libs.generic.util.Gateway;

public final class BindingTools {

	/**
	 * Keeps two {@link List}s matched, including item index.
	 * 
	 * @param <F>       The type of the list to listen to changes on.
	 * @param <T>       The type of the list to apply changes to (the list that gets
	 *                  "bound" to the source/from list).
	 * @param from      The list to listen to changes on.
	 * @param to        The list to apply changes to (the list that gets "bound" to
	 *                  the source/from list).
	 * @param converter A {@link Function} used to convert objects of the source
	 *                  {@link List}'s type to objects that get put in the
	 *                  destination {@link List}.
	 * @return An {@link Unbindable} that, when {@link Unbindable#unbind() invoked},
	 *         unbinds the destination list from the source list.
	 */
	public static <F, T> Unbindable bind(ObservableList<? extends F> from, List<? super T> to,
			Function<? super F, ? extends T> converter) {
		ListChangeListener<F> listener = c -> {
			while (c.next())
				if (c.wasPermutated()) {
					to.subList(c.getFrom(), c.getTo()).clear();
					to.addAll(c.getFrom(), JavaTools.addAll(c.getList().subList(c.getFrom(), c.getTo()), converter,
							new ArrayList<>(c.getTo() - c.getFrom())));
				} else {
					if (c.wasRemoved())
						to.subList(c.getFrom(), c.getFrom() + c.getRemovedSize()).clear();
					if (c.wasAdded())
						to.addAll(c.getFrom(),
								JavaTools.addAll(c.getAddedSubList(), converter, new ArrayList<>(c.getAddedSize())));
				}
		};
		from.addListener(listener);
		return () -> from.removeListener(listener);
	}

	public static final class FilterBinding<T> {

		private final ObservableList<? extends T> container;
		private final List<T> glass;
		private final Collection<Function<? super T, Boolean>> filters;
		private final ListChangeListener<T> listener;

		@SafeVarargs
		public FilterBinding(final ObservableList<? extends T> container, final List<T> glass,
				final Function<? super T, Boolean>... filters) {
			this.glass = glass;
			this.filters = new QuickList<>(filters);
			synchronized (this) {
				glass.clear();
				N: for (final T t : container) {
					for (final Function<? super T, Boolean> f : filters)
						if (!f.apply(t))
							continue N;
					glass.add(t);
				}
				(this.container = container).addListener(listener = (ListChangeListener<T>) c -> {
					while (c.next())
						if (c.wasAdded())
							NEXT_ITEM: for (final T t1 : c.getAddedSubList())
								synchronized (this) {
									for (final Function<? super T, Boolean> f1 : this.filters)
										if (!f1.apply(t1))
											continue NEXT_ITEM;
									glass.add(t1);
								}
						else if (c.wasRemoved())
//							NEXT_ITEM:
							for (final T t2 : c.getRemoved())
								synchronized (this) {
//									if (FilterBinding.this.filters.size() < glass.size()) {
//									for (Function<? super T, Boolean> f2 : this.filters)
//										if (!f2.apply(t2))
//											continue NEXT_ITEM;
//									// The above assumes that items can't change.
									glass.remove(t2);
//									} else
//										glass.remove(t);
								}
				});
			}

		}

		public synchronized void addFilter(final Function<? super T, Boolean> filter) {
			filters.add(filter);
			for (final Iterator<T> iterator = glass.iterator(); iterator.hasNext();)
				if (!filter.apply(iterator.next()))
					iterator.remove();
		}

		public synchronized void bind() {
			glass.clear();
			N: for (final T t : container) {
				for (final Function<? super T, Boolean> f : filters)
					if (!f.apply(t))
						continue N;
				glass.add(t);
			}
			container.addListener(listener);
		}

		public synchronized void refresh() {
			glass.clear();
			NEXT_ITEM: for (final T t : container) {
				for (final Function<? super T, Boolean> f : filters)
					if (!f.apply(t))
						continue NEXT_ITEM;
				glass.add(t);
			}
		}

		public synchronized void removeFilter(final Function<? super T, Boolean> filter) {
			filters.remove(filter);

			NEXT_ITEM: for (final T t : container)
				if (!filter.apply(t)) {
					for (final Function<? super T, Boolean> f : filters)
						if (!f.apply(t))
							continue NEXT_ITEM;
					glass.add(t);
				}
		}

		public synchronized void unbind() {
			container.removeListener(listener);
		}

	}

	public static final class PipewayBinding<F, T, X1 extends ObservableValue<? extends F> & WritableValue<? super F>, X2 extends ObservableValue<? extends T> & WritableValue<? super T>> {
		private final WeakReference<X1> from;
		private final WeakReference<X2> to;
		private final Gateway<F, T> converter;
		private boolean updating;

		private final ChangeListener<F> fromListener = new ChangeListener<F>() {

			@Override
			public void changed(final ObservableValue<? extends F> observable, final F oldValue, final F newValue) {
				if (updating)
					return;
				final X1 f = from.get();
				final X2 t = to.get();

				if (t == null)
					f.removeListener(this);
				else
					try {
						updating = true;
						T val;
						try {
							val = converter.to(newValue);
						} catch (final RuntimeException e) {
							errHandler.accept(e);
							return;
						}
						t.setValue(val);
					} finally {
						updating = false;
					}
			}
		};

		private final ChangeListener<T> toListener = new ChangeListener<T>() {

			@Override
			public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {
				if (updating)
					return;
				final X1 f = from.get();
				final X2 t = to.get();

				if (f == null)
					t.removeListener(this);
				else
					try {
						updating = true;
						F val;
						try {
							val = converter.from(newValue);
						} catch (final RuntimeException e) {
							errHandler.accept(e);
							return;
						}
						f.setValue(val);
					} finally {
						updating = false;
					}
			}
		};

		private final Consumer<? super RuntimeException> errHandler;

		public PipewayBinding(final X1 prop1, final X2 prop2, final Gateway<F, T> converter) {
			this(prop1, prop2, converter, a -> {
			});
		}

		public PipewayBinding(final X1 prop1, final X2 prop2, final Gateway<F, T> converter,
				final Consumer<? super RuntimeException> errHandler) {
			this.from = new WeakReference<>(prop1);
			this.to = new WeakReference<>(prop2);
			this.converter = converter;
			prop1.addListener(fromListener);
			prop2.addListener(toListener);
			this.errHandler = errHandler;
		}

		public void unbind() {
			final X1 f = from.get();
			if (f != null)
				f.removeListener(fromListener);
			final X2 t = to.get();
			if (t != null)
				t.removeListener(toListener);
		}

	}

	public interface Unbindable extends Runnable {
		void unbind();

		@Override
		default void run() {
			unbind();
		}
	}

	public static <T> Unbindable bind(final Collection<? super T> list, final ObservableListView<? extends T> view) {
		final ListListener<T> listener = new ListListener<T>() {

			@Override
			public void added(final List<? extends T> items, final int startpos) {
				list.addAll(items);
			}

			@Override
			public void removed(final List<? extends T> items, final int startpos) {
				for (final T t : items)
					list.remove(t);
			}
		};
		view.addListener(listener);
		return () -> view.removeListener(listener);
	}

	public static <F, T> void bind(final ObservableValue<F> from, final Function<? super F, ? extends T> converter,
			final Property<T> to) {
		to.bind(Bindings.createObjectBinding(() -> converter.apply(from.getValue()), from));
	}

	/**
	 * Creates a bidirectional binding between the two given observable & writable
	 * values. Whenever one property is changed externally, the new value of that
	 * property is passed to the given {@link Gateway}. The result is then assigned
	 * to the other property. If an invocation of the {@link Gateway} throws a
	 * {@link RuntimeException}, the change is ignored. (This is useful if you want
	 * a property only to update when its pairly bound property is changed to a
	 * value that can be converted to something assignable to the former property).
	 * The resulting {@link PipewayBinding} accesses its observable & writable value
	 * through {@link WeakReference}s.
	 *
	 * @param <F>     The type of value that the first observable & writable value
	 *                object can hold.
	 * @param <T>     The type of value that the second observable & writable value
	 *                object can hold.
	 * @param <X1>    The type of the first observable & writable value object.
	 * @param <X2>    The type of the second observable & writable value object.
	 * @param from    The first observable & writable value.
	 * @param gateway The converter.
	 * @param to      The second observable & writable value.
	 * @return A {@link PipewayBinding} that can be used to unbind the properties.
	 */
	public static <F, T, X1 extends ObservableValue<? extends F> & WritableValue<? super F>, X2 extends ObservableValue<? extends T> & WritableValue<? super T>> PipewayBinding<F, T, X1, X2> bindBidirectional(
			final X1 from, final Gateway<F, T> gateway, final X2 to) {
		return new PipewayBinding<>(from, to, gateway);
	}

	/**
	 * Returns an {@link ObservableListView} which propagates the changes in the
	 * specified {@link ObservableListView} that pass the given filters. <b>Changes
	 * propagated past this filter will be be broken, in terms of position. I.e.,
	 * the positions of changes propagated from this view should not be expected to
	 * be accurate.</b>
	 *
	 * @param <T>     The type of the object of the list that gets changed.
	 * @param view    The view to filter changes from.
	 * @param filters The filters.
	 * @return A new {@link ObservableListView}.
	 */
	@SafeVarargs
	public static <T> ObservableListView<T> filter(final ObservableListView<? extends T> view,
			final Function<? super T, Boolean>... filters) {
		return new ObservableListView<T>() {
			{
				view.addListener(new ListListener<T>() {

					@Override
					public void added(final List<? extends T> items, final int startpos) {
						final List<T> prop = new ArrayList<>(items.size());
						NEXT_ITEM: for (final T t : items) {
							for (final Function<? super T, Boolean> f : filters)
								if (!f.apply(t))
									continue NEXT_ITEM;
							prop.add(t);
						}

						propAdd(prop, -1);
					}

					@Override
					public void removed(final List<? extends T> items, final int startpos) {
						final List<T> prop = new ArrayList<>(items.size());
						NEXT_ITEM: for (final T t : items) {
							for (final Function<? super T, Boolean> f : filters)
								if (!f.apply(t))
									continue NEXT_ITEM;
							prop.add(t);
						}

						propRem(prop, -1);
					}
				});
			}
		};
	}

	/**
	 * Creates a filtered binding between the two given {@link List}s. Each change
	 * in the <code>container</code> list is applied through each given
	 * <code>filter</code>. If any filter returns <code>false</code> when given the
	 * item involved in a change, the change is ignored (i.e. the
	 * <code>boundContainer</code> is not affected by the change observed in the
	 * container). If the change is an addition, each filter must return
	 * <code>true</code> when given the object being added for the addition to be
	 * reflected in the <code>boundContainer</code>. If the change is a removal,
	 * each filter must still return <code>true</code> for the removal to be
	 * reflected in the <code>boundContainer</code>. Changes are observed happening
	 * in the <code>container</code> and are reflected (if the filters allow) in the
	 * <code>boundContainer</code>.
	 *
	 * @param <T>            The type of the content of the bound list.
	 * @param container      The {@link List} to observe.
	 * @param boundContainer The {@link List} to apply observed changes that
	 *                       successfully pass through the filters, to.
	 * @param filters        The filters that allow a change to be propagated to the
	 *                       bound list.
	 * @return A {@link FilterBinding} object which can be used to modify the
	 *         filters involved in the binding or to unbind/rebind the lists at
	 *         will.
	 */
	@SafeVarargs
	public static <T> FilterBinding<T> filterBind(final ObservableList<? extends T> container,
			final List<T> boundContainer, final Function<? super T, Boolean>... filters) {
		return new FilterBinding<>(container, boundContainer, filters);
	}

	/**
	 * This function is effectively equivalent to
	 * {@link #filterBind(ObservableList, List, Function...)} with the exception
	 * that filters which can be passed into this function but not into
	 * {@link #filterBind(ObservableList, List, Function...)} can cause exceptional
	 * behavior when the <code>boundContainer</code> is being <i>strained</i>. The
	 * bound container may sometimes need to have elements—that were added to it by
	 * means other than this filtered binding—removed. In these cases, the removed
	 * elements will be passed through the provided filters to assure that the
	 * removal change should be propagated to the <code>boundContainer</code>, but
	 * if any of these elements does not fit in one of the specified filters, a
	 * {@link ClassCastException} will be thrown.
	 *
	 * @param <T1>           The type of value of the observed list.
	 * @param <T2>           The type of value of the bound list.
	 * @param container      The container to observe.
	 * @param boundContainer The container that changes will be propagated to.
	 * @param filters        The filters that must be able to accept items which are
	 *                       a supertype of <i>only the observed list</i>
	 *                       (<code>container</code>). It is expected that the
	 *                       <code>boundContainer</code> is not modified except
	 *                       through the active filter when this filter is active
	 *                       and that the list is empty to begin with to assure that
	 *                       all of these filters are only ever provided a subset of
	 *                       the values contained in the observed list.
	 * @return A {@link FilterBinding} representing the binding.
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T1, T2 extends T1> FilterBinding<T1> filterBind1(final ObservableList<T2> container,
			final List<T1> boundContainer, final Function<? super T2, Boolean>... filters) {
		return filterBind(container, boundContainer, (Function<? super T1, Boolean>[]) filters);
	}

	public static <F, T> ObservableValue<T> mask(final ObservableValue<F> prop,
			final Function<? super F, ? extends T> converter) {
		return new ObservableValue<T>() {

			class IncrementetiveChangeListener implements ChangeListener<F> {

				private int count;

				private final ChangeListener<? super T> backing;

				public IncrementetiveChangeListener(final ChangeListener<? super T> backing) {
					this.backing = backing;
				}

				@Override
				public void changed(final ObservableValue<? extends F> observable, final F oldValue, final F newValue) {
					for (int i = 0; i < count; i++)
						backing.changed(val, converter.apply(oldValue), converter.apply(newValue));
				}

			}

			private final ObservableValue<T> val = this;

			Map<ChangeListener<? super T>, IncrementetiveChangeListener> listeners = new HashMap<>();

			@Override
			public void addListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					listeners.get(listener).count++;
				else
					listeners.put(listener, new IncrementetiveChangeListener(listener));
			}

			@Override
			public void addListener(final InvalidationListener listener) {
				prop.addListener(listener);
			}

			@Override
			public T getValue() {
				return converter.apply(prop.getValue());
			}

			@Override
			public void removeListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					if (listeners.get(listener).count-- < 0)
						listeners.remove(listener);
			}

			@Override
			public void removeListener(final InvalidationListener listener) {
				prop.removeListener(listener);
			}
		};
	}

	public static <F, T> Property<T> mask(final Property<F> prop, final Gateway<F, T> gateway) {
		return new Property<T>() {
			class IncrementetiveChangeListener implements ChangeListener<F> {

				private int count;

				private final ChangeListener<? super T> backing;

				public IncrementetiveChangeListener(final ChangeListener<? super T> backing) {
					this.backing = backing;
				}

				@Override
				public void changed(final ObservableValue<? extends F> observable, final F oldValue, final F newValue) {
					for (int i = 0; i < count; i++)
						backing.changed(val, gateway.to(oldValue), gateway.to(newValue));
				}

			}

			class IntermediaryValue implements ObservableValue<T> {
				private final List<InvalidationListener> ils = new ArrayList<>();
				private final List<ChangeListener<? super T>> cls = new ArrayList<>();
				private T value;

				@Override
				public void addListener(final ChangeListener<? super T> listener) {
					cls.add(listener);
				}

				@Override
				public void addListener(final InvalidationListener listener) {
					ils.add(listener);
				}

				public void changed(final T oldVal) {
					for (final InvalidationListener il : ils)
						il.invalidated(this);
					for (final ChangeListener<? super T> cl : cls)
						cl.changed(this, oldVal, value);
				}

				@Override
				public T getValue() {
					return value;
				}

				@Override
				public void removeListener(final ChangeListener<? super T> listener) {
					cls.remove(listener);
				}

				@Override
				public void removeListener(final InvalidationListener listener) {
					ils.remove(listener);
				}
			}

			private final Property<T> val = this;

			Map<ChangeListener<? super T>, IncrementetiveChangeListener> listeners = new WeakHashMap<>();

			private final IntermediaryValue intermediary = new IntermediaryValue();

			ObservableValue<F> intMask = mask((ObservableValue<T>) intermediary, gateway.to());

			private final ChangeListener<T> listener = (observable, oldValue, newValue) -> {
				final T oldVal = intermediary.value;
				intermediary.value = newValue;
				intermediary.changed(oldVal);
			};

			private ObservableValue<? extends T> currentObserver;

			private final Map<Property<T>, PipewayBinding<?, ?, ?, ?>> bibinds = new HashMap<>();

			@Override
			public void addListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					listeners.get(listener).count++;
				else
					listeners.put(listener, new IncrementetiveChangeListener(listener));
			}

			@Override
			public void addListener(final InvalidationListener arg0) {
				prop.addListener(arg0);
			}

			@Override
			public void bind(final ObservableValue<? extends T> observable) {
				if (currentObserver != null)
					unbind();
				intermediary.value = observable.getValue();
				prop.bind(intMask);
				(currentObserver = observable).addListener(listener);
			}

			@Override
			public void bindBidirectional(final Property<T> other) {
				BindingTools.bindBidirectional(prop, gateway, other);
			}

			@Override
			public Object getBean() {
				return prop.getBean();
			}

			@Override
			public String getName() {
				return prop.getName();
			}

			@Override
			public T getValue() {
				return gateway.to(prop.getValue());
			}

			@Override
			public boolean isBound() {
				return prop.isBound();
			}

			@Override
			public void removeListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					if (listeners.get(listener).count-- < 0)
						listeners.remove(listener);
			}

			@Override
			public void removeListener(final InvalidationListener arg0) {
				prop.removeListener(arg0);
			}

			@Override
			public void setValue(final T arg0) {
				prop.setValue(gateway.from(arg0));
			}

			@Override
			public void unbind() {
				prop.unbind();
				currentObserver.removeListener(listener);
			}

			@Override
			public void unbindBidirectional(final Property<T> other) {
				if (bibinds.containsKey(other))
					bibinds.remove(other).unbind();
			}
		};
	}

	public static <F, T, O extends Observable> ObservableValue<T> toObservableValue(final O obs,
			final Function<O, T> valueSupplier) {
		return new ObservableValue<T>() {

			class Listener implements InvalidationListener {
				private int count;
				private final ChangeListener<? super T> backing;
				private T currentValue = valueSupplier.apply(obs);

				public Listener(final ChangeListener<? super T> backing) {
					this.backing = backing;
				}

				@Override
				public void invalidated(final Observable observable) {
					final T nv = valueSupplier.apply(obs);
					if (currentValue != nv)
						for (int i = 0; i < count; i++)
							backing.changed(inst, currentValue, nv);
					currentValue = nv;

				}

			}

			private final Map<ChangeListener<? super T>, Listener> listeners = new HashMap<>();

			private final ObservableValue<T> inst = this;

			@Override
			public void addListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					listeners.get(listener).count++;
				else
					listeners.put(listener, new Listener(listener));
			}

			@Override
			public void addListener(final InvalidationListener listener) {
				obs.addListener(listener);
			}

			@Override
			public T getValue() {
				return valueSupplier.apply(obs);
			}

			@Override
			public void removeListener(final ChangeListener<? super T> listener) {
				if (listeners.containsKey(listener))
					if (listeners.get(listener).count-- < 0)
						listeners.remove(listener);
			}

			@Override
			public void removeListener(final InvalidationListener listener) {
				obs.removeListener(listener);
			}
		};
	}

	public static <T> ObservableListView<T> view(final ObservableList<? extends T> list) {
		return new ObservableListView<>(list);
	}

	public static <F, T> ObservableListView<T> view(final ObservableListView<? extends F> view,
			final Function<F, T> converter) {
		return new ObservableListView<T>() {
			{
				view.addListener(new ListListener<F>() {

					@Override
					public void added(final List<? extends F> items, final int startpos) {
						final List<T> list = new ArrayList<>(items.size());
						for (final F f : items)
							list.add(converter.apply(f));
						propAdd(list, startpos);

					}

					@Override
					public void removed(final List<? extends F> items, final int startpos) {
						final List<T> list = new ArrayList<>(items.size());
						for (final F f : items)
							list.add(converter.apply(f));
						propRem(list, startpos);
					}
				});
			}
		};
	}

	public static <T> ObservableListView<T> view(final ObservableListView<T> view) {
		return new ObservableListView<>(view);
	}

	private BindingTools() {
	}

}
