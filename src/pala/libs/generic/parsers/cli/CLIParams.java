package pala.libs.generic.parsers.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pala.libs.generic.strings.StringTools;

public class CLIParams {
	public static boolean checkFlag(final CLIParams params, final boolean def, final String... flags) {
		return params.checkFlag(def, flags);
	}

	public static double readDouble(final CLIParams params, final double def, final String... flags) {
		return params.readDouble(def, flags);
	}

	public static int readInt(final CLIParams params, final int def, final String... flags) {
		return params.readInt(def, flags);
	}

	public static String readString(final CLIParams params, final String def, final String... flags) {
		return params.readString(def, flags);
	}

	private final Map<String, String> named;
	private final List<String> unnamed, flags;

	protected CLIParams(Map<String, String> named, List<String> flags, List<String> unnamed) {
		this.named = named;
		this.flags = flags;
		this.unnamed = unnamed;
	}

	/**
	 * Whether to ignore captialization (letter casing) in methods where casing
	 * cannot be specified (e.g. {@link #checkFlag(boolean, String...)}).
	 * <code>false</code> by default.
	 */
	private boolean ignoreCase;

	/**
	 * <p>
	 * Creates a {@link CLIParams} object by parsing the array of {@link String}
	 * arguments. The provided {@link String}s are distinguished into three types:
	 * Named parameters, flags, and unnamed arguments.
	 * </p>
	 * <ul>
	 * <li>Named parameters are those that start with a hyphen (<code>-</code>) and
	 * contain an equals symbol (<code>=</code>) separating their name and their
	 * value. The hyphen they contain is a part of their name.</li>
	 * <li>Flags are parameters starting with a hyphen (<code>-</code>) that do not
	 * have an equals symbol or value after them.</li>
	 * <li>Unnamed arguments are simply the list of arguments that, conventionally,
	 * follow the flags and named params.</li>
	 * </ul>
	 * <p>
	 * Conventionally, flags and named params can be interleaved, but unnamed
	 * arguments are always specified at the end.
	 * </p>
	 * <p>
	 * The {@link #getNamed()}, {@link #getFlags()}, and {@link #getUnnamed()}
	 * {@link List}s created by this constructor are all unmodifiable.
	 * </p>
	 * 
	 * @param args The {@link String} arguments to parse.
	 */
	public CLIParams(final String... args) {
		final Map<String, String> named = new HashMap<>();
		final List<String> unnamed = new ArrayList<>(), flags = new ArrayList<>();
		for (final String s : args)
			if (s.startsWith("-")) {
				final int p = s.indexOf('=');
				if (p != -1)
					named.put(s.substring(0, p), s.substring(p + 1));
				else
					flags.add(s);
			} else
				unnamed.add(s);

		this.named = Collections.unmodifiableMap(named);
		this.flags = Collections.unmodifiableList(flags);
		this.unnamed = Collections.unmodifiableList(unnamed);
	}

	public CLIParams(Map<String, String> namedParams, List<String> nonKeyValueArgs) {
		named = namedParams;
		List<String> unnamed = new ArrayList<>(), flags = new ArrayList<>();
		for (String s : nonKeyValueArgs)
			(s.startsWith("-") ? flags : unnamed).add(s);
		this.unnamed = Collections.unmodifiableList(unnamed);
		this.flags = Collections.unmodifiableList(flags);
	}

	public boolean checkFlag(final boolean def, final boolean casesensitive, final String... flags) {
		for (final String s : getFlags())
			if (casesensitive ? StringTools.equalsAny(s, flags) : StringTools.equalsAnyIgnoreCase(s, flags))
				return true;
		for (final Entry<String, String> e : getNamed().entrySet())
			if (casesensitive ? StringTools.equalsAny(e.getKey(), flags)
					: StringTools.equalsAnyIgnoreCase(e.getKey(), flags))
				return Boolean.parseBoolean(e.getValue());
		return def;
	}

	public boolean checkFlag(final boolean def, final String... flags) {
		return checkFlag(def, ignoreCase, flags);
	}

	public Map<String, String> getNamed() {
		return named;
	}

	public List<String> getUnnamed() {
		return unnamed;
	}

	public List<String> getFlags() {
		return flags;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public double readDouble(final double def, final boolean casesensitive, final String... flags) {
		for (final Entry<String, String> e : getNamed().entrySet())
			if (casesensitive ? StringTools.equalsAny(e.getKey(), flags)
					: StringTools.equalsAnyIgnoreCase(e.getKey(), flags))
				return Double.parseDouble(e.getValue());
		return def;
	}

	public double readDouble(final double def, final String... flags) {
		return readDouble(def, ignoreCase, flags);
	}

	public int readInt(final int def, final boolean casesensitive, final String... flags) {
		for (final Entry<String, String> e : getNamed().entrySet())
			if (casesensitive ? StringTools.equalsAny(e.getKey(), flags)
					: StringTools.equalsAnyIgnoreCase(e.getKey(), flags))
				return Integer.parseInt(e.getValue());
		return def;
	}

	public int readInt(final int def, final String... flags) {
		return readInt(def, ignoreCase, flags);
	}

	public long readLong(final long def, final boolean casesensitive, final String... flags) {
		for (final Entry<String, String> e : getNamed().entrySet())
			if (casesensitive ? StringTools.equalsAny(e.getKey(), flags)
					: StringTools.equalsAnyIgnoreCase(e.getKey(), flags))
				return Long.parseLong(e.getValue());
		return def;
	}

	public long readLong(final long def, final String... flags) {
		return readLong(def, ignoreCase, flags);
	}

	public String readString(final String def, final boolean casesensitive, final String... flags) {
		for (final Entry<String, String> e : getNamed().entrySet())
			if (casesensitive ? StringTools.equalsAny(e.getKey(), flags)
					: StringTools.equalsAnyIgnoreCase(e.getKey(), flags))
				return e.getValue();
		return def;
	}

	public String readString(final String def, final String... flags) {
		return readString(def, ignoreCase, flags);
	}

	public void setIgnoreCase(final boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

}
