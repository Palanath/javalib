package pala.libs.generic.parsing;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public interface CharacterParser extends Parser<Character> {
	static CharacterParser from(final CharSequence seq) {
		return new CharacterParser() {
			int i = -1;

			@Override
			public int nxt() {
				return i + 1 >= seq.length() ? -1 : seq.charAt(++i);
			}

			@Override
			public int pk() {
				return i + 1 >= seq.length() ? -1 : seq.charAt(i + 1);
			}
		};
	}

	static CharacterParser from(final Parser<Character> in) {
		return new CharacterParser() {

			@Override
			public int nxt() {
				final Character c = in.next();
				return c == null ? -1 : c;
			}

			@Override
			public int pk() {
				final Character c = in.peek();
				return c == null ? -1 : c;
			}
		};
	}

	static CharacterParser from(final Reader in) {
		return new CharacterParser() {
			Integer cache;

			@Override
			public int nxt() {
				try {
					if (cache == null)
						return in.read();
					final int c = cache;
					cache = null;
					return c;
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public int pk() {
				if (cache != null)
					return cache;
				try {
					return cache = in.read();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	default String clct(final Function<Character, Boolean> cond) {
		final StringBuilder sb = new StringBuilder();
		while (peek() != null && cond.apply(peek()))
			sb.append(next());
		return sb.toString();
	}

	@Override
	default Character next() {
		final int nxt = nxt();
		return nxt == -1 ? null : (char) nxt;
	}

	int nxt();

	default void parseWhitespace() {
		while (pk() != -1 && Character.isWhitespace(pk()))
			nxt();
	}

	@Override
	default Character peek() {
		final int pk = pk();
		return pk == -1 ? null : (char) pk;
	}

	int pk();
}
