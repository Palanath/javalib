package pala.libs.generic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import pala.libs.generic.util.Gateway;

public class Datamap extends HashMap<String, String> {

	public static class UpdateException extends RuntimeException {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private UpdateException(final Throwable cause) {
			super(cause);
		}
	}

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	// TODO Trim read input.
	public static Datamap read(final InputStream inputStream) {
		final Datamap map = new Datamap();
		map.update(inputStream);
		return map;
	}

	public static Datamap readLax(final InputStream inputStream) {
		final Datamap map = new Datamap();
		map.updateLax(inputStream);
		return map;
	}

	public static void save(final Datamap datamap, final OutputStream output) {
		datamap.save(output);
	}

	protected String splitter = "=", nextPair = "\n";

	protected String escapeKey(final String key) {
		return key.replace("\\", "\\\\").replace("\n", "\\\n").replace("=", "\\=");
	}

	protected String escapeValue(final String value) {
		return value.replace("\\", "\\\\").replace("\n", "\\\n");
	}

	public <T> T get(final String key, final Function<? super String, ? extends T> converter) {
		return converter.apply(get(key));
	}

	public <T> T get(final String key, final Gateway<? extends T, ? super String> converter) {
		return get(key, converter.to());
	}

	public <T> String put(final String key, final Function<? super T, ? extends String> converter, final T value) {
		return put(key, converter.apply(value));
	}

	public <T> String put(final String key, final Gateway<? super T, ? extends String> converter, final T value) {
		return put(key, converter.from(), value);
	}

	public void save(final OutputStream output) {
		try (PrintWriter writer = new PrintWriter(output)) {
			write(writer);
		}
	}

	public void save(final OutputStreamWriter out) {
		try (PrintWriter writer = new PrintWriter(out)) {
			write(writer);
		}
	}

	public void update(final InputStream inputStream) {
		try (Reader reader = new InputStreamReader(inputStream)) {
			NEXT_KEY: while (true) {
				String key = "";
				boolean escaped = false;
				while (true) {
					final int x = reader.read();
					if (x == -1)
						if (!key.isEmpty())
							throw new RuntimeException("Found a key with no value.");
						else
							return;

					final char c = (char) x;
					if (c == '\\') {
						if (!escaped) {
							escaped = true;
							continue;
						}
						key += '\\';
					} else if (c == '=')
						if (escaped)
							key += '=';
						else
							break;// Move on to parsing the value
					else if (c == '\n')
						if (escaped)
							key += '\n';
						else
							// Throw an exception, since this key has no value.
							throw new RuntimeException("Found a key with no value.");
					else
						key += c;
					escaped = false;
				}

				String value = "";
				while (true) {
					final int x = reader.read();
					switch (x) {
					case -1:
						put(key, value);
						return;
					case '\\':
						if (!escaped) {
							escaped = true;
							continue;
						}
						value += '\\';
						break;
					case '\n':
						if (!escaped) {
							put(key, value);
							continue NEXT_KEY;
						}
						value += '\n';
						break;
					default:
						value += (char) x;
						break;
					}
					escaped = false;
				}

			}

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateLax(final InputStream inputStream) {
		try (Reader reader = new InputStreamReader(inputStream)) {
			NEXT_KEY: while (true) {
				String key = "";
				boolean escaped = false;
				while (true) {
					final int x = reader.read();
					if (x == -1)
						return;// There's a dangling key at the end of the file. Ignore it.
					// Or we just, actually reached the end of the file. Either way...

					final char c = (char) x;
					if (c == '\\') {
						if (!escaped) {
							escaped = true;
							continue;
						}
						key += '\\';
					} else if (c == '=')
						if (escaped)
							key += '=';
						else
							break;// Move on to parsing the value
					else if (c == '\n')
						if (escaped)
							key += '\n';
						else
							// Ignore this key. It has no value. (Later, this sort of "error" will represent
							// the key exisiting, but having no value (or having the value null, or
							// something).)
							continue NEXT_KEY;
					else
						key += c;
					escaped = false;
				}

				String value = "";
				while (true) {
					final int x = reader.read();
					switch (x) {
					case -1:
						put(key, value);
						return;
					case '\\':
						if (!escaped) {
							escaped = true;
							continue;
						}
						value += '\\';
						break;
					case '\n':
						if (!escaped) {
							put(key, value);
							continue NEXT_KEY;
						}
						value += '\n';
						break;
					default:
						value += (char) x;
						break;
					}
					escaped = false;
				}

			}

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void write(final PrintWriter writer) {
		for (final Map.Entry<String, String> e : entrySet())
			write(writer, e.getKey(), e.getValue());
	}

	protected void write(final PrintWriter writer, final String key, final String value) {
		writer.print(escapeKey(key) + splitter + escapeValue(value) + nextPair);
	}

}
