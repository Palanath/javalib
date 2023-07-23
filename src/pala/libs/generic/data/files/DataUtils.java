package pala.libs.generic.data.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataUtils {
	public static <E> List<E> readAll(String extension, Function<? super InputStream, ? extends E> reader,
			boolean recurse, File... paths) {
		return readAll(reader, a -> a.getName().endsWith('.' + extension), recurse, paths);
	}

	public enum TraversalMode {
		/**
		 * Represents no recursion through a file tree. If a {@link File} object points
		 * to a directory, it is completely ignored.
		 */
		NONE,
		/**
		 * <p>
		 * Represents shallow (one-layer) recursion. If a {@link File} object points to
		 * a directory, its immediate child files are processed, but its immediate child
		 * directories are ignored.
		 * </p>
		 * <p>
		 * In other words, if a {@link File} object points to a directory, that
		 * directory is processed {@link #NONE without recursion}.
		 * </p>
		 */
		SHALLOW,
		/**
		 * Represents indefinite recursion. If a {@link File} object points to a
		 * directory, that directory's children are processed recursively, with this
		 * same {@link #DEEP} recursion mode. This causes traversal to traverse over an
		 * entire {@link File} tree.
		 */
		DEEP
	}

	/**
	 * <p>
	 * Traverses the provided {@link File}s and loads objects from them.
	 * </p>
	 * <p>
	 * This method traverses each {@link File} object provided. If
	 * <code>recurse</code> is <code>true</code> then the contents of any
	 * directories encountered are traversed as well, recursively, for more files
	 * and sub-directories, otherwise, if <code>recurse</code> is
	 * <code>false</code>, directories provided are ignored.
	 * </p>
	 * <p>
	 * Each {@link File} object whose {@link File#isFile()} method returns
	 * <code>true</code> is provided to the specified {@link Predicate}. Any
	 * {@link File}s for which the {@link Predicate} then returns <code>true</code>
	 * are opened for reading through a {@link FileInputStream}, and that
	 * {@link FileInputStream} is provided to the specified {@link Function} to read
	 * the contents of the {@link File} into an object. After the {@link Function}
	 * returns, either normally or exceptionally, the {@link InputStream} is closed
	 * and cleaned up appropriately.
	 * </p>
	 * <p>
	 * The objects read are collected into a {@link List} and returned.
	 * </p>
	 * 
	 * @param <E>     The type of object being read from the files.
	 * @param reader  A {@link Function} that reads the file's content and builds an
	 *                object off of it.
	 * @param filter  A filter which returns <code>false</code> for any {@link File}
	 *                objects which should be ignored. This filter is only provided
	 *                {@link File} objects whose {@link File#isFile()} method
	 *                returns true; it is not provided directories.
	 * @param recurse Whether to look inside directories recursively.
	 * @param paths   The {@link File} paths to traverse through.
	 * @return A {@link List} of read objects.
	 */
	public static <E> List<E> readAll(Function<? super FileInputStream, ? extends E> reader,
			Predicate<? super File> filter, boolean recurse, File... paths) {
		return readAll(reader, filter, recurse ? TraversalMode.DEEP : TraversalMode.NONE, paths);
	}

	/**
	 * <p>
	 * Traverses the provided {@link File}s and loads objects from them.
	 * </p>
	 * <p>
	 * This method traverses each {@link File} object provided. If any {@link File}
	 * object points to a directory, it is handled in accordance with the
	 * {@link TraversalMode} policy provided.
	 * </p>
	 * <p>
	 * Then, each {@link File} object whose {@link File#isFile()} method returns
	 * <code>true</code> is provided to the specified {@link Predicate}. Any
	 * {@link File}s for which the {@link Predicate} then returns <code>true</code>
	 * are opened for reading through a {@link FileInputStream}, and that
	 * {@link FileInputStream} is provided to the specified {@link Function} to read
	 * the contents of the {@link File} into an object. After the {@link Function}
	 * returns, either normally or exceptionally, the {@link InputStream} is closed
	 * and cleaned up appropriately.
	 * </p>
	 * <p>
	 * The objects read are collected into a {@link List} and returned.
	 * </p>
	 * 
	 * @param <E>           The type of object being read from the files.
	 * @param reader        A {@link Function} that reads the file's content and
	 *                      builds an object off of it.
	 * @param filter        A filter which returns <code>false</code> for any
	 *                      {@link File} which should be ignored. This filter is
	 *                      only provided {@link File} objects whose
	 *                      {@link File#isFile()} method returns true; it is not
	 *                      provided directories.
	 * @param traversalMode The {@link TraversalMode} which defines how to treat
	 *                      directories encountered.
	 * @param paths         The {@link File} paths to traverse through. This may
	 *                      always contain directories, though whether (and how)
	 *                      those directories are processed depends on the
	 *                      {@link TraversalMode}.
	 * @return The {@link List} of read objects.
	 */
	public static <E> List<E> readAll(Function<? super FileInputStream, ? extends E> reader,
			Predicate<? super File> filter, TraversalMode traversalMode, File... paths) {
		return readAll(new ArrayList<>(), reader, filter, traversalMode, paths);
	}

	/**
	 * <p>
	 * Traverses the provided {@link File}s and loads objects from them.
	 * </p>
	 * <p>
	 * This method traverses each {@link File} object provided. If any {@link File}
	 * object points to a directory, it is handled in accordance with the
	 * {@link TraversalMode} policy provided.
	 * </p>
	 * <p>
	 * Then, each {@link File} object whose {@link File#isFile()} method returns
	 * <code>true</code> is provided to the specified {@link Predicate}. Any
	 * {@link File}s for which the {@link Predicate} then returns <code>true</code>
	 * are opened for reading through a {@link FileInputStream}, and that
	 * {@link FileInputStream} is provided to the specified {@link Function} to read
	 * the contents of the {@link File} into an object. After the {@link Function}
	 * returns, either normally or exceptionally, the {@link InputStream} is closed
	 * and cleaned up appropriately.
	 * </p>
	 * <p>
	 * The objects read are collected into the provided {@link List} and that
	 * {@link List} is returned.
	 * </p>
	 * 
	 * @param <E>           The type of object being read from the files.
	 * @param <L>           The type of the {@link List} provided which the results
	 *                      will be added to.
	 * @param result        The {@link List} to add the loaded objects to.
	 * @param reader        A {@link Function} that reads the file's content and
	 *                      builds an object off of it.
	 * @param filter        A filter which returns <code>false</code> for any
	 *                      {@link File} which should be ignored. This filter is
	 *                      only provided {@link File} objects whose
	 *                      {@link File#isFile()} method returns true; it is not
	 *                      provided directories.
	 * @param traversalMode The {@link TraversalMode} which defines how to treat
	 *                      directories encountered.
	 * @param paths         The {@link File} paths to traverse through. This may
	 *                      always contain directories, though whether (and how)
	 *                      those directories are processed depends on the
	 *                      {@link TraversalMode}.
	 * @return The {@link List} of read objects. This is the same {@link List} as
	 *         what is provided by the caller.
	 */
	public static <E, L extends List<? super E>> L readAll(L result,
			Function<? super FileInputStream, ? extends E> reader, Predicate<? super File> filter,
			TraversalMode traversalMode, File... paths) {
		return readAll(result, reader, filter, traversalMode, null, paths);
	}

	public static class ObjectLoadingException extends RuntimeException {
		/**
		 * Serial UID
		 */
		private static final long serialVersionUID = 1L;
		private final File file;

		public File getFile() {
			return file;
		}

		public ObjectLoadingException(Throwable cause, File file) {
			super(cause);
			this.file = file;
		}

		public ObjectLoadingException(String message, Throwable cause, File file) {
			super(message, cause);
			this.file = file;
		}
	}

	public static <E, L extends List<? super E>> L readAll(L result,
			Function<? super FileInputStream, ? extends E> reader, Predicate<? super File> filter,
			TraversalMode traversalMode, Consumer<? super ObjectLoadingException> exceptionHandler, File... paths) {
		for (File f : paths)
			if (f.isDirectory()) {
				if (traversalMode == TraversalMode.SHALLOW)
					readAll(result, reader, filter, TraversalMode.NONE, f.listFiles());
				else if (traversalMode == TraversalMode.DEEP)
					readAll(result, reader, filter, TraversalMode.DEEP, paths);
			} else if (f.isFile() && filter.test(f))
				try (FileInputStream fis = new FileInputStream(f)) {
					result.add(reader.apply(fis));
				} catch (Exception e) {
					ObjectLoadingException ole = new ObjectLoadingException(e, f);
					if (exceptionHandler != null)
						exceptionHandler.accept(ole);
					else
						throw ole;
				}
		return result;
	}

}
