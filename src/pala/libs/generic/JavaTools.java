package pala.libs.generic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import pala.libs.generic.util.Pair;

public final class JavaTools {
	/**
	 * Converts and adds each element in the <code>from</code> matrix to the
	 * <code>test</code> collection.
	 *
	 * @param <FE>      The type of element in the source object.
	 * @param <TE>      The type of the element in the destination
	 *                  {@link Collection}.
	 * @param <T>       The type of the destination.
	 * @param from      The source {@link Iterable}.
	 * @param converter The converter, used to convert between <code>FE</code>s and
	 *                  <code>TE</code>s.
	 * @param dest      The destination {@link Collection}.
	 * @return The destination {@link Collection}.
	 */
	public static <FE, TE, T extends Collection<? super TE>> T addAll(final Iterable<? extends FE> from,
			final Function<FE, TE> converter, final T dest) {
		for (final FE fe : from)
			dest.add(converter.apply(fe));
		return dest;
	}

	public static byte[] addToArray(final byte[] arr, final byte... items) {
		requireNonNull(arr, items);
		final byte[] newArr = new byte[arr.length + items.length];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		System.arraycopy(items, 0, newArr, 0, items.length);
		return newArr;
	}

	@SafeVarargs
	public static <T> T[] addToArray(final T[] arr, final T... items) {
		requireNonNull(arr, items);
		final T[] res = array(arr.length + items.length, arr);
		System.arraycopy(items, 0, res, arr.length, items.length);
		return res;
	}

	public @SafeVarargs static <E> E[] array(final int size, final E... elements) {
		return Arrays.copyOf(elements, size);
	}

	@SafeVarargs
	public static <T> T[] array(final T... items) {
		return items;
	}

	public static <T> int binarySearch(int size, final Function<? super Integer, ? extends T> arrayIndexer,
			final Comparator<? super T> comparator, final T key) {
		if (size == 0)
			return 0;
		if (comparator.compare(arrayIndexer.apply(0), key) > 0)
			return -1;
		if (comparator.compare(arrayIndexer.apply(size - 1), key) < 0)
			return -size - 1;

		int l = 0, point = (size + l) / 2;
		for (; l != size; point = (size + l) / 2) {
			final int res = comparator.compare(key, arrayIndexer.apply(point));
			if (res == 0)
				return point;
			if (res < 0)
				size = point;
			else
				l = point;
		}
		return -point - 1;
	}

	@SuppressWarnings("unchecked")
	public static <T> int binarySearch(final T object, final List<? extends T> list, Comparator<? super T> comparator) {
		if (comparator == null)
			comparator = (Comparator<Object>) Comparator.naturalOrder();
		if (list.isEmpty())
			return -1;
		int min = 0, max = list.size() - 1;
		int index = (max - min) / 2 + (max - min & 1) + min;
		while (true) {
			index = (max - min) / 2 + (max - min & 1) + min;
			final int res = comparator.compare(object, list.get(index));
			if (res == 0)
				return index;
			if (res > 0) {
				// Our object is higher, move min up:
				if (max < (min = index + 1))
					return -index - 2;
			} else // Our object is lower, move max down:
			if ((max = index - 1) < min)
				return -index - 1;
		}
	}

	public static int bytesToInt(final byte... bytes) {
		return (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | bytes[3] & 0xff;
	}

	public static long bytesToLong(final byte... bytes) {
		return ((long) bytes[0] & 0xff) << 56 | ((long) bytes[1] & 0xff) << 48 | ((long) bytes[2] & 0xff) << 40
				| ((long) bytes[3] & 0xff) << 32 | ((long) bytes[4] & 0xff) << 24 | ((long) bytes[5] & 0xff) << 16
				| ((long) bytes[6] & 0xff) << 8 | (long) bytes[7] & 0xff;
	}

	@SafeVarargs
	public static <T, L extends Collection<? super T>> L combine(final L dest, final Collection<T>... sources) {
		for (final Collection<T> i : sources)
			dest.addAll(i);
		return dest;
	}

	@SafeVarargs
	public static <T, L extends Collection<? super T>> L combine(final L dest, final Iterable<T>... sources) {
		for (final Iterable<T> i : sources)
			for (final T t : i)
				dest.add(t);
		return dest;
	}

	@SafeVarargs
	public static <T> T[] combine(final T[]... arrays) {
		int ts = 0;
		for (final T[] array : arrays)
			ts += array.length;
		@SuppressWarnings("unchecked")
		final T[] res = (T[]) Array.newInstance(arrays.getClass().getComponentType().getComponentType(), ts);
		for (int i = 0, j = 0; i < arrays.length; j += arrays[i++].length)
			System.arraycopy(arrays[i], 0, res, j, arrays[i].length);
		return res;
	}

	@SafeVarargs
	public static <T> T combineArrays(final T... arrays) {
		@SuppressWarnings("unchecked")
		final T arr = (T) Array.newInstance(arrays.getClass().getComponentType().getComponentType(),
				(int) sumSize(arrays));
		final int c = 0;
		for (final T t : arrays)
			System.arraycopy(t, 0, arr, c, Array.getLength(t));
		return arr;
	}

	@SafeVarargs
	public static <T> Iterator<T> concat(final Iterator<? extends T>... iterators) {
		return new Iterator<T>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				for (; i < iterators.length; i++)
					if (iterators[i].hasNext())
						return true;
				return false;
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return iterators[i].next();
			}

			@Override
			public void remove() {
				if (i < iterators.length)
					iterators[i].remove();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <F, T> T[] convert(final Function<? super F, ? extends T> converter, final F... fs) {
		if (fs == null)
			return null;
		if (fs.length == 0)
			return (T[]) new Object[0];
		else {
			T first;
			T[] res;
			int i = 0;
			LOOP: {
				for (; i < fs.length; i++) {
					first = converter.apply(fs[i]);
					if (first != null) {
						res = (T[]) Array.newInstance(first.getClass(), fs.length);
						break LOOP;
					}
				}
				return (T[]) new Object[fs.length];
			}
			res[i] = first;
			for (; i < fs.length; i++)
				res[i] = converter.apply(fs[i]);
			return res;
		}
	}

	/**
	 * <p>
	 * Deeply copies the provided array. Clones of all array types are made, though
	 * the original reference type values for all other objects are not cloned.
	 * </p>
	 * <p>
	 * This method does not need to have uniform arrays (where all values in the
	 * array are of the exact same type).
	 * </p>
	 *
	 * @param <T>   The array type.
	 * @param array The array to copy. If <code>null</code> is provided, this method
	 *              throws a {@link NullPointerException}. Otherwise, if a non-array
	 *              value is provided, this method returns that value.
	 * @return The cloned array, with all arrays recursively contained therein also
	 *         cloned.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepCopy(final T array) {
		final Class<? extends Object> cls = array.getClass();
		if (!cls.isArray())
			return array;
		final int len = Array.getLength(array);
		if (cls.getComponentType().isPrimitive()) {
			final T c = (T) Array.newInstance(cls.getComponentType(), len);
			System.arraycopy(array, 0, c, 0, len);
			return c;
		} else {
			final Object[] arr = (Object[]) array;
			final Object[] n = (Object[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
			for (int i = 0; i < arr.length; i++)
				n[i] = arr[i] != null && arr[i].getClass().isArray() ? deepCopy(arr[i]) : arr[i];
			return (T) n;
		}
	}

	/**
	 * Deletes each file and its children if the file is a directory.
	 *
	 * @param files The files to delete.
	 */
	public static final void deltree(final File... files) {
		for (final File f : files)
			if (f.isDirectory())
				deltree(f.listFiles());
			else
				f.delete();
	}

	/**
	 * Wraps the value in an array of the provided size with every value in the
	 * array being the provided value.
	 *
	 * @param <T>   The type of the value to put into the new array.
	 * @param value The value to fill the new array with.
	 * @param size  The size of the new array.
	 * @return An array of size size filled with the provided value.
	 */
	public static <T> T[] extend(final T value, final int size) {
		@SuppressWarnings("unchecked")
		final T[] arr = (T[]) Array.newInstance(value.getClass(), size);
		Arrays.fill(arr, value);
		return arr;
	}

	public static <T> void fill(final T[][] arr, final T value) {
		for (final T[] a : arr)
			Arrays.fill(a, value);
	}

	public static double findMedian(final double... values) {
		final double[] copy = values.clone();
		return findMedianUnsafe(copy);
	}

	public static <E extends Comparable<? extends E>> Pair<E, E> findMedian(final List<? extends E> items) {
		return findMedianUnsafe(new ArrayList<>(items));
	}

	public static <E> Pair<E, E> findMedian(final List<? extends E> items, final Comparator<E> comparator) {
		return findMedianUnsafe(new ArrayList<>(items), comparator);
	}

	public static double findMedianUnsafe(final double... values) {
		// TODO Improve
		if (values == null)
			throw null;
		if (values.length == 0)
			return 0;
		Arrays.sort(values);
		return (values.length & 1) == 0 ? (values[values.length / 2] + values[values.length / 2 - 1]) / 2
				: values[values.length / 2];
	}

	public static <E extends Comparable<? extends E>> Pair<E, E> findMedianUnsafe(final List<? extends E> items) {
		return findMedianUnsafe(items, null);
	}

	public static <E> Pair<E, E> findMedianUnsafe(final List<? extends E> items, final Comparator<E> comparator) {
		items.sort(comparator);
		return getMidElement(items);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] flatten(final T[][] multarr) {
		final List<T> elements = flattenToList(multarr);
		return elements.toArray(
				(T[]) Array.newInstance(multarr.getClass().getComponentType().getComponentType(), elements.size()));
	}

	public static <T> List<T> flattenToList(final T[][] multarr) {
		final List<T> elements = new ArrayList<>();
		for (final T[] a : multarr)
			Collections.addAll(elements, a);
		return elements;
	}

	public static <E> Map<E, Integer> frequencyMap(final Iterable<? extends E> itr) {
		return frequencyMap(itr.iterator(), new HashMap<>());
	}

	public static <E, M extends Map<? super E, Integer>> M frequencyMap(final Iterable<? extends E> itr,
			final M freqmap) {
		return frequencyMap(itr.iterator(), freqmap);
	}

	public static <E> Map<E, Integer> frequencyMap(final Iterator<? extends E> itr) {
		return frequencyMap(itr, new HashMap<>());
	}

	public static <E, M extends Map<? super E, Integer>> M frequencyMap(final Iterator<? extends E> itr,
			final M freqmap) {
		for (; itr.hasNext();) {
			final E val = itr.next();
			if (freqmap.containsKey(val))
				freqmap.put(val, freqmap.get(val) + 1);
			else
				freqmap.put(val, 1);
		}
		return freqmap;
	}

	/**
	 * Returns a {@link Pair} object if the given list is not empty or
	 * <code>null</code>. Under these circumstances, if the given list's size is
	 * even, the {@link Pair} returned will contain the items in the list at indexes
	 * <code>list.size() / 2 - 1</code> and <code>list.size() / 2</code>,
	 * respectively. If the list's size is odd, the {@link Pair} will consist of the
	 * item in the list at index <code>list.size() / 2</code>, and
	 * <code>null</code>, respectively. If the list is empty, this method returns
	 * <code>null</code>, and if the given list is <code>null</code>, this method
	 * throws <code>null</code>.
	 *
	 * @param <E>   The type of object held by the given list.
	 * @param items The list of items to get the middle element(s) of.
	 * @return A {@link Pair} consisting of both of the middle elements if the
	 *         list's size is even, or the middle element if the list's size is odd.
	 */
	private static <E> Pair<E, E> getMidElement(final List<? extends E> items) {
		if (items == null)
			throw null;
		if (items.isEmpty())
			return null;
		if ((items.size() & 1) == 0) {
			final Iterator<? extends E> itr = items.listIterator(items.size() / 2 - 1);
			return new Pair<>(itr.next(), itr.next());
		}
		return new Pair<>(items.get(items.size() / 2), null);
	}

	/**
	 * Cleanly maps two integers between and including <code>-32768</code> and
	 * <code>32767</code> to a new int. If either input integer is greater in
	 * magnitude than the former number with same sign, this method first reduces
	 * the input using the Java remainder operator so that it does fit the
	 * aforementioned range.
	 *
	 * @param f The first input integer.
	 * @param s The second input integer.
	 * @return The hashed int.
	 */
	public static int hash(int f, int s) {
		return (f = (Integer.MIN_VALUE & f) == 0 ? 2 * (f % 32768)
				: -2 * ((f + 1) % 32768 - 1) - 1) >= (s = (Integer.MIN_VALUE & s) == 0 ? 2 * (s % 32768)
						: -2 * ((s + 1) % 32768 - 1) - 1) ? f * f + f + s : f + s * s;
	}

	public static byte[] intToBytes(final int i) {
		return new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i };
	}

	@SafeVarargs
	public static <E> Iterable<E> iterable(final E... arr) {
		return () -> new Iterator<E>() {

			int pos;

			@Override
			public boolean hasNext() {
				return pos < arr.length;
			}

			@Override
			public E next() {
				return arr[pos++];
			}
		};
	}

	@SafeVarargs
	public static <T> Iterable<T> iterable(final Iterable<? extends T>... iterables) {
		return () -> iterator(iterables);
	}

	public static <E> Iterable<E> iterable(final Iterator<E> itr) {
		return () -> itr;
	}

	@SafeVarargs
	public static <T> Iterator<T> iterator(final Iterable<? extends T>... iterables) {
		@SuppressWarnings("unchecked")
		final Iterator<? extends T>[] itrs = new Iterator[iterables.length];
		for (int i = 0; i < iterables.length; i++)
			itrs[i] = iterables[i].iterator();
		return concat(itrs);
	}

	@SafeVarargs
	public static <T> Iterator<T> iterator(final T... items) {
		return iterable(items).iterator();
	}

	public static <T> Iterator<T> iterator(final T[][] elems) {
		@SuppressWarnings("unchecked")
		final Iterator<T>[] itrs = new Iterator[elems.length];
		for (int i = 0; i < elems.length; i++)
			itrs[i] = iterator(elems[i]);
		return concat(itrs);
	}

	public static byte[] longToBytes(final long l) {
		return new byte[] { (byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32),
				(byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l };
	}

	public static <F, T> Iterable<T> mask(final Iterable<? extends F> itr,
			final Function<? super F, ? extends T> conv) {
		return () -> mask(itr.iterator(), conv);
	}

	public static <F, T> Iterator<T> mask(final Iterator<? extends F> itr,
			final Function<? super F, ? extends T> conv) {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return itr.hasNext();
			}

			@Override
			public T next() {
				return conv.apply(itr.next());
			}
		};
	}

	public static int maxPage(final int itemsPerPage, final int listSize) {
		return (listSize + itemsPerPage - 1) / itemsPerPage;
	}

	public static int maxPage(final int itemsPerPage, final List<?> items) {
		return maxPage(itemsPerPage, items.size());
	}

	public static <E> List<E> paginate(final int page, final int itemsPerPage, final List<E> items) {
		if (items.size() == 0 && page == 1)
			return items.subList(0, 0);
		final int item = (page - 1) * itemsPerPage;
		final int maxPage = maxPage(itemsPerPage, items);
		if (page < 1 || page > maxPage)
			return null;

		return items.subList(item, Math.min(item + itemsPerPage, items.size()));
	}

	@SafeVarargs
	public static final <T> T pickRandomElement(final T... ts) {
		return ts[(int) (Math.random() * ts.length)];
	}

	public static String printInEnglish(final Iterator<?> itr, final boolean and) {
		final StringBuilder builder = new StringBuilder();
		if (itr.hasNext()) {
			builder.append(itr.next());
			if (itr.hasNext()) {
				Object o = itr.next();
				if (!itr.hasNext())
					builder.append(' ' + (and ? "and" : "or") + ' ').append(o);
				else {
					Object two = itr.next();
					while (itr.hasNext()) {
						builder.append(", ").append(o);
						o = two;
						two = itr.next();
					}
					builder.append(", ").append(o).append(", " + (and ? "and" : "or") + ' ').append(two);
				}
			}
		}
		return builder.toString();
	}

	public static <K, V, L extends List<? super V>> void putIntoListMap(final Map<K, L> map, final K key, final V value,
			final Supplier<L> listmaker) {
		L l = map.get(key);
		if (l == null)
			map.put(key, l = listmaker.get());
		l.add(value);
	}

	public static byte[] read(final InputStream in) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] bytes = new byte[65536];
		int x;
		while ((x = in.read(bytes)) != -1)
			baos.write(bytes, 0, x);
		return baos.toByteArray();
	}

	public static String readText(final InputStream is) {
		final Scanner s = new Scanner(is);
		final StringBuilder sb = new StringBuilder();
		while (s.hasNextLine())
			sb.append(s.nextLine()).append('\n');
		s.close();
		return sb.toString();
	}

	public static <K, V> void removeFromListMap(final Map<K, ? extends Collection<? super V>> map, final K key,
			final V value) {
		final Collection<? super V> coll = map.get(key);
		if (coll != null) {
			coll.remove(value);
			if (coll.isEmpty())
				map.remove(key);
		}
	}

	/**
	 * Throws a {@link NullPointerException} if any of the objects provided are
	 * <code>null</code>.
	 *
	 * @param objects The array of objects.
	 * @author Palanath
	 */
	public static void requireNonNull(final Object... objects) {
		for (final Object o : objects)
			if (o == null)
				throw null;
	}

	public static BigInteger sumFrequencyMap(final Map<?, Integer> multipliersModifiable) {
		BigInteger sum = BigInteger.ZERO;
		for (final int i : multipliersModifiable.values())
			sum = sum.add(BigInteger.valueOf(i));
		return sum;
	}

	public static long sumSize(final Object... arrays) {
		long sum = 0;
		for (final Object o : arrays)
			sum += Array.getLength(o);
		return sum;
	}

	public static <E> Stack<E> unmodifiableStack(final Stack<E> stack) {

		return new Stack<E>() {

			/**
			 * SUID
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public synchronized boolean add(final E e) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public void add(final int index, final E element) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean addAll(final Collection<? extends E> c) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean addAll(final int index, final Collection<? extends E> c) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void addElement(final E obj) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void ensureCapacity(final int minCapacity) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void insertElementAt(final E obj, final int index) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized Iterator<E> iterator() {
				return unmodifyingIterator(super.iterator());
			}

			@Override
			public synchronized ListIterator<E> listIterator() {
				return unmodifyingListIterator(super.listIterator());
			}

			@Override
			public synchronized ListIterator<E> listIterator(final int index) {
				return unmodifyingListIterator(super.listIterator(index));
			}

			@Override
			public synchronized E remove(final int index) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public boolean remove(final Object o) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean removeAll(final Collection<?> c) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void removeAllElements() {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean removeElement(final Object obj) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void removeElementAt(final int index) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean removeIf(final Predicate<? super E> filter) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			protected synchronized void removeRange(final int fromIndex, final int toIndex) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void replaceAll(final UnaryOperator<E> operator) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized boolean retainAll(final Collection<?> c) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized E set(final int index, final E element) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void setElementAt(final E obj, final int index) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void setSize(final int newSize) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void sort(final Comparator<? super E> c) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public Spliterator<E> spliterator() {
				return super.spliterator();
			}

			@Override
			public synchronized List<E> subList(final int fromIndex, final int toIndex) {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

			@Override
			public synchronized void trimToSize() {
				throw new UnsupportedOperationException(
						"This Stack is unmodifiable; the requested operation is invalid.");
			}

		};
	}

	public static <E> Iterator<E> unmodifyingIterator(final Iterator<E> itr) {
		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return itr.hasNext();
			}

			@Override
			public E next() {
				return itr.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"This Iterator can't modify its underlying Collection; that operation is invalid.");
			}
		};
	}

	public static <E> ListIterator<E> unmodifyingListIterator(final ListIterator<E> base) {
		return new ListIterator<E>() {

			@Override
			public void add(final E e) {
				throw new UnsupportedOperationException(
						"This ListIterator can't modify its underlying Collection; that operation is invalid.");
			}

			@Override
			public boolean hasNext() {
				return base.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return base.hasNext();
			}

			@Override
			public E next() {
				return base.next();
			}

			@Override
			public int nextIndex() {
				return base.nextIndex();
			}

			@Override
			public E previous() {
				return base.previous();
			}

			@Override
			public int previousIndex() {
				return base.previousIndex();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"This ListIterator can't modify its underlying Collection; that operation is invalid.");
			}

			@Override
			public void set(final E e) {
				throw new UnsupportedOperationException(
						"This ListIterator can't modify its underlying Collection; that operation is invalid.");
			}
		};
	}

	private JavaTools() {
	}
}
