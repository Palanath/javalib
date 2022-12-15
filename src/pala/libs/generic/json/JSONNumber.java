package pala.libs.generic.json;

public class JSONNumber extends Number implements JSONValue {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private final boolean neg;
	private final String left, right, exp;

	public JSONNumber(final boolean neg, final String left, final String right, final String exp) {
		this.neg = neg;
		this.left = left;
		this.right = right;
		this.exp = exp;
	}

	public JSONNumber(final long number) {
		String left = String.valueOf(number);
		if (neg = (number & Long.MIN_VALUE) == Long.MIN_VALUE)
			left = left.substring(1);
		this.left = left;
		right = exp = null;
	}

	@Override
	public double doubleValue() {
		final StringBuilder b = new StringBuilder(left);
		if (right != null)
			b.append('.').append(right);
		if (exp != null)
			b.append('e').append(exp);
		return (neg ? -1 : 1) * Double.parseDouble(b.toString());
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public int intValue() {
		int val = Integer.parseInt(left);
		if (neg)
			val = -val;
		if (exp != null)
			val = (int) Math.pow(10, val);
		return val;
	}

	@Override
	public long longValue() {
		long val = Long.parseLong(left);
		if (neg)
			val = -val;
		if (exp != null)
			val = (long) Math.pow(10, val);
		return val;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(neg ? "-" : "").append(left);
		if (right != null)
			stringBuilder.append('.').append(right);
		if (exp != null)
			stringBuilder.append('E').append(exp);
		return stringBuilder.toString();
	}

	@Override
	public String toString(final String indentation) {
		return toString();
	}

}
