package pala.libs.generic.json;

public enum JSONConstant implements JSONValue {
	TRUE, FALSE, NULL;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	@Override
	public String toString(final String indentation) {
		return toString();
	}
}
