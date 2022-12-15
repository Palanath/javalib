package pala.libs.generic.util;

import java.util.HashMap;

public class Splittree<V> {

	public class Branch extends Splittree<V> {
		private final Character item;

		private Branch(final char item) {
			Splittree.this.map.put(this.item = item, this);// Puts this branch in the parent branch.
		}

		@Override
		public String toString() {
			return "Item[\'" + item + "': " + super.toString() + "]";
		}

	}

	private static char head(final String str) {
		return str.charAt(0);
	}

	private static String tail(final String str) {
		return str.substring(1);
	}

	protected final HashMap<Character, Branch> map = new HashMap<>();

	private V val;

	/**
	 * <p>
	 * Returns whether or not the specified key is in use by the map.
	 * </p>
	 * <p>
	 * Please note that <b>keys may be added artificially</b>, as a byproduct of
	 * different put operations. Particularly, when an item is put into this map
	 * with key, K:
	 *
	 * <pre>
	 * any key, <code>q</code>, that causes the expression: <code>K.beginsWith(q)</code> to return <code>true</code> will also be contained in the map.
	 * </pre>
	 *
	 * @param key The key to check the presence of.
	 * @return
	 */
	public boolean containsKey(final String key) {
		return sub(key) == null;
	}

	public V get(final String key) {
		final Splittree<V> val = sub(key);
		return val == null ? null : val.getValue();
	}

	public V getValue() {
		return val;
	}

	public void put(final String key, final V value) {
		if (key.isEmpty())
			val = value;
		else {
			final char h = head(key);
			final Branch branch = !map.containsKey(h) ? this.new Branch(h)// lol i hope ur confused 8)
					: map.get(h);
			branch.put(tail(key), value);
		}
	}

	public Splittree<V> sub(final String key) {
		if (key.isEmpty())
			return this;
		final char h = head(key);
		return map.containsKey(h) ? map.get(h).sub(tail(key)) : null;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
