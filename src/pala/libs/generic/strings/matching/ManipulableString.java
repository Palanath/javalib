package pala.libs.generic.strings.matching;

/**
 * @author Palanath
 */
public class ManipulableString {
	private String text;

	public ManipulableString(final String text) {
		this.text = text;
	}

	/**
	 * @param matches The matchings to match this against.
	 * @return The remaining text (i.e., the same as the result of an immediate call
	 *         to {@link #getText()}), or <code>null</code> if nothing was matched.
	 */
	public String consumeIf(final Matching... matches) {
		String match;
		for (final Matching m : matches)
			if ((match = m.match(text)) != null)
				return text = match;
		return null;
	}

	public String consumeIf(final String... matches) {
		for (final String s : matches)
			if (text.startsWith(s))
				return text = text.substring(s.length());
		return null;
	}

	public String consumeIfIgnoreCase(final String... matches) {
		for (final String s : matches)
			if (text.toLowerCase().startsWith(s.toLowerCase()))
				return text = text.substring(s.length());
		return null;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}