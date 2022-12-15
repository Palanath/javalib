package pala.libs.generic.commands.processing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pala.libs.generic.commands.StringCommand;
import pala.libs.generic.streams.CharacterStream;

/**
 * This class is not threadsafe. :P
 *
 * @author Palanath
 *
 */
public class StringCommandParser {

	private String commandInitiator;

	public StringCommandParser(final String commandChar) {
		if ((commandInitiator = commandChar) == null)
			throw new IllegalArgumentException();
	}

	public String getCommandInitiator() {
		return commandInitiator;
	}

	/**
	 * Parses a command out of the given {@link String}. This method begins by
	 * attempting to find the {@link #commandInitiator} at the start of the given
	 * {@link String}. If it cannot find an exact match of the
	 * {@link #commandInitiator}, for any reason (including the presence of
	 * whitespace), this method will return <code>null</code>. Otherwise, the method
	 * continues by trimming any whitespace. Once this whitespace, (which is after
	 * the {@link #commandInitiator}), has been passed over, this method splits the
	 * string by unquoted whitespace. The first result of the split is considered
	 * the command. If no command is found, the returned
	 * {@link StringCommand#command StringCommand's command} will be an empty
	 * string. If whitespace is found after the command or after an argument, but no
	 * succeeding argument is found, there will be an empty {@link String} at the
	 * end of the parsed {@link StringCommand#args} that is returned by this method.
	 *
	 * @param input The input {@link String} to parse.
	 * @return <code>null</code> or the parsed command as a {@link StringCommand}.
	 */
	public StringCommand parse(final String input) {
		final CharacterStream stream = CharacterStream.from(input);

		if (parseCommandInitiator(stream))
			return null;

		final List<String> args = new LinkedList<>();

		int c;
		while (Character.isWhitespace(c = stream.next()))
			;

		boolean quote = false, escaped = false;
		final StringBuilder currArg = new StringBuilder();
		while (c != -1) {
			if (c == '\\') {
				if (!(escaped ^= true))
					currArg.append('\\');
			} else if (c == '"')
				if (escaped) {
					currArg.append('"');
					escaped = false;
				} else
					quote ^= true;
			else {
				if (Character.isWhitespace(c)) {
					if (!quote) {
						args.add(currArg.toString());
						currArg.setLength(0);
						while (Character.isWhitespace(c = stream.next()))
							;
						continue;
					}
					if (escaped) {
						currArg.append('\\');
						escaped = false;
					}
				} else {
					if (escaped) {
						currArg.append("\\");
						escaped = false;
					}
				}
				currArg.append((char) c);
			}
			c = stream.next();
		}
		args.add(currArg.toString());

		final String command = args.get(0), argArr[] = new String[args.size() - 1];
		final Iterator<String> itr = args.iterator();
		if (itr.hasNext()) {
			itr.next();
			for (int i = 0; itr.hasNext(); argArr[i++] = itr.next())
				;
		}

		return new StringCommand(command, input, argArr);

	}

	/**
	 * Parses the {@link #commandInitiator} from the given {@link CharacterStream}
	 * that represents a command invocation. <code>false</code> is returned if the
	 * command initiator is found in the stream, and <code>true</code> is returned
	 * otherwise. This method leaves off such that the next character in the stream
	 * is the character immediately after the command separator.
	 *
	 * @param stream The stream to parse.
	 * @return <code>true</code> if the command initiator is NOT found in the
	 *         stream, and <code>false</code> otherwise.
	 */
	protected boolean parseCommandInitiator(final CharacterStream stream) {
		for (int i = 0; i < commandInitiator.length(); i++)
			if (stream.next() != commandInitiator.charAt(i))
				return true;
		return false;
	}

	public void setCommandInitiator(final String commandInitiator) {
		if (commandInitiator == null)
			throw new IllegalArgumentException();
		this.commandInitiator = commandInitiator;
	}
}
