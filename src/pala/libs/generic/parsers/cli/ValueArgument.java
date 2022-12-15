package pala.libs.generic.parsers.cli;

public class ValueArgument implements Argument {

	private final String prefix, value;

	protected ValueArgument(final String prefix, final String value) {
		this.prefix = prefix;
		this.value = value;
	}

	@Override
	public String name() {
		return value;
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
