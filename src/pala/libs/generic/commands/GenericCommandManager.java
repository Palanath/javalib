package pala.libs.generic.commands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import pala.libs.generic.JavaTools;

/**
 * <p>
 * A {@link GenericCommandManager} is an object that accepts input (of the type
 * specified via the type parameter, <code>D</code>), and "handles" it via
 * registered objects. These registered objects can be of three types:
 * {@link OptionalGenericCommandConsumer}s, {@link GenericCommandConsumer}s, or
 * {@link GenericCommand}s.
 * </p>
 * <p>
 * When handling a piece of data, each {@link OptionalGenericCommandConsumer} is
 * checked to see if it will accept the data object. If it does, the encountered
 * reference to the optional consumer is deleted from this manager, and the
 * optional consumer is fired. The given piece of data is considered handled,
 * and this manager waits until it receives more data. If no optional consumer
 * accepted the data, then the <i>last</i> {@link GenericCommandConsumer} to be
 * added to this manager is given the data, if any, after which the data is
 * considered handled. If no regular consumer is registered with this manager,
 * each registered {@link GenericCommand} is checked, in the order that they
 * were added, (with the first-added command being checked first), to see if a
 * call to the command's {@link GenericCommand#match(Object)} method, given the
 * data, returns <code>true</code>. If it does, then the data is considered
 * handled.
 * </p>
 * <p>
 * Both types of consumers are unregistered when they are given a piece of data.
 * Commands are the only "persistent" data handler, in that they remain extant
 * after they are given data to handle.
 * </p>
 * <p>
 * The abnormality with optional consumers is that they are checked to
 * <i>match</i> pending data, before being given it. On the other hand, any
 * regular consumer is simply <i>given</i> the next piece of data, if a regular
 * consumer is registered.
 * </p>
 * <p>
 * <b>Note: Optional consumers can be registered as regular ones, and will
 * behave as regular ones, if they are given to the
 * {@link #addConsumer(GenericCommandConsumer)} method, instead of the
 * {@link #addConsumer(OptionalGenericCommandConsumer)} method.</b>
 * </p>
 *
 * @author Palanath
 *
 * @param <D> The type of the data that this manager will accept via its
 *            {@link #run(Object)} method.
 */
public class GenericCommandManager<D> implements AbstractCommandManager<D> {
	private final List<GenericCommand<? super D>> commands = new LinkedList<>();
	private final Stack<GenericCommandConsumer<? super D>> consumers = new Stack<>();
	private final Stack<OptionalGenericCommandConsumer<? super D>> optionalConsumers = new Stack<>();

	protected final List<GenericCommand<? super D>> commandView = Collections.unmodifiableList(commands);
	protected final Stack<GenericCommandConsumer<? super D>> consumerView = JavaTools.unmodifiableStack(consumers);
	protected final Stack<OptionalGenericCommandConsumer<? super D>> optionalConsumerView = JavaTools
			.unmodifiableStack(optionalConsumers);

	@Override
	public void addCommand(final GenericCommand<? super D> command) {
		if (!commands.contains(command))
			commands.add(command);
	}

	@Override
	public void addConsumer(final GenericCommandConsumer<? super D> consumer) {
		consumers.push(consumer);
	}

	@Override
	public void addOptionalConsumer(final OptionalGenericCommandConsumer<? super D> consumer) {
		optionalConsumers.push(consumer);
	}

	@Override
	public void removeCommand(final GenericCommand<? super D> command) {
		commands.remove(command);
	}

	@Override
	public void removeConsumer(final GenericCommandConsumer<? super D> consumer) {
		consumers.remove(consumer);
	}

	@Override
	public void removeOptionalConsumer(final OptionalGenericCommandConsumer<? super D> consumer) {
		optionalConsumers.remove(consumer);
	}

	@Override
	public boolean run(final D data) {

		// Optional consumers are checked,
		while (!optionalConsumers.isEmpty())
			if (optionalConsumers.peek().act(data)) {
				optionalConsumers.pop();
				return true;
			}

		// then regular consumers are checked,
		if (!consumers.isEmpty()) {
			consumers.pop().act(data);
			return true;
		}

		// then commands are checked.
		for (final GenericCommand<? super D> gc : commands)
			if (gc.match(data)) {
				gc.act(data);
				return true;
			}
		return false;
	}
}
