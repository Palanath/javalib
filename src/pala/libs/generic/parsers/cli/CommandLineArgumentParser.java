package pala.libs.generic.parsers.cli;

import java.util.ArrayList;
import java.util.List;

public class CommandLineArgumentParser {
	public static CommandLineArgs parse(final String... args) {
		final List<Argument> parsed = new ArrayList<>();
		for (String s : args)
			if (s.contains("=")) {
				final int splindex = s.indexOf('=');
				String first = s.substring(0, splindex), prefix;
				if (first.startsWith("--")) {
					prefix = "--";
					first = first.substring(2);
				} else if (first.startsWith("-")) {
					prefix = "-";
					first = first.substring(1);
				} else
					prefix = "";
				parsed.add(new KeyValueArgument(prefix, first,
						splindex == s.length() - 1 ? "" : s.substring(splindex + 1)));
			} else {
				String prefix;
				if (s.startsWith("--")) {
					prefix = "--";
					s = s.substring(2);
				} else if (s.startsWith("-")) {
					prefix = "-";
					s = s.substring(1);
				} else
					prefix = "";
				parsed.add(new ValueArgument(prefix, s));
			}

		return new CommandLineArgs(parsed);
	}
}
