package pala.libs.generic.strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class StringTools {

	public static class NumberUnit {
		public static NumberUnit BYTE = new NumberUnit("B", BigInteger.ONE),
				KILOBYTE = new NumberUnit("KB", BigInteger.valueOf(1000)),
				MEGABYTE = new NumberUnit("MB", KILOBYTE.getAmt().multiply(KILOBYTE.getAmt())),
				GIGABYTE = new NumberUnit("GB", MEGABYTE.getAmt().multiply(KILOBYTE.getAmt())),
				TERABYTE = new NumberUnit("TB", GIGABYTE.getAmt().multiply(KILOBYTE.getAmt()));

		private static final NumberUnit[] BYTE_SIZES = { BYTE, KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE };

		public static NumberUnit[] getByteSizes() {
			return BYTE_SIZES.clone();
		}

//		GRAND("K"), MILLION("M"), BILLION("B"), TRILLION("T"), QUADRIILLION("Q");

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
	}

	private static final byte[] HEX_CHAR_BYTES = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

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
	 * Formats the provided number as if it were a monetary value, (applying the
	 * {@link MoneyUnit} conversion, as appropriate), but does not prepend the
	 * {@link #CURRENCY_SYMBOL}.
	 *
	 * @param number The {@link BigInteger} number to format.
	 * @return A string holding the formatted number.
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
			} else {
				if (delimiter.charAt(0) == c) {
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
		}
		parts.add(input.substring(previousDelimitPosition + 1));

		return parts.toArray(new String[parts.size()]);
	}

	public static void main(String[] args) {
		split("", "abc");
	}

}
