package pala.libs.generic.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public interface JSONSavable {
	JSONValue toJSON();

	/**
	 * Writes this {@link JSONSavable} to the provided {@link OutputStream} by
	 * creating an {@link OutputStreamWriter} and calling {@link #save(Writer)} with
	 * it. This method does not close the provided {@link OutputStream} when it is
	 * complete.
	 * 
	 * @param out The {@link OutputStream} to write this object to.
	 * @throws IOException If an {@link IOException} occurs while writing.
	 */
	default void save(OutputStream out) throws IOException {
		save(new OutputStreamWriter(out));
	}

	/**
	 * Writes this {@link JSONSavable} to the provided {@link Writer} then calls the
	 * {@link Writer}'s {@link Writer#flush()} method. This method does not close
	 * the {@link Writer} after it completes.
	 * 
	 * @param out The {@link Writer} to write to.
	 * @throws IOException If an {@link IOException} occurs while writing.
	 */
	default void save(Writer out) throws IOException {
		out.write(toJSON().toString());
		out.flush();
	}

	/**
	 * <p>
	 * Saves this {@link JSONSavable} to the provided {@link File} attempting to
	 * create or overwrite the {@link File}, depending on whether or not it exists.
	 * This method opens a new {@link FileWriter} for writing and calls
	 * {@link #save(Writer)} with it, then safely closes the writer.
	 * </p>
	 * <p>
	 * {@link IOException}s are propagated to the caller.
	 * </p>
	 * 
	 * @param file The {@link File} to save to.
	 * @throws IOException If an {@link IOException} occurs during the saving
	 *                     process. This can happen, for example, if the
	 *                     {@link File} cannot be opened for writing (e.g. no
	 *                     permissions or a filesystem object such as a directory
	 *                     already exists at the specified location).
	 */
	default void save(File file) throws IOException {
		try (FileWriter fw = new FileWriter(file)) {
			save(fw);
		}
	}
}
