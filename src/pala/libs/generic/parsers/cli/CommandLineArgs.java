package pala.libs.generic.parsers.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import pala.libs.generic.JavaTools;

public class CommandLineArgs {
	protected final Map<String, List<ValueArgument>> valueArgs;
	protected final Map<String, List<KeyValueArgument>> keyValueArgs;

	public CommandLineArgs() {
		valueArgs = Collections.emptyMap();
		keyValueArgs = Collections.emptyMap();
	}

	public CommandLineArgs(final List<Argument> arguments) {
		valueArgs = new HashMap<>();
		keyValueArgs = new HashMap<>();

		for (final Argument a : arguments)
			if (a instanceof ValueArgument) {
				final ValueArgument arg = (ValueArgument) a;
				JavaTools.putIntoListMap(valueArgs, arg.name(), arg, ArrayList::new);
			} else if (a instanceof KeyValueArgument) {
				final KeyValueArgument arg = (KeyValueArgument) a;
				JavaTools.putIntoListMap(keyValueArgs, arg.key(), arg, ArrayList::new);
			} else
				throw new RuntimeException("Unknown argument type found.");
	}

	public boolean flag(final String name) {
		return valueArgs.containsKey(name);
	}

	public boolean flag(final String prefix, final String name) {
		if (!flag(name))
			return false;
		for (final ValueArgument v : valueArgs.get(name))
			if (prefix.equals(v.prefix()))
				return true;
		return false;
	}

	public String getKV(final String name) {
		if (!flag(name))
			return null;
		// Get value argument from end of list.
		final List<KeyValueArgument> values = keyValueArgs.get(name);
		final ListIterator<KeyValueArgument> itr = values.listIterator(values.size());
		while (itr.hasPrevious()) {
			final KeyValueArgument p = itr.previous();
			if (name.equals(p.name()))
				return p.value();
		}

		return null;
	}

	public KeyValueArgument getKVArg(final String name) {
		if (!flag(name))
			return null;
		// Get value argument from end of list.
		final List<KeyValueArgument> values = keyValueArgs.get(name);
		final ListIterator<KeyValueArgument> itr = values.listIterator(values.size());
		while (itr.hasPrevious()) {
			final KeyValueArgument p = itr.previous();
			if (name.equals(p.name()))
				return p;
		}

		return null;
	}

	public String getValue(final String name) {
		if (!flag(name))
			return null;
		// Get value argument from end of list.
		final List<ValueArgument> values = valueArgs.get(name);
		final ListIterator<ValueArgument> itr = values.listIterator(values.size());
		while (itr.hasPrevious()) {
			final ValueArgument p = itr.previous();
			if (name.equals(p.name()))
				return p.value();
		}

		return null;
	}

	public ValueArgument getValueArg(final String name) {
		if (!flag(name))
			return null;
		// Get value argument from end of list.
		final List<ValueArgument> values = valueArgs.get(name);
		final ListIterator<ValueArgument> itr = values.listIterator(values.size());
		while (itr.hasPrevious()) {
			final ValueArgument p = itr.previous();
			if (name.equals(p.name()))
				return p;
		}

		return null;
	}

	public boolean hasKV(final String name) {
		return keyValueArgs.containsKey(name);
	}

	public boolean isPresent(final String argumentName) {
		return valueArgs.containsKey(argumentName) || keyValueArgs.containsKey(argumentName);
	}

}
