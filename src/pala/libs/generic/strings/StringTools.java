package pala.libs.generic.strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pala.libs.generic.JavaTools;

public final class StringTools {

	public static class NumberUnit implements Comparable<NumberUnit> {
		public static final NumberUnit BYTE = new NumberUnit("B", BigInteger.ONE),
				KILOBYTE = new NumberUnit("KB", BigInteger.valueOf(1000)),
				MEGABYTE = new NumberUnit("MB", KILOBYTE.getAmt().multiply(KILOBYTE.getAmt())),
				GIGABYTE = new NumberUnit("GB", MEGABYTE.getAmt().multiply(KILOBYTE.getAmt())),
				TERABYTE = new NumberUnit("TB", GIGABYTE.getAmt().multiply(KILOBYTE.getAmt()));

		public static final NumberUnit NANOSECONDS = new NumberUnit("ns", BigInteger.ONE),
				MICROSECONDS = NANOSECONDS.derive("\u03BCs", 1000), MILLISECONDS = MICROSECONDS.derive("ms", 1000),
				SECONDS = MILLISECONDS.derive("s", 1000), MINUTES = SECONDS.derive("m", 60),
				HOURS = MINUTES.derive("hr", 60), DAYS = HOURS.derive("d", 24), WEEKS = DAYS.derive("wk", 7),
				MONTHS = DAYS.derive("mo", 31), YEARS = DAYS.derive("yr", 365), DECADES = YEARS.derive("dec", 10);

		private static final NumberUnit[] BYTE_SIZES = { BYTE, KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE }, TIMES = {
				NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS, DECADES };

		public NumberUnit adjust(NumberUnit other) {
			return adjust(other.getAmt());
		}

		/**
		 * Returns a new {@link NumberUnit} that is the same as this {@link NumberUnit}
		 * except that the {@link #getAmt()} is divded by the provided <code>amt</code>.
		 * This method effectively "scales" this {@link NumberUnit} to be in terms of
		 * the provided amount. For example, calling
		 * <code>MONTHS.adjust(SECONDS.getAmt())</code> will return a new
		 * {@link NumberUnit} representing months, that can be used to format numbers
		 * specifying <i>seconds</i> instead of <i>nanoseconds</i>.
		 * 
		 * @param amt The amount to divide by.
		 * @return
		 */
		public NumberUnit adjust(BigInteger amt) {
			return new NumberUnit(symbol, getAmt().divide(amt));
		}

		public static NumberUnit[] getByteSizes() {
			return BYTE_SIZES.clone();
		}

		public static NumberUnit[] getTimes() {
			return TIMES.clone();
		}

		/**
		 * <p>
		 * Returns an array of {@link NumberUnit}s representing durations of time. The
		 * returned array of {@link NumberUnit}s begins with a {@link NumberUnit}
		 * instance representing the same unit as the specified {@link NumberUnit}, but
		 * with an {@link #getAmt() amount} of <code>1</code>. The returned array is in
		 * sorted, ascending order, with the smallest {@link NumberUnit} at the
		 * beginning.
		 * </p>
		 * <p>
		 * The provided {@link NumberUnit} <code>start</code> should be one of the
		 * following time-based {@link NumberUnit}s:
		 * </p>
		 * <ul>
		 * <li>{@link #NANOSECONDS}</li>
		 * <li>{@link #MICROSECONDS}</li>
		 * <li>{@link #MILLISECONDS}</li>
		 * <li>{@link #SECONDS}</li>
		 * <li>{@link #MINUTES}</li>
		 * <li>{@link #HOURS}</li>
		 * <li>{@link #DAYS}</li>
		 * </ul>
		 * <p>
		 * The above list is relative to {@link #NANOSECONDS}, which has the
		 * {@link #getAmt() value} <code>1</code>, meaning that any of the time-based
		 * {@link NumberUnit}s specified in this class are designed to format times
		 * specified <i>in</i> nanoseconds. If a time is specified in, e.g., seconds, it
		 * will need to be multiplied by <code>1_000_000_000</code> to be formatted
		 * appropriately.
		 * </p>
		 * <p>
		 * The returned array of {@link NumberUnit} is adjusted so that the specified
		 * unit has value <code>1</code>. For example, if {@link #SECONDS} is provided
		 * as an argument, the returned array will be equivalent to:
		 * </p>
		 * 
		 * <pre>
		 * <code>new NumberUnit[] { new NumberUnit("s", BigInteger.ONE) // First unit is seconds, with value 1.
		 * , new NumberUnit("m", BigInteger.valueOf(60)), new NumberUnit("hr", BigInteger.valueOf(60 * 60)), new NumberUnit("d", BigInteger.valueOf(60 * 60 * 24)), ...};</code>
		 * </pre>
		 * 
		 * @param start
		 * @return
		 */
		public static NumberUnit[] getTimes(NumberUnit start) {
			int ind = Arrays.binarySearch(TIMES, start);
			if (ind < 0)
				throw new IllegalArgumentException("Provided NumberUnit is not a time unit.");
			return adjust(Arrays.copyOfRange(TIMES, ind, TIMES.length));
		}

		/**
		 * <p>
		 * Adjusts the specified array of {@link NumberUnit}s so that the first
		 * {@link NumberUnit} has value <code>1</code>. This function returns a new
		 * array of {@link NumberUnit}s, containing {@link NumberUnit} objects that are
		 * synonymous to those specified in the provided array, but that their
		 * {@link #getAmt() amounts} have all been divided by the amount of the first
		 * specified {@link NumberUnit}. For example, if
		 * </p>
		 * 
		 * <pre>
		 * <code>{ SECONDS, MINUTES, HOURS }</code>
		 * </pre>
		 * 
		 * <p>
		 * is provided, the result will be:
		 * </p>
		 * 
		 * <pre>
		 * <code>{ new NumberUnit(SECONDS.getSymbol(), BigInteger.valueOf(1)), new NumberUnit(MINUTES.getSymbol(), MINUTES.getAmt().divide(SECONDS.getAmt())), new NumberUnit(HOURS.getSymbol(), HOURS.getAmt().divide(SECONDS.getAmt())) }</code>
		 * </pre>
		 * 
		 * <p>
		 * The returned array is newly constructed and modifiable as desired.
		 * </p>
		 * 
		 * @param units The units, in ascending order.
		 * @return A new array of adjusted {@link NumberUnit}s.
		 */
		public static NumberUnit[] adjust(NumberUnit... units) {
			NumberUnit[] n = new NumberUnit[units.length];
			for (int i = 0; i < units.length; i++)
				n[i] = new NumberUnit(units[i].getSymbol(), units[i].getAmt().divide(units[0].getAmt()));
			return n;
		}

		private final String symbol;
		private final BigInteger amt;

		public NumberUnit(final String symbol, final BigInteger amt) {
			this.symbol = symbol;
			this.amt = amt;
		}

		public BigInteger getAmt() {
			return amt;
		}

		public String getSymbol() {
			return symbol;
		}

		public NumberUnit derive(String symbol, long factor) {
			return derive(symbol, BigInteger.valueOf(factor));
		}

		public NumberUnit derive(String symbol, BigInteger factor) {
			return new NumberUnit(symbol, getAmt().multiply(factor));
		}

		@Override
		public int compareTo(NumberUnit o) {
			return amt.compareTo(o.amt);
		}

		@Override
		public String toString() {
			return getSymbol();
		}

		@Override
		public int hashCode() {
			return Objects.hash(amt, symbol);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			else if (!(obj instanceof NumberUnit))
				return false;
			NumberUnit other = (NumberUnit) obj;
			return Objects.equals(amt, other.amt) && Objects.equals(symbol, other.symbol);
		}

	}

	private static final byte[] HEX_CHAR_BYTES = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

	public static final boolean endsWithIgnoreCase(String string, String suffix) {
		if (string.length() < suffix.length())
			return false;
		for (int i = 0; i < suffix.length(); i++)
			if (Character.toLowerCase(string.charAt(string.length() - i - 1)) != Character
					.toLowerCase(suffix.charAt(suffix.length() - i - 1)))
				return false;
		return true;
	}

	/**
	 * <p>
	 * Returns true if any of <code>possiblePieces</code> are contained inside the
	 * given String, <code>string</code>.
	 * </p>
	 * <p>
	 * Note: This method tends to be faster than comparing possible matches and the
	 * base string with {@link String#contains(CharSequence)}, after calling
	 * {@link String#toLowerCase()} upon all of them, if the base string contains
	 * fragments of the possible pieces, (where the fragments start with the same
	 * characters as their respective pieces), or if any of the possible pieces are
	 * near a long list of possible pieces, in comparison to a for loop. The latter
	 * is due to the fact that this method checks against all the pieces
	 * simultaneously, so if a match is near the end of <code>possiblePieces</code>,
	 * it doesn't have to wait for all the other possible pieces to be checked
	 * before it can be checked.
	 * </p>
	 *
	 * @param string         The string that may contain something in
	 *                       <code>possiblePieces</code>.
	 * @param possiblePieces Any number of Strings. If <code>string</code> contains
	 *                       one of these, this method will return true.
	 * @return <code>true</code> if one of the <code>possiblePieces</code> are found
	 *         inside <code>string</code>. False otherwise.
	 */
	public static boolean containsIgnoreCase(String string, final String... possiblePieces) {

		string = string.toLowerCase();

		final List<String> standbies = new LinkedList<>();
		final List<Pair> matchingPieces = new LinkedList<>();
		for (final String s : possiblePieces)
			if (s == null || s.isEmpty())
				continue;
			else
				standbies.add(s);

		final char[] charArray = string.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			final char c = charArray[i];
			for (final Iterator<Pair> iterator = matchingPieces.iterator(); iterator.hasNext();) {
				final Pair e = iterator.next();
				final char nextChar = Character.toLowerCase(e.string.charAt(e.value));
				if (nextChar == c) {
					e.value++;
					if (e.value == e.getLength())
						return true;
				} else
					iterator.remove();
			}
			for (final Iterator<String> iterator = standbies.iterator(); iterator.hasNext();) {
				final String s = iterator.next();
				if (s.length() > charArray.length - i)
					iterator.remove();
				else {
					final char firstChar = Character.toLowerCase(s.charAt(0));
					if (firstChar == c)
						if (s.length() == 1)
							return true;
						else
							matchingPieces.add(new Pair(s, 1));
				}
			}
		}

		return false;
	}

	public static boolean equalsAny(final String arg, final String... possibleMatches) {
		if (arg == null)
			return false;
		for (final String s : possibleMatches)
			if (arg.equals(s))
				return true;
		return false;
	}

	public static boolean equalsAnyIgnoreCase(final String arg, final String... possibleMatches) {
		if (arg == null)
			return false;
		for (final String s : possibleMatches)
			if (arg.equalsIgnoreCase(s))
				return true;
		return false;
	}

	/**
	 * <p>
	 * Returns a {@link Map} of all the {@link NumberUnit}-parsed values of the
	 * provided number. The provided {@link NumberUnit} array should be in ascending
	 * order.
	 * </p>
	 * <p>
	 * Note that the {@link BigInteger} is interpreted as a number of
	 * <code>unit</code> units. The first {@link NumberUnit} provided is the unit
	 * for the {@link BigInteger}.
	 * 
	 * @param value The value to get the parts of.
	 * @param units The types of parts to get. The first of these is the unit that
	 *              the {@link BigInteger} is interpreted as. This array should be
	 *              in ascending order.
	 * @return A {@link Map} of the {@link NumberUnit}s parsed.
	 */
	public static Map<NumberUnit, BigInteger> getParts(NumberUnit unit, BigInteger count,
			NumberUnit... formattingUnits) {
		count = count.multiply(unit.amt);
		Map<NumberUnit, BigInteger> values = new HashMap<>(formattingUnits.length);
		for (int i = formattingUnits.length - 1; i >= 0; --i) {
			BigInteger[] vals = count.divideAndRemainder(formattingUnits[i].amt);
			if (!vals[0].equals(BigInteger.ZERO))
				values.put(formattingUnits[i], vals[0]);
			count = vals[1];// Set value to be the remainder.
		}
		return values;
	}

	public static String format(NumberUnit unit, BigInteger count, String delimiter, NumberUnit... formattingUnits) {
		count = count.multiply(unit.amt);
		StringBuilder sb = new StringBuilder();
		for (int i = formattingUnits.length - 1; i >= 0; --i) {
			BigInteger[] vals = count.divideAndRemainder(formattingUnits[i].amt);
			if (!vals[0].equals(BigInteger.ZERO)) {
				if (sb.length() != 0)
					sb.append(delimiter);
				sb.append(vals[0]).append(formattingUnits[i].symbol);
			}
			count = vals[1];
		}
		return sb.length() == 0 ? "0" + formattingUnits[0].symbol : sb.toString();
	}

	public static Map<NumberUnit, BigInteger> getParts(BigInteger value, NumberUnit... units) {
		return getParts(NumberUnit.NANOSECONDS, value, units);// NANOSECONDS has amt 1.
	}

	public static String format(BigInteger number, String delimiter, NumberUnit... units) {
		return format(NumberUnit.NANOSECONDS, number, delimiter, units);
	}

	/**
	 * <p>
	 * Converts the provided {@link Duration} into a {@link BigInteger} so that it
	 * can be used in {@link #format(BigInteger, NumberUnit...) formatting}.
	 * </p>
	 * <p>
	 * {@link Duration} objects are comprised of a <code>seconds</code> count and a
	 * <code>nanoseconds</code> count, both of which are separate fields. The
	 * <code>nanoseconds</code> count ranges from <code>0</code> to
	 * <code>999,999,999</code>.
	 * </p>
	 * <p>
	 * This method can return a {@link BigInteger} that is equal to the number of
	 * <code>seconds</code> in the provided {@link Duration} (if
	 * <code>secOrNanoPrecision</code> is <code>true</code>) OR it can return a
	 * {@link BigInteger} that is equal to the number of TOTAL nanoseconds in the
	 * provided {@link Duration}. The number of total nanoseconds in the
	 * {@link Duration} is the number of seconds in the {@link Duration}, times
	 * <code>1,000,000,000</code>, plus the number of <code>nanoseconds</code>
	 * stored in the {@link Duration}.
	 * </p>
	 * 
	 * @param duration           The {@link Duration} to convert.
	 * @param secOrNanoPreicison Whether second or nanosecond precision is desired.
	 *                           If second precision is requested, the
	 *                           {@link BigInteger} will represent seconds (e.g.
	 *                           <code>1</code> will mean 1 second). If nanosecond
	 *                           precision is requested, the {@link BigInteger} will
	 *                           represent nanoseconds (e.g. <code>1</code> will
	 *                           mean 1 nanosecond and <code>1,000,000,001</code>
	 *                           will mean 1 second and 1 nanosecond).
	 * @return The converted value.
	 */
	public static BigInteger toBigIntegerForFormatting(Duration duration, boolean secOrNanoPreicison) {
		return secOrNanoPreicison ? BigInteger.valueOf(duration.getSeconds())
				: BigInteger.valueOf(duration.getSeconds()).multiply(BigInteger.valueOf(1000000000))
						.add(BigInteger.valueOf(duration.getNano()));
	}

	/**
	 * <p>
	 * Binary searches the array of provided {@link NumberUnit}s to determine which
	 * of them is the largest that is less than or equal to the value of the
	 * provided <code>number</code>. Note that, if no such {@link NumberUnit}
	 * satisfies this criteria, this method returns <code>null</code>. The provided
	 * array of {@link NumberUnit}s should be sorted in ascending order.
	 * </p>
	 * <p>
	 * The return value of this method is the largest unit that would be stored in
	 * the map return value of {@link #getParts(BigInteger, NumberUnit...)} with the
	 * same number and {@link NumberUnit}s as arguments. (E.g., for 1000 seconds as
	 * the number argument and {@link NumberUnit#getTimes()} as the array of units,
	 * {@link NumberUnit#MINUTES} would be returned, since the largest
	 * {@link NumberUnit} in the array that is less than or equal to the provided
	 * number is {@link NumberUnit#MINUTES}.)
	 * </p>
	 * 
	 * @param number The {@link NumberUnit} to find the largest unit of.
	 * @param units  The array of {@link NumberUnit}s.
	 * @return The largest {@link NumberUnit} that is used in the string
	 *         representation of the provided {@link NumberUnit}.
	 */
	public static NumberUnit getLargest(BigInteger number, NumberUnit... units) {
		int ind = JavaTools.binarySearch(units.length, a -> {
			int res = units[a].amt.compareTo(number);
			return res == 0 ? null : res > 0;
		});
		return ind < 0 ? ind == -1 ? null : units[-ind - 2] : units[ind];
	}

	public static String format(BigInteger number, NumberUnit... units) {
		return format(number, "", units);
	}

	public static String format(NumberUnit unit, BigInteger count, NumberUnit... formattingUnits) {
		return format(unit, count, "", formattingUnits);
	}

	/**
	 * Formats the provided number in bytes, using suffixes as appropriate.
	 *
	 * @param number The {@link BigInteger} number to format.
	 * @return A string holding the formatted output.
	 */
	public static String formatBytes(final BigInteger number) {
		NumberUnit m = null;
		final BigDecimal bd = new BigDecimal(number);
		for (final NumberUnit element : NumberUnit.BYTE_SIZES)
			if (number.compareTo(element.amt) >= 0)
				m = element;
			else
				break;
		if (m == null)
			return String.valueOf(number);

		final BigDecimal b = bd.divide(new BigDecimal(m.amt)).setScale(2, RoundingMode.HALF_UP);
		return b.stripTrailingZeros().toPlainString() + m.symbol;
	}

	public static String formatBytes(final long bytes) {
		return formatBytes(BigInteger.valueOf(bytes));
	}

	public static byte[] fromBase64String(final String base64) {
		return Base64.getDecoder().decode(base64);
	}

	public static byte[] fromHexString(final String b) {
		if ((b.length() & 1) != 0)
			throw new NumberFormatException("Input must be pairs of hex characters.");
		final byte[] rb = new byte[b.length() / 2];
		for (int i = 0; i < b.length(); i += 2)
			rb[i / 2] = (byte) (hexVal(b.charAt(i)) << 4 | hexVal(b.charAt(i + 1)));
		return rb;
	}

	private static byte hexVal(final char c) {
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'A':
			return 10;
		case 'B':
			return 11;
		case 'C':
			return 12;
		case 'D':
			return 13;
		case 'E':
			return 14;
		case 'F':
			return 15;
		default:
			throw new NumberFormatException("Input must be hex chars.");
		}
	}

	public static boolean isNumeric(final String str) {
		for (int i = 0; i < str.length(); i++)
			if (!Character.isDigit(str.charAt(i)))
				return false;
		return true;
	}

	public static String toBase64String(final byte... data) {
		return Base64.getEncoder().encodeToString(data);
	}

	public static String toHexString(final byte... b) {
		final byte[] rb = new byte[b.length * 2];

		for (int i = 0; i < b.length; i++) {
			rb[i * 2] = HEX_CHAR_BYTES[b[i] >>> 4 & 0xF];
			rb[i * 2 + 1] = HEX_CHAR_BYTES[b[i] & 0xF];
		}

		return new String(rb, StandardCharsets.UTF_8);
	}

	/**
	 * <p>
	 * Splits the provided <code>input</code> into parts using the provided
	 * <code>delimiter</code> and returns the parts in an array. Parts may be empty
	 * where there are two consecutive instances of the provided delimiter in the
	 * provided input (in which case the "part" between them is the empty string,
	 * such as in the example input string <code>"a..b"</code> delimited by
	 * <code>"."</code>, which would result in the array:
	 * <code>{"a", "", "b"</code>. Notice that <code>"a"</code> is the string from
	 * the beginning of the input to the first <code>"."</code>, <code>""</code> is
	 * the string "between" the two <code>"."</code> characters, and
	 * <code>"b"</code> is the string between the last <code>"."</code> and the end
	 * of the string). The length of the returned array is always the number of
	 * delimiters contained in the provided input, plus 1.
	 * </p>
	 * <p>
	 * This method does not consider delimiters that intersect earlier delimiters.
	 * For example, with input <code>"abbbc"</code> and delimiter <code>"bb"</code>,
	 * this method would find the first two <code>"bb"</code> characters in the
	 * input, (at positions <code>1,2</code>), and consider them an instance of the
	 * delimiter. The string would then be split as along those characters, denoted
	 * by a <code>|</code> in the following depiction:
	 * </p>
	 * <p>
	 * If the empty string is provided for the delimiter, the string is split
	 * between each character, and the returned array will contain each character in
	 * the input string, as a {@link String}.
	 * </p>
	 * <p>
	 * If the empty string is provided for the input, an array of size one
	 * containing the empty string is returned.
	 * </p>
	 * 
	 * <pre>
	 * <code>a|bc</code>
	 * </pre>
	 * <p>
	 * The method would then return the array <code>{"a", "bc"}</code>
	 * </p>
	 * 
	 * @param input     The input string to split.
	 * @param delimiter The delimiter to split the string along.
	 * @return A {@link String} array containing the parts of the split input.
	 */
	public static String[] split(String input, String delimiter) {
		int previousDelimitPosition = -1;// Starts at "before" the string.
		List<String> parts = new ArrayList<>(3);
		if (input.isEmpty())
			return new String[] { input };
		OUTER: for (int i = 0, c = input.charAt(i);; c = ++i >= input.length() ? -1 : input.charAt(i)) {
			// Check if we're "at" a delimiter.
			if (c == -1)
				break;
			if (delimiter.isEmpty()) {
				parts.add(input.substring(previousDelimitPosition + 1, i + 1));
				previousDelimitPosition = i;
			} else if (delimiter.charAt(0) == c) {
				if (delimiter.length() == 1) {
					parts.add(input.substring(previousDelimitPosition + 1, i));
					previousDelimitPosition = i;
				} else
					for (int j = i + 1;; j++) {
						c = j >= input.length() ? -1 : input.charAt(j);
						if (c == -1)
							break OUTER;
						else if (c == delimiter.charAt(j - i)) {
							if (j - i == delimiter.length() - 1) {// We've parsed a whole instance of the delimiter
																	// out of the input.

								// i is the position of the start of the instance of the delimiter that we just
								// found, in the input.
								// previousDelimitPosition is the last character of the previous instance of the
								// delimiter (or -1, if none), in the input.
								parts.add(input.substring(previousDelimitPosition + 1, i));
								previousDelimitPosition = j;
								// "Skip forward" in the outer loop, over the delimiter we just parsed.
								i = j;
								break;// Upon the outer loop's, i will become what j currently is, +1 (which is the
										// next character that we want to be considered by the outer loop).
							}
						} else// Character didn't match the delimiter.
							break;

					}
			}
		}
		parts.add(input.substring(previousDelimitPosition + 1));

		return parts.toArray(new String[parts.size()]);
	}

	public static String maxSize(String str, int maxSize, String ellipsis) {
		return str.length() > maxSize ? str.substring(0, maxSize - ellipsis.length()) + ellipsis : str;
	}

}
