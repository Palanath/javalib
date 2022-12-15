package pala.libs.generic.parsers.cli;

public class KeyValueArgument implements Argument {

	private final String prefix, key, value;

	protected KeyValueArgument(final String prefix, final String key, final String value) {
		this.prefix = prefix;
		this.key = key;
		this.value = value;
	}

	public String key() {
		return key;
	}

	@Override
	public String name() {
		return key();
	}

	@Override
	public String prefix() {
		return prefix;
	}

	@Override
	public String value() {
		return value;
	}

}
