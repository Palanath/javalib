package pala.libs.generic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import pala.libs.generic.util.Box;
import pala.libs.generic.util.FallibleSupplier;
import pala.libs.generic.util.Pair;
import pala.libs.generic.util.functions.BiBooleanFunction;

public final class JavaTools {

	private static DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER;

	public static DateTimeFormatter getSimpleDateTimeFormatter() {
		return SIMPLE_DATE_TIME_FORMATTER == null
				? SIMPLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd kk:mm:ss")
				: SIMPLE_DATE_TIME_FORMATTER;
	}

	public interface Indexable<E> {
		void set(int pos, E element);

		int index(E element);
	}

	public static <E> void copyTo(E[] destArray, int destFrom, int numElements, Iterator<? extends E> items) {
		for (int i = destFrom; i < destFrom + numElements; ++i)
			destArray[i] = items.next();
	}

	public static <T> Iterable<T> reverseIterable(List<? extends T> list) {
		return () -> new Iterator<T>() {
			private final ListIterator<? extends T> li = list.listIterator(list.size());

			@Override
			public boolean hasNext() {
				return li.hasPrevious();
			}

			@Override
			public T next() {
				return li.previous();
			}

			@Override
			public void remove() {
				li.remove();
			}
		};
	}

	public static <E> void swap(Indexable<E> indexable, E element, E newValue) {
		indexable.set(indexable.index(element), newValue);
	}

	public static <E> void swap(E[] array, E element, E newValue) {
		swap(new Indexable<E>() {

			@Override
			public void set(int pos, E element) {
				array[pos] = element;
			}

			@Override
			public int index(E element) {
				for (int i = 0; i < array.length; i++)
					if (array[i] == element)
						return i;
				return -1;
			}
		}, element, newValue);
	}

	public static <E> void swap(List<E> list, E element, E newValue) {
		swap(new Indexable<E>() {

			@Override
			public void set(int pos, E element) {
				list.set(pos, element);
			}

			@Override
			public int index(E element) {
				return list.indexOf(element);
			}
		}, element, newValue);
	}

	/**
	 * <p>
	 * Determines if the provided <code>T</code> is contained in the specified
	 * array. If so, returns the first index in the array at which the provided
	 * element is contained. If not, returns <code>-1</code>.
	 * </p>
	 * <p>
	 * This function uses {@link Objects#equals(Object, Object)} to check object
	 * equality.
	 * </p>
	 * 
	 * @param <T>      The type of array to search.
	 * @param needle   The item to search for.
	 * @param haystack The array to search through.
	 * @return <code>-1</code> if <code>needle</code> is not found in
	 *         <code>haystack</code>, otherwise, the index of the first occurrence
	 *         of <code>needle</code> in <code>haystack</code>.
	 */
	@SafeVarargs
	public static <T> int indexOf(T needle, T... haystack) {
		for (int i = 0; i < haystack.length; i++)
			if (Objects.equals(needle, haystack[i]))
				return i;
		return -1;
	}

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

	@SuppressWarnings("unchecked")
	public static <T> int binarySearch(int size, final Function<? super Integer, ? extends T> arrayIndexer,
			Comparator<? super T> comparator, final T key) {
		final Comparator<? super T> c = comparator == null ? (Comparator<Object>) Comparator.naturalOrder()
				: comparator;
		return binarySearch(size, a -> {
			T obj = arrayIndexer.apply(a);
			int res = c.compare(obj, key);
			return res == 0 ? null : c.compare(obj, key) > 0;
		});
	}

	/**
	 * <h2>Overview</h2>
	 * <p>
	 * Performs a generic binary search. The provided <code>size</code> is used to
	 * know the original maximum bound for the binary search (the binary search will
	 * start by querying the provided {@link Function} with half the
	 * <code>size</code> as initial input). The provided {@link Function}
	 * <code>checker</code> is used to determine if the value at the queried
	 * position is less than, greater than, or equal to the object being searched
	 * for. This {@link Function} encodes both the abstract collection of items
	 * being searched <i>and</i> comparisons against the object being searched for
	 * and elements in the abstract collection.
	 * </p>
	 * <p>
	 * Upon any invocation of the provided {@link Function} with an {@link Integer}
	 * position as an argument, the {@link Function} should return:
	 * </p>
	 * <ul>
	 * <li><code>true</code> if the element at the <i>queried index</i> (the index
	 * provided to the {@link Function}) is <i>greater than</i> the element being
	 * searched for,</li>
	 * <li><code>false</code> if the element at the <i>queried index</i> is <i>less
	 * than</i> the element being searched for,</li>
	 * <li>and <code>null</code> if the element at the <i>queried index</i> is
	 * equivalent in magnitude to the element being searched for.</li>
	 * </ol>
	 * <h2>Example</h2>
	 * <p>
	 * Consider a simple <code>Dog</code> class:
	 * </p>
	 * 
	 * <pre>
	 * <code>class Dog implements Comparable&lt;Dog&gt; {
	 * 	private final int weight = (int) (Math.random() * 1000);
	 * 
	 * 	public &#64;Override int compareTo(Dog o) {
	 * 		return weight - o.weight;
	 * 	}
	 * }</code>
	 * </pre>
	 * <p>
	 * To binary search a {@link List} of <code>Dog</code>s, denoted
	 * <code>dogs</code>, as so:
	 * </p>
	 * 
	 * <pre>
	 * <code>List&lt;Dogs&gt; dogs = new ArrayList<>();
	 * dogs.sort(null);
	 * for (int i = 0; i < 100; i++, dogs.add(new Dog()));</code>
	 * </pre>
	 * 
	 * <p>
	 * This method would be called as follows:
	 * </p>
	 * 
	 * <pre>
	 * <code>// Dog being searched for:
	 * Dog searchElement = dogs.get((int) (Math.random() * dogs.size()));
	 * 
	 * int index = binarySearch(dogs.size(), ind -> {
	 * 	Dog dog = dogs.get(ind);
	 * 	int res = dog.compareTo(searchElement);
	 * 	if (res > 0) // dog > searchElement
	 * 		return true;
	 * 	else if (res == 0) // dog == searchElement
	 * 		return null;
	 * 	else // dog < searchElement
	 * 		return false;
	 * });</code>
	 * </pre>
	 * 
	 * @param size    The total number of elements to search.
	 * @param checker The {@link Function} used to obtain and check items from the
	 *                collection of elements.
	 * @return The index of the element, if found, or <code>-index - 1</code>, where
	 *         <code>index</code> is the position that the element would be in if it
	 *         were present in the sorted collection.
	 */
	public static int binarySearch(int size, Function<? super Integer, Boolean> checker) {
		if (size == 0)
			return -1;
		int min = 0, max = size - 1, ind;
		while (true) {
			Boolean res = checker.apply(ind = (max - min) / 2 + (max - min & 1) + min);
			if (res == null)
				return ind;
			else if (res) {// get(ind) > searchElement
				if ((max = ind - 1) < min)
					return -ind - 1;
			} else if (max < (min = ind + 1))
				return -ind - 2;
		}
	}

	public static <T> int binarySearch(final T object, final List<? extends T> list, Comparator<? super T> comparator) {
		return binarySearch(list.size(), list::get, comparator, object);
	}

	public interface Interpolator<T> {
		T[] interpolate(T left, T right);
	}

	@SafeVarargs
	public static <T> int max(Comparator<? super T> ranker, T... items) {
		int maxind = 0;
		for (int i = 1; i < items.length; i++)
			if (ranker.compare(items[maxind], items[i]) < 0)
				maxind = i;
		return maxind;
	}

	@SafeVarargs
	public static <T> int min(Comparator<? super T> ranker, T... items) {
		int minind = 0;
		for (int i = 1; i < items.length; i++)
			if (ranker.compare(items[minind], items[i]) > 0)
				minind = i;
		return minind;
	}

	/**
	 * <h2>Overview</h2>
	 * <p>
	 * Optimizes for the value on a 1D surface that is captured by the provided
	 * {@link Comparator} ranker. The surface's maximum value between (including)
	 * the provided <code>lower</code> and <code>upper</code> bounds is searched
	 * for.
	 * </p>
	 * <h2>Description</h2>
	 * <p>
	 * This method operates on a conceptual, 1-input function whose graph looks like
	 * a mountain (a <code>V</code> shape, but upside down). This function attempts
	 * to iteratively narrow the range containing the optimal value by comparing the
	 * outputs of the function at different points along the current range.
	 * </p>
	 * <h3>Single Pass</h3>
	 * <p>
	 * The method begins with the provided range (from <code>lower</code> to
	 * <code>upper</code>). The <code>lower</code> and <code>upper</code> values,
	 * defining the range, represent inputs to the function being optimized.
	 * </p>
	 * <p>
	 * This method interpolates the range using the provided {@link Interpolator}
	 * and expects to receive a number of values. (The values need not be
	 * contiguous.)
	 * </p>
	 * <p>
	 * This method then finds the two values, out of those returned, that rank the
	 * highest using the provided {@link Comparator}. These two values are then set
	 * to define the range for the next cycle, or are returned as a {@link Pair} if
	 * as many cycles as desired has elapsed.
	 * </p>
	 * <h2>Notes</h2>
	 * <ol>
	 * <li>The number of values returned by the {@link Interpolator} should be at
	 * least 2, so a new range can be defined. The number of values returned can be
	 * anything no less than 2, however. This algorithm will pick the two
	 * highest-ranking values and use those to define the new range.</li>
	 * <li>The values returned by the {@link Interpolator} are not considered to be
	 * sorted. The {@link Interpolator} can return them in any order.
	 * <ul>
	 * <li>A method could be devised which utilizes order in values returned from an
	 * {@link Interpolator} to make fewer calls to the {@link Comparator}
	 * ranker.</li>
	 * </ul>
	 * </li>
	 * <li>This method calls the {@link Comparator} frequently and does not perform
	 * inter-cycle caching. If the {@link Comparator} is costly to invoke, callers
	 * can implement caching into the {@link Comparator}.</li>
	 * </ol>
	 * 
	 * 
	 * @param <T>          The type of point on the 1D surface.
	 * @param lower        The lower bound of the surface (defining one end of the
	 *                     range of the line to search over).
	 * @param upper        The upper bound of the surface (defining the other end of
	 *                     the range of the line to search over).
	 * @param interpolator A function that returns points to search over between two
	 *                     provided points. The points in this list should be
	 *                     ordered along the 1D surface (line) that they reside on.
	 *                     If the interpolator returns 1 value, that value is
	 *                     returned as both elements of a {@link Pair} from this
	 *                     method. If the interpolator returns 0 values, this method
	 *                     throws an {@link IllegalArgumentException}, (signifying
	 *                     that the {@link Interpolator} is not valid for this
	 *                     method).
	 * @param ranker       A {@link Comparator} that determines which of two values
	 *                     is greater.
	 * @param rounds       The number of cycles the optimization algorithm should
	 *                     perform.
	 * @return A smaller range containing the optimal value.
	 */
	public static <T> Pair<T, T> optimizeForMax(T lower, T upper, Interpolator<T> interpolator,
			Comparator<? super T> ranker, int rounds) {
		return optimizeForMax(lower, upper, a -> a, interpolator, ranker, rounds);
	}

	public static <T, O> Pair<T, T> optimizeForMax(T lower, T upper, Function<? super T, ? extends O> converter,
			Interpolator<T> interpolator, Comparator<? super O> ranker, int rounds) {
		return optimizeForMax(lower, upper, converter, interpolator, ranker, rounds, a -> {
		});
	}

	public static <T, O> Pair<T, T> optimizeForMax(T lower, T upper, Function<? super T, ? extends O> converter,
			Interpolator<T> interpolator, Comparator<? super O> ranker, int rounds,
			Consumer<? super Pair<T, T>> cycleHandler) {
		return optimizeForMax(lower, upper, converter, interpolator, ranker, rounds, cycleHandler, (a, b) -> false);
	}

	public static <T, O> Pair<T, T> optimizeForMax(T lower, T upper, Function<? super T, ? extends O> converter,
			Interpolator<T> interpolator, Comparator<? super O> ranker, int rounds,
			Consumer<? super Pair<T, T>> cycleHandler, BiBooleanFunction<? super T, ? super T> earlyStoppingCondition) {
		if (cycleHandler == null)
			cycleHandler = a -> {
			};
		if (earlyStoppingCondition == null)
			earlyStoppingCondition = (a, b) -> false;

		for (int l = 0; l < rounds; l++) {
			T[] items = interpolator.interpolate(lower, upper);
			O[] arr = JavaTools.convert(converter, items);
			if (arr.length == 0)
				throw new IllegalArgumentException("Interpolator returned too few values.");
			else if (arr.length == 1)
				return new Pair<>(items[0], items[0]);
			int largest = 0, secondLargest = 1;
			if (ranker.compare(arr[largest], arr[secondLargest]) < 0) {
				int t = largest;
				largest = secondLargest;
				secondLargest = t;
			}
			for (int i = 2; i < arr.length; i++)
				if (ranker.compare(arr[largest], arr[i]) < 0) {
					secondLargest = largest;
					largest = i;
				} else if (ranker.compare(arr[secondLargest], arr[i]) < 0)
					secondLargest = i;
			cycleHandler.accept(new Pair<>(items[secondLargest], items[largest]));
			// Early stop if the bounds we found are the same as the ones we last called the
			// interpolator with.
			if (earlyStoppingCondition.apply(lower = items[secondLargest], upper = items[largest]))
				return new Pair<>(lower, upper);
		}
		return new Pair<>(lower, upper);
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
	public static <T> T[] combine(T[] array1, T... array2) {
		return combine(array(array1, array2));
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
		deltree(null, files);
	}

	public static final void deltree(BiConsumer<File, Boolean> callback, File... files) {
		walktree(callback == null ? File::delete : a -> callback.accept(a, a.delete()), files);
	}

	/**
	 * Iterates over every file and its children if the file is a directory.
	 * Children are always iterated over before the parent. When a file is iterated
	 * over, the provided {@link Consumer} is called and the {@link File} is
	 * provided to it. This function can be used to delete a whole directory tree or
	 * set of directory trees through calling {@link File#delete()} in the
	 * {@link Consumer}.
	 * 
	 * @param walker The {@link Consumer} to receive each file being walked over.
	 *               This should not be <code>null</code>.
	 * @param files  The {@link File}s or {@link File} trees to walk over.
	 */
	public static final void walktree(Consumer<? super File> walker, File... files) {
		for (File f : files) {
			if (f.isDirectory())
				walktree(walker, f.listFiles());
			walker.accept(f);
		}
	}

	public static final void walktree(Consumer<? super File> walker, Iterable<? extends File> files) {
		for (File f : files) {
			if (f.isDirectory())
				walktree(walker, f.listFiles());
			walker.accept(f);
		}
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

	/**
	 * Returns an {@link Iterator} that skips over any elements from the provided
	 * {@link Iterator} for which the provided {@link Predicate} returns
	 * <code>true</code>. Both {@link Iterator#hasNext()} and
	 * {@link Iterator#next()} work on this {@link Iterator}, though
	 * {@link Iterator#remove()} does not.
	 * 
	 * @param <T>    The type of the {@link Iterator}.
	 * @param itr    The {@link Iterator} to filter.
	 * @param filter The {@link Predicate} to use as a filter. Each element that the
	 *               {@link Predicate} returns <code>true</code> for is filtered out
	 *               (skipped over).
	 * @return The new {@link Iterator} that is filtered.
	 */
	public static <T> Iterator<T> filter(Iterator<? extends T> itr, Predicate<? super T> filter) {
		return new Iterator<T>() {
			private Box<T> cache;

			@Override
			public boolean hasNext() {
				while (cache == null) {
					if (itr.hasNext()) {
						T temp = itr.next();
						if (!filter.test(temp))
							cache = new Box<>(temp);
					} else
						return false;
				}
				return true;
			}

			@Override
			public T next() {
				if (hasNext()) {
					T temp = cache.value;
					cache = null;
					return temp;
				} else
					throw new NoSuchElementException();
			}
		};
	}

	public static <T> Iterable<T> filter(Iterable<? extends T> itr, Predicate<? super T> filter) {
		return () -> filter(itr.iterator(), filter);
	}

	/**
	 * <p>
	 * Returns an {@link Iterable} that filters the provided {@link Iterable}
	 * according to the provided {@link Predicate} filter, but then casts the
	 * argument to the specified type, <code>Q</code>.
	 * </p>
	 * <p>
	 * This {@link Function} is intended to be used with {@link Predicate}s that
	 * filter out any values from the {@link Iterable} that are not of a specified
	 * type, that type being <code>Q</code>. If the {@link Predicate} allows any
	 * elements that are not an instance of type <code>Q</code> to pass through, the
	 * result may be unsafe.
	 * </p>
	 * 
	 * <pre>
	 * <code>Iterable&lt;Number&gt; x = ...;
	 * Iterable&lt;Integer&gt; a = filterToType(x, a->a instanceof Integer);</code>
	 * </pre>
	 * 
	 * <p>
	 * This function essentially performs what {@link #filter(Iterable, Predicate)}
	 * does but casts each item let through the filter.
	 * </p>
	 * 
	 * @param <T>    The type of the source {@link Iterable}.
	 * @param <Q>    The type of the result {@link Iterable}.
	 * @param itr    The source {@link Iterable}.
	 * @param filter The filter to apply.
	 * @return The resulting {@link Iterable}.
	 */
	@SuppressWarnings("unchecked")
	public static <T, Q extends T> Iterable<Q> filterToType(Iterable<? extends T> itr, Predicate<? super T> filter) {
		return (Iterable<Q>) filter(itr, filter);
	}

	@SuppressWarnings("unchecked")
	public static <T, Q extends T> Iterator<Q> filterToType(Iterator<? extends T> itr, Predicate<? super T> filter) {
		return (Iterator<Q>) filter(itr, filter);
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

	public static final class Collector<T> implements Consumer<T> {
		public final List<T> items = new ArrayList<>();

		@Override
		public void accept(T t) {
			items.add(t);
		}

		public List<T> getItems() {
			return items;
		}
	}

	public static List<String> readLines(InputStream is) {
		return collectFromWithin(a -> readLines(is, a));
	}

	public static <T> List<T> collectFromWithin(Consumer<? super Consumer<? super T>> consumer) {
		Collector<T> t = new Collector<>();
		consumer.accept(t);
		return t.items;
	}

	public static <T> List<T> collect(Iterator<? extends T> items) {
		List<T> res = new ArrayList<>();
		while (items.hasNext())
			res.add(items.next());
		return res;
	}

	public static <T> List<T> collect(Iterable<? extends T> items) {
		List<T> res = new ArrayList<>();
		for (T t : items)
			res.add(t);
		return res;
	}

	public static void readLines(InputStream is, Consumer<? super String> lineHandler) {
		try (final Scanner s = new Scanner(is)) {
			while (s.hasNextLine())
				lineHandler.accept(s.nextLine());
		}
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

	@SafeVarargs
	public static <T> T[][] listProd(T[]... lists) {
		int dim = 1;// Number of lists there will be. One list per element combination.
		for (T[] s : lists)
			dim *= s.length;
		@SuppressWarnings("unchecked")
		T[][] result = (T[][]) Array.newInstance(lists.getClass().getComponentType().getComponentType(), dim,
				lists.length);

		int dupecount = 1;
		for (int i = lists.length - 1; i >= 0; dupecount *= lists[i--].length)
			for (int j = 0; j < result.length; j++)
				result[j][i] = lists[i][(j / dupecount) % lists[i].length];

		return result;
	}

	@SuppressWarnings("unchecked")
	public static <V, T extends Throwable> V hideCheckedExceptions(FallibleSupplier<?, ? extends V> computation)
			throws T {
		try {
			return computation.get();
		} catch (Throwable e) {
			throw (T) e;
		}
	}

	private JavaTools() {
	}

	/**
	 * Invokes the first {@link Supplier}. If it returns a non-<code>null</code>
	 * value, returns that value. Otherwise, invokes and returns the result of the
	 * second {@link Supplier}.
	 * 
	 * @param <T>           The type of the value to get.
	 * @param supplier      The first {@link Supplier} to try.
	 * @param otherSupplier The second {@link Supplier} to try as a fallback if the
	 *                      first one returns <code>null</code>.
	 * @return The result.
	 */
	public static <T> T getFallback(Supplier<? extends T> supplier, Supplier<? extends T> otherSupplier) {
		T t;
		if ((t = supplier.get()) != null)
			return t;
		return otherSupplier.get();
	}

	/**
	 * <p>
	 * Invokes the first {@link Supplier}. If it returns a non-<code>null</code>
	 * value, returns that value. Otherwise, if another {@link Supplier} is
	 * provided, repeats the process with this {@link Supplier}, moving on to the
	 * third if provided and the second returns <code>null</code>. If all
	 * {@link Supplier}s return <code>null</code>, this function returns
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * This function calls each {@link Supplier} in order and does not handle
	 * exceptions; {@link Supplier} exceptions are propagated to the caller.
	 * </p>
	 * 
	 * @param <T>       The type of the result.
	 * @param supplier  The first {@link Supplier} to invoke.
	 * @param fallbacks The remaining {@link Supplier}s.
	 * @return The result, or <code>null</code> if all {@link Supplier}s return
	 *         <code>null</code>.
	 */
	@SafeVarargs
	public static <T> T getFallback(Supplier<? extends T> supplier, Supplier<? extends T>... fallbacks) {
		T t;
		if ((t = supplier.get()) != null)
			return t;
		else
			for (Supplier<? extends T> s : fallbacks)
				if ((t = s.get()) != null)
					return t;
		return t;
	}

	/**
	 * <p>
	 * Iterates over the {@link Map}'s keys and replaces each of them with the key
	 * returned by the specified {@link Function}.
	 * </p>
	 * <p>
	 * For every entry in the {@link Map}, the entry's key is given to the specified
	 * {@link Function}, the entry is removed, and the value of the entry is placed
	 * under the new key (returned by the specified {@link Function}) in the
	 * {@link Map}.
	 * </p>
	 * 
	 * @param <K>      The type of keys stored in the {@link Map}.
	 * @param <V>      The type of values stored in the {@link Map}.
	 * @param replacer The function that is used to derive the new key from the old
	 *                 key.
	 * @param map      The {@link Map} to perform the replacement on.
	 */
	public static <K, V> void replaceKeys(Function<? super K, ? extends K> replacer, Map<K, V> map) {
		replaceKeysByEntry(a -> replacer.apply(a.getKey()), map);
	}

	/**
	 * Same as {@link #replaceKeys(Function, Map)}, but the provided
	 * {@link Function} is given the entire {@link Map} {@link Entry} to derive the
	 * new key from.
	 * 
	 * @param <K>      The type of keys stored in the {@link Map}.
	 * @param <V>      The type of values stored in the {@link Map}.
	 * @param replacer {@link Function} that, for each {@link Entry} in the
	 *                 {@link Map}, is given the {@link Entry} and is expected to
	 *                 return the new key that the value of the {@link Entry} should
	 *                 be placed at.
	 * @param map      The {@link Map} to perform the replacement on.
	 */
	public static <K, V> void replaceKeysByEntry(Function<Entry<K, V>, ? extends K> replacer, Map<K, V> map) {
		Collection<Entry<K, V>> entries = new ArrayList<>(map.entrySet());
		for (Entry<K, V> t : entries)
			map.put(replacer.apply(t), map.remove(t.getKey()));
	}

	public static <V> void replaceValues(Function<? super V, ? extends V> replacer, Map<?, V> map) {
		replaceValuesByEntry(a -> replacer.apply(a.getValue()), map);
	}

	public static <K, V> void replaceValuesByEntry(Function<? super Entry<K, V>, ? extends V> replacer, Map<K, V> map) {
		for (Entry<K, V> e : map.entrySet())
			e.setValue(replacer.apply(e));
	}

	public static String toFormattedString(Map<?, ?> map) {
		return toFormattedString(map, "{\n\t", "\n\t", " : ", "\n}");
	}

	public static String toFormattedString(Map<?, ?> map, String openBrace, String entryDelimiter,
			String keyValueDelimiter, String closeBrace) {
		StringBuilder sb = new StringBuilder(openBrace);
		Iterator<? extends Entry<?, ?>> itr = map.entrySet().iterator();
		if (itr.hasNext()) {
			Entry<?, ?> x = itr.next();
			sb.append(x.getKey()).append(keyValueDelimiter).append(x.getValue());
			while (itr.hasNext()) {
				x = itr.next();
				sb.append(entryDelimiter).append(x.getKey()).append(keyValueDelimiter).append(x.getValue());
			}
		}
		return sb.append(closeBrace).toString();
	}

	public static void mkParentDirs(File f) {
		File pf = f.getParentFile();
		if (pf != null)
			pf.mkdirs();
	}

	public static void mkParentDirs(File... files) {
		for (File f : files)
			mkParentDirs(f);
	}

	/**
	 * <p>
	 * Creates the directory for the specified file and opens a {@link PrintWriter}
	 * to the specified file <code>location</code> and writes the provided
	 * {@link String} to it. No additional trailing line is added
	 * ({@link PrintWriter#print(String)} is used rather than
	 * {@link PrintWriter#println(String)}).
	 * </p>
	 * <p>
	 * This function creates
	 * 
	 * @param text     The text to write.
	 * @param location The location to write the text to.
	 * @throws FileNotFoundException If the file could not be created and written to
	 *                               (if {@link PrintWriter#PrintWriter(String)}
	 *                               throws a {@link FileNotFoundException}).
	 */
	public static void writeToFile(String text, String location) throws FileNotFoundException {
		writeToFile(text, new File(location));
	}

	public static void writeToFile(String text, File location) throws FileNotFoundException {
		mkParentDirs(location);
		try (PrintWriter pw = new PrintWriter(location)) {
			pw.print(text);
		}
	}

	public static void writeToFile(String text, String location, Charset charset) throws FileNotFoundException {
		writeToFile(text, new File(location), charset);
	}

	public static void writeToFile(String text, File location, Charset charset) throws FileNotFoundException {
		mkParentDirs(location);
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(location), charset))) {
			pw.print(text);
		}
	}

	public static String grabResource(String url) throws IOException {
		return grabResource(new URL(url));
	}

	public static String grabResource(URL url) throws IOException {
		return JavaTools.readText(url.openStream());
	}

	public static List<String> grabLines(String url) throws IOException {
		return grabLines(new URL(url));
	}

	public static List<String> grabLines(URL url) throws IOException {
		return readLines(url.openStream());
	}

	@SafeVarargs
	public static <I, O> O[] forEach(Function<? super I, ? extends O> processor, I... inputs) {
		@SuppressWarnings("unchecked")
		O[] res = (O[]) new Object[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			res[i] = processor.apply(inputs[i]);
		return res;
	}

	@SafeVarargs
	public static <I> void doForEach(Consumer<? super I> processor, I... inputs) {
		for (I i : inputs)
			processor.accept(i);
	}

	public static String[][] split(String delimRegex, String... strings) {
		return forEach(a -> a.split(delimRegex), strings);
	}

	public static <I, O> List<O> forEach(Iterable<? extends I> inputs, Function<? super I, ? extends O> processor) {
		return forEach(inputs.iterator(), processor);
	}

	public static <I> void doForEach(Iterable<? extends I> inputs, Consumer<? super I> processor) {
		for (I i : inputs)
			processor.accept(i);
	}

	public static List<String[]> split(String delimRegex, Iterable<? extends String> strings) {
		return forEach(strings, a -> a.split(delimRegex));
	}

	public static <I, O> List<O> forEach(Iterator<? extends I> inputs, Function<? super I, ? extends O> processor) {
		List<O> res = new ArrayList<O>();
		for (; inputs.hasNext();)
			res.add(processor.apply(inputs.next()));
		return res;
	}

	public static <I> void doForEach(Iterator<? extends I> inputs, Consumer<? super I> processor) {
		while (inputs.hasNext()) {
			processor.accept((I) inputs.next());
		}
	}

	public static List<String[]> split(String delimRegex, Iterator<? extends String> strings) {
		return forEach(strings, a -> a.split(delimRegex));
	}

	/**
	 * <p>
	 * Runs the provided {@link Function} on each input provided that is within the
	 * specified range from <code>from</code> (inclusive) to, but not including,
	 * <code>to</code>. The result is collected into an array and returned.
	 * </p>
	 * <p>
	 * If <code>from</code> is negative, it is treated as <code>0</code>.
	 * </p>
	 * <p>
	 * If <code>to</code> is negative, it is treated as the size of the array.
	 * </p>
	 * 
	 * @param <I>       The type of input element.
	 * @param <O>       The type of output element.
	 * @param processor The processor to handle each element that needs to be
	 *                  processed with.
	 * @param from      The beginning position of the array.
	 * @param to        The ending position of the array.
	 * @param inputs    The input array.
	 * @return An array of size <code>to - from</code> (where both <code>to</code>
	 *         and <code>from</code> are corrected, if negative, as described
	 *         above), containing the processed elements.
	 */
	@SafeVarargs
	public static <I, O> O[] forEach(int from, int to, Function<? super I, ? extends O> processor, I... inputs) {
		if (from < 0)
			from = 0;
		if (to < 0)
			to = inputs.length;
		@SuppressWarnings("unchecked")
		O[] res = (O[]) new Object[to - from];
		for (int i = from; i < to; i++)
			res[i] = processor.apply(inputs[i]);
		return res;
	}

	@SafeVarargs
	public static <I> void doForEach(int from, int to, Consumer<? super I> processor, I... inputs) {
		if (from < 0)
			from = 0;
		if (to < 0)
			to = inputs.length;
		for (int i = from; i < to; i++)
			processor.accept(inputs[i]);
	}

	public static <I, O> List<O> forEach(int from, int to, Function<? super I, ? extends O> processor,
			Iterable<? extends I> inputs) {
		return forEach(from, to, inputs.iterator(), processor);
	}

	public static <I> void doForEach(int from, int to, Iterable<? extends I> inputs, Consumer<? super I> processor) {
		doForEach(from, to, inputs.iterator(), processor);
	}

	public static <I, O> List<O> forEach(int from, int to, Iterator<? extends I> inputs,
			Function<? super I, ? extends O> processor) {
		List<O> res = new ArrayList<>();
		doForEach(from, to, inputs, a -> res.add(processor.apply(a)));
		return res;
	}

	public static <I> void doForEach(int from, int to, Iterator<I> itr, Consumer<? super I> elementHandler) {
		// Handle from lower bound.
		if (from < 0)
			from = 0;
		else
			// If starting point (from) is not <0, skip over elements until iter is at
			// starting point.
			for (int i = 0; i < from; i++, itr.next())
				;
		// If end point is "end of iter" then keep going until we hit the end of the
		// iter, (inputs.hasNext() is our stopping condition).
		// Otherwise, keep going until from is == to.
		if (to < 0)
			for (; itr.hasNext(); elementHandler.accept(itr.next()))
				;
		else
			for (; from < to; from++, elementHandler.accept(itr.next()))
				;
	}

	@FunctionalInterface
	public interface Convolver<I, O> extends Function<I[], O> {
		@Override
		O apply(@SuppressWarnings("unchecked") I... input);
	}

	@SuppressWarnings("unchecked")
	public static <I, O> O[] convolve(int kernelSize, Function<? super I[], ? extends O> handler, I... input) {
		if (input.length < kernelSize)
			return (O[]) new Object[0];

		I[] kern = (I[]) Array.newInstance(input.getClass().getComponentType(), kernelSize);
		O[] res = (O[]) new Object[input.length - kernelSize + 1];
		for (int i = 0; i < input.length - kernelSize + 1; i++) {
			System.arraycopy(input, i, kern, 0, kernelSize);
			res[i] = handler.apply(kern);
		}

		return res;
	}

	public static <I, O> List<O> convolve(int kernelSize, List<? extends I> input,
			Function<? super List<? extends I>, ? extends O> handler) {
		if (input.size() < kernelSize)
			return new ArrayList<>(0);

		List<O> res = new ArrayList<>(input.size() - kernelSize + 1);
		for (int i = 0; i < input.size() - kernelSize + 1; i++)
			res.add(handler.apply(input.subList(i, i + kernelSize)));
		return res;
	}

	public static <E> int collectAtMostNElementsFromIterator(int count, Iterator<? extends E> elements,
			Consumer<? super E> handler) {
		int i = 0;
		for (; i < count && elements.hasNext(); i++)
			handler.accept(elements.next());
		return i;
	}

	public static <I, O> List<O> convolve(int kernelSize, Iterable<? extends I> input,
			Function<? super List<I>, ? extends O> handler) {
		return convolve(kernelSize, input.iterator(), handler);
	}

	public static <I, O> List<O> convolve(int kernelSize, Iterator<? extends I> itr,
			Function<? super List<I>, ? extends O> handler) {
		LinkedList<I> items = new LinkedList<>();
		if (collectAtMostNElementsFromIterator(kernelSize, itr, items::add) < kernelSize)
			return new ArrayList<>(0);

		List<O> res = new ArrayList<O>();
		res.add(handler.apply(items));
		while (itr.hasNext()) {
			items.removeFirst();
			items.add(itr.next());
			res.add(handler.apply(items));
		}
		return res;
	}

	public static <K, V> void updateMap(Map<? super K, V> map, K key, V value,
			BiFunction<? super V, ? super V, ? extends V> updater) {
		map.put(key, map.containsKey(key) ? updater.apply(value, map.get(key)) : value);
	}

	@SafeVarargs
	public static <K, V, M extends Map<? super K, V>> M combineMaps(M dest,
			BiFunction<? super V, ? super V, ? extends V> entryCombiner, Map<? extends K, ? extends V>... maps) {
		for (Map<? extends K, ? extends V> m : maps)
			for (Entry<? extends K, ? extends V> e : m.entrySet())
				updateMap(dest, e.getKey(), e.getValue(), entryCombiner);
		return dest;
	}

	public static <K, V> Map<K, V> combine(Map<? extends K, ? extends V> first, Map<? extends K, ? extends V> second,
			BiFunction<? super V, ? super V, ? extends V> entryCombiner) {
		return combineMaps(new HashMap<>(first), entryCombiner, second);
	}

	/**
	 * Puts the provided value into the provided {@link Map} at the provided key if
	 * there is not already an entry for that key. If there already exists an entry
	 * at the specified key in the {@link Map}, this method simply returns that
	 * value. Otherwise, the method returns the value it inserts into the
	 * {@link Map} (the one provided).
	 * 
	 * @param <K>   The type of the key.
	 * @param <V>   The type of the value.
	 * @param map   The {@link Map}.
	 * @param key   The key.
	 * @param value The value.
	 * @return The value located at the provided key in the map once the method
	 *         completes. This is either the provided value or the value already in
	 *         the {@link Map}, if one was already there.
	 */
	public static <K, V> V putIfAbsent(Map<? super K, V> map, K key, V value) {
		if (map.containsKey(key))
			return map.get(key);
		map.put(key, value);
		return value;
	}

	public static BigDecimal average(Iterable<? extends BigDecimal> values, int scale) {
		BigDecimal avg = BigDecimal.ZERO;
		int count = 0;
		for (BigDecimal b : values) {
			count++;
			avg = avg.add(b);
		}
		return avg.divide(BigDecimal.valueOf(count), scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal variance(Iterable<? extends BigDecimal> values, int scale) {
		return variance(values, scale, average(values, scale));
	}

	/**
	 * Calculates the variance of the given population.
	 * 
	 * @param values The values in the population.
	 * @param scale  The scale of the result.
	 * @param mean   The population mean.
	 * @return The variance of the population.
	 */
	public static BigDecimal variance(Iterable<? extends BigDecimal> values, int scale, BigDecimal mean) {
		return average(mask(values, a -> a.multiply(a)), scale).subtract(mean.pow(2)).setScale(scale,
				RoundingMode.HALF_UP);
	}

	public static BigDecimal sampleVariance(Iterable<? extends BigDecimal> values, int scale) {
		return sampleVariance(values, scale, average(values, scale));
	}

	/**
	 * Attempts to estimate the variance of a population through a sample from it.
	 * 
	 * @param values The values in the sample.
	 * @param scale  The scale of the result.
	 * @param mean   The sample mean.
	 * @return The estimated population variance.
	 */
	public static BigDecimal sampleVariance(Iterable<? extends BigDecimal> values, int scale, BigDecimal mean) {
		BigDecimal v = BigDecimal.ZERO;
		int count = 0;
		for (BigDecimal b : values) {
			count++;
			v = v.add(mean.subtract(b).pow(2));
		}
		return v.divide(BigDecimal.valueOf(count).subtract(BigDecimal.ONE), scale, RoundingMode.HALF_UP)
				.subtract(mean.pow(2)).setScale(scale, RoundingMode.HALF_UP);
	}

	public static final BigDecimal BIG_DECIMAL_TWO = BigDecimal.valueOf(2);

	public static BigDecimal sqrt(BigDecimal input, int scale) {
		double f = Math.sqrt(input.doubleValue());
		BigDecimal s = BigDecimal.ZERO.setScale(scale),
				g = Double.isFinite(f) ? BigDecimal.valueOf(f).setScale(scale, RoundingMode.HALF_UP) : input;
		while (s.compareTo(g) != 0)
			g = input.divide(s = g, scale, RoundingMode.HALF_UP).add(s).divide(BIG_DECIMAL_TWO, scale,
					RoundingMode.HALF_UP);
		return g;
	}

	public static BigDecimal stddev(Iterable<? extends BigDecimal> values, int scale) {
		return sqrt(variance(values, scale), scale);
	}

	public static BigDecimal stddev(Iterable<? extends BigDecimal> values, int scale, BigDecimal mean) {
		return sqrt(variance(values, scale, mean), scale);
	}

	public static BigDecimal sampleStddev(Iterable<? extends BigDecimal> values, int scale) {
		return sqrt(sampleVariance(values, scale), scale);
	}

	public static BigDecimal sampleStddev(Iterable<? extends BigDecimal> values, int scale, BigDecimal mean) {
		return sqrt(sampleVariance(values, scale, mean), scale);
	}

	public static BigDecimal[] calculateMeanAndVariance(Iterable<? extends BigDecimal> values, int scale) {
		BigDecimal a = average(values, scale);
		return new BigDecimal[] { a, variance(values, scale, a) };
	}

	public static BigDecimal[] calculateMeanAndSampleVariance(Iterable<? extends BigDecimal> values, int scale) {
		BigDecimal a = average(values, scale);
		return new BigDecimal[] { a, sampleVariance(values, scale, a) };
	}

	/**
	 * Creates and returns a new {@link Iterable} whose {@link Iterator}s consist of
	 * the items whose indices are <code>offset</code> plus non-negative integer
	 * multiples of <code>n</code>. The {@link Iterator}s start at
	 * <code>offset</code> in the {@link List}, (if the {@link List} is big enough),
	 * and return that starting element, then the element <code>n</code> farther
	 * into the {@link List}. Subsequent calls return the elements spaced
	 * <code>n</code> after the last.
	 * 
	 * @param <T>    The type of item in the {@link List}.
	 * @param offset The offset of the first element.
	 * @param n      The jump width/spacing.
	 * @param items  The {@link List} of items.
	 * @return The new {@link Iterable}.
	 */
	public static <T> Iterable<T> nth(int offset, int n, List<? extends T> items) {
		return () -> new Iterator<T>() {

			int p = offset;

			@Override
			public boolean hasNext() {
				return p < items.size();
			}

			@Override
			public T next() {
				T item = items.get(p);
				p += n;
				return item;
			}
		};
	}

	public static <T> List<T> collectNth(int offset, int n, List<? extends T> items) {
		return collect(nth(offset, n, items));
	}

	public static BigDecimal linearInterp(BigDecimal from, BigDecimal to, BigDecimal frac) {
		return frac.multiply(from).add(BigDecimal.ONE.subtract(frac).multiply(to));
	}

	public static String repeat(String input, int times) {
		StringBuilder sb = new StringBuilder(input.length() * times);
		for (; times > 0; times--)
			sb.append(input);
		return sb.toString();
	}

	public static String repeat(char input, int times) {
		char[] arr = new char[times];
		Arrays.fill(arr, input);
		return new String(arr);
	}

	/**
	 * Returns a {@link String} containing the provided <code>text</code>, padded by
	 * the provided <code>pc</code> or truncated, as appropriate, so that the
	 * {@link String} has the size specified by <code>space</code>. Padding is
	 * applied to the front (filling the front of the string with <code>pc</code>
	 * padding chars) if <code>padfront</code> is <code>true</code>, otherwise it is
	 * applied to the back (end).
	 * 
	 * @param text     The text to pad or truncated.
	 * @param space    The amount of space (characters) that the returned text
	 *                 should take up.
	 * @param padfront Whether to pad the front or back of the {@link String}.
	 * @param pc       The character to use for padding.
	 * @return The padded or truncated {@link String}.
	 */
	public static String padOrShrink(String text, int space, boolean padfront, char pc) {
		return text.length() < space ? pad(text, padfront, pc, space - text.length()) : text.substring(0, space);
	}

	public static String padOrShrink(String text, int space, boolean padfront) {
		return padOrShrink(text, space, padfront, ' ');
	}

	public static String padOrShrink(String text, int space) {
		return padOrShrink(text, space, false);
	}

	public static String pad(String text, boolean padfront, char pc, int charsToPad) {
		if (padfront)
			return repeat(pc, charsToPad) + text;
		else {
			StringBuilder sb = new StringBuilder(text);
			for (int i = charsToPad; i > 0; i--)
				sb.append(pc);
			return sb.toString();
		}
	}

	public static String pad(String text, boolean padfront, int charsToPad) {
		return pad(text, padfront, ' ', charsToPad);
	}

	public static String pad(String text, int charsToPad) {
		return pad(text, false, charsToPad);
	}

	public static String padUpTo(String text, int space, boolean padfront, char pc) {
		return text.length() < space ? pad(text, padfront, pc, space - text.length()) : text;
	}

	public static String padUpTo(String text, int space, boolean padfront) {
		return padUpTo(text, space, padfront, ' ');
	}

	public static String padUpTo(String text, int space) {
		return padUpTo(text, space, false);
	}

	/**
	 * Replaces all the items in the provided array and then returns the provided
	 * array. No new array is created directly by this method.
	 * 
	 * @param <T>       The type of item in the array.
	 * @param converter The {@link Function} applied to each individual array
	 *                  element.
	 * @param items     The array of items to convert each element of.
	 * @return The provided array (after all contained elements have been
	 *         converted).
	 */
	@SafeVarargs
	public static <T> T[] replaceAll(Function<? super T, ? extends T> converter, T... items) {
		for (int i = 0; i < items.length; i++)
			items[i] = converter.apply(items[i]);
		return items;
	}

	/**
	 * Returns <code>true</code> if the provided <code>item</code> is between the
	 * provided <code>lower</code> and <code>upper</code> bounds.
	 * 
	 * @param <I>   The type of the item.
	 * @param <TL>  The type of the lower bound.
	 * @param <TU>  The type of the upper bound.
	 * @param item  The item.
	 * @param lower The lower bound.
	 * @param upper The upper bound.
	 * @return <code>lower.compareTo(item) > 0 && upper.compareTo(item) < 0</code>
	 */
	public static <I, TL extends Comparable<? super I>, TU extends Comparable<? super I>> boolean isBetween(I item,
			TL lower, TU upper) {
		return lower.compareTo(item) > 0 && upper.compareTo(item) < 0;
	}

	/**
	 * Returns <code>true</code> if the provided <code>item</code> is between the
	 * provided <code>lower</code> and <code>upper</code> bounds or if it is equal
	 * to the bounds.
	 * 
	 * @param <I>   The type of the item.
	 * @param <TL>  The type of the lower bound.
	 * @param <TU>  The type of the upper bound.
	 * @param item  The item.
	 * @param lower The lower bound.
	 * @param upper The upper bound.
	 * @return <code>lower.compareTo(item) >= 0 && upper.compareTo(item) <= 0</code>
	 */
	public static <I, TL extends Comparable<? super I>, TU extends Comparable<? super I>> boolean isOnRange(I item,
			TL lower, TU upper) {
		return lower.compareTo(item) >= 0 && upper.compareTo(item) <= 0;
	}

	public static <I, C extends Comparable<? super I>> boolean lessThan(I item, C bound) {
		return bound.compareTo(item) > 0;
	}

	public static <I, C extends Comparable<? super I>> boolean lessThanOrEqualTo(I item, C bound) {
		return bound.compareTo(item) >= 0;
	}

	public static <I, C extends Comparable<? super I>> boolean greaterThan(I item, C bound) {
		return bound.compareTo(item) < 0;
	}

	public static <I, C extends Comparable<? super I>> boolean greaterThanOrEqualTo(I item, C bound) {
		return bound.compareTo(item) <= 0;
	}

	/**
	 * Returns <code>true</code> if each element in the provided array is less than
	 * the subsequent element.
	 * 
	 * @param <C>   The type of the item in the array.
	 * @param items The array of items.
	 * @return <code>true</code> if the array is sorted in ascending order.
	 */
	@SafeVarargs
	public static <C extends Comparable<? super C>> boolean lessThan(C... items) {
		for (int i = 1; i < items.length; i++)
			if (items[i - 1].compareTo(items[i]) >= 0)
				return false;
		return true;
	}

	@SafeVarargs
	public static <C extends Comparable<? super C>> boolean isSortedAscending(C... items) {
		for (int i = 1; i < items.length; i++)
			if (items[i - 1].compareTo(items[i]) > 0)
				return false;
		return true;
	}

	@SafeVarargs
	public static <C extends Comparable<? super C>> boolean greaterThan(C... items) {
		for (int i = 1; i < items.length; i++)
			if (items[i - 1].compareTo(items[i]) <= 0)
				return false;
		return true;
	}

	@SafeVarargs
	public static <C extends Comparable<? super C>> boolean isSortedDescending(C... items) {
		for (int i = 1; i < items.length; i++)
			if (items[i - 1].compareTo(items[i]) < 0)
				return false;
		return true;
	}

	public static double[] addVectors(double[] first, double... second) {
		assert first.length == second.length : "Cannot add vectors of different lengths";
		return addVectorInto(first.clone(), second);
	}

	public static double[] addVectorInto(double[] result, double... addend) {
		for (int i = 0; i < result.length; i++)
			result[i] = result[i] + addend[i];
		return result;
	}

}
