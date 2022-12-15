package pala.libs.generic.parsers.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application.Parameters;
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

	private final List<String> unnamed;

	/**
	 * Whether to ignore captialization (letter casing) in methods where casing
	 * cannot be specified (e.g. {@link #checkFlag(boolean, String...)}).
	 * <code>false</code> by default.
	 */
	private boolean ignoreCase;

	public CLIParams(final Parameters params) {
		named = params.getNamed();
		unnamed = params.getUnnamed();
	}

	public CLIParams(final String... args) {
		final Map<String, String> named = new HashMap<>();
		final List<String> unnamed = new ArrayList<>();
		for (final String s : args) {
			final int p = s.indexOf('=');
			if (p != -1)
				named.put(s.substring(0, p), s.substring(p + 1));
			else
				unnamed.add(s);
		}

		this.named = Collections.unmodifiableMap(named);
		this.unnamed = Collections.unmodifiableList(unnamed);
	}

	public boolean checkFlag(final boolean def, final boolean casesensitive, final String... flags) {
		for (final String s : getUnnamed())
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
