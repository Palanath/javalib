package pala.libs.generic.commands;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import pala.libs.generic.JavaTools;

public class WeakReferencingGenericCommandManager<D> implements AbstractCommandManager<D> {

	protected static void updateIterable(final Iterable<WeakReference<?>> iterable) {
		for (final Iterator<WeakReference<?>> iterator = iterable.iterator(); iterator.hasNext();)
			if (iterator.next().get() == null)
				iterator.remove();
	}

	private final List<GenericCommand<? super D>> commands = new LinkedList<>();
	private final Stack<WeakReference<GenericCommandConsumer<? super D>>> consumers = new Stack<>();

	private final Stack<WeakReference<OptionalGenericCommandConsumer<? super D>>> optionalConsumers = new Stack<>();
	protected final List<GenericCommand<? super D>> commandView = Collections.unmodifiableList(commands);
	protected final Stack<WeakReference<GenericCommandConsumer<? super D>>> consumerView = JavaTools
			.unmodifiableStack(consumers);

	protected final Stack<WeakReference<OptionalGenericCommandConsumer<? super D>>> optionalConsumersView = JavaTools
			.unmodifiableStack(optionalConsumers);

	@Override
	public void addCommand(final GenericCommand<? super D> cmd) {
		commands.add(cmd);
	}

	@Override
	public void addConsumer(final GenericCommandConsumer<? super D> cnsm) {
		consumers.add(new WeakReference<>(cnsm));
	}

	@Override
	public void addOptionalConsumer(final OptionalGenericCommandConsumer<? super D> optnCnsm) {
		optionalConsumers.add(new WeakReference<>(optnCnsm));
	}

	protected boolean containsConsumer(final GenericCommandConsumer<? super D> cnsm) {
		for (final Iterator<WeakReference<GenericCommandConsumer<? super D>>> iterator = consumers.iterator(); iterator
				.hasNext();) {
			final Object refCnsm = iterator.next().get();
			if (refCnsm == null)
				iterator.remove();
			else if (cnsm == refCnsm)
				return true;
		}
		return false;
	}

	protected boolean containsOptionalConsumer(final OptionalGenericCommandConsumer<? super D> optnCnsm) {
		for (final Iterator<WeakReference<OptionalGenericCommandConsumer<? super D>>> iterator = optionalConsumers
				.iterator(); iterator.hasNext();) {
			final Object refOptnCnsm = iterator.next().get();
			if (refOptnCnsm == null)
				iterator.remove();
			else if (optnCnsm == refOptnCnsm)
				return true;
		}
		return false;
	}

	@Override
	public void removeCommand(final GenericCommand<? super D> cmd) {
		commands.remove(cmd);
	}

	@Override
	public void removeConsumer(final GenericCommandConsumer<? super D> cnsm) {
		for (final Iterator<WeakReference<GenericCommandConsumer<? super D>>> itr = consumers.iterator(); itr
				.hasNext();) {
			final Object item = itr.next().get();
			if (item == null || cnsm == item)
				itr.remove();
		}
	}

	@Override
	public void removeOptionalConsumer(final OptionalGenericCommandConsumer<? super D> optnCnsm) {
		for (final Iterator<WeakReference<OptionalGenericCommandConsumer<? super D>>> itr = optionalConsumers
				.iterator(); itr.hasNext();) {
			final Object item = itr.next().get();
			if (item == null || optnCnsm == item)
				itr.remove();
		}
	}

	@Override
	public boolean run(final D data) {

		if (!optionalConsumers.isEmpty())
			for (final Iterator<WeakReference<OptionalGenericCommandConsumer<? super D>>> iterator = optionalConsumers
					.iterator(); iterator.hasNext();) {
				final OptionalGenericCommandConsumer<? super D> optnCnsm = iterator.next().get();
				if (optnCnsm == null)
					iterator.remove();
				else if (optnCnsm.act(data)) {
					iterator.remove();
					return true;
				}
			}

		GenericCommandConsumer<? super D> cnsm = null;
		while (!consumers.isEmpty() && (cnsm = consumers.pop().get()) == null)
			;
		if (cnsm != null) {
			cnsm.act(data);
			return true;
		}

		for (final GenericCommand<? super D> cmd : commands)
			if (cmd.match(data)) {
				cmd.act(data);
				return true;
			}

		return false;
	}

}
