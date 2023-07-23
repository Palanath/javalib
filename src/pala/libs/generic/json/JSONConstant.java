package pala.libs.generic.json;

/**
 * Represents a JSON constant. The constant, <code>NULL</code> is represented in
 * Java code by the Java value, <code>null</code>.
 * 
 * @author Palanath
 *
 */
public enum JSONConstant implements JSONValue {
	TRUE, FALSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	@Override
	public String toString(final String indentation) {
		return toString();
	}
}
