package pala.libs.generic.streams.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import pala.libs.generic.generators.Generator;
import pala.libs.generic.util.UncheckedExceptionWrapper;

public class BackwardLineInputStream implements AutoCloseable, Generator<String> {

	private static final int BUFFER_SIZE = 8192;

	private final RandomAccessFile raf;
	private long pos;
	private final byte[] b = new byte[BUFFER_SIZE];
	private int buffrem;
	private Charset charset;
	private byte[] newLine;

	public BackwardLineInputStream(final File file) throws IOException {
		this(file, StandardCharsets.UTF_8);
	}

	public BackwardLineInputStream(final File file, final Charset charset) throws IOException {
		raf = new RandomAccessFile(file, "r");
		raf.seek(pos = raf.length());
		setCharset(charset);
	}

	public BackwardLineInputStream(final RandomAccessFile raf) throws IOException {
		this(raf, StandardCharsets.UTF_8);
	}

	public BackwardLineInputStream(final RandomAccessFile raf, final Charset charset) throws IOException {
		pos = (this.raf = raf).length();
		setCharset(charset);
	}

	public BackwardLineInputStream(final String file) throws IOException {
		this(file, StandardCharsets.UTF_8);
	}

	public BackwardLineInputStream(final String file, final Charset charset) throws IOException {
		this(new File(file), charset);
	}

	@Override
	public void close() throws IOException {
		raf.close();
	}

	public Charset getCharset() {
		return charset;
	}

	private boolean hasNext() {
		return buffrem > 0 || pos > 0;
	}

	public boolean hasNextLine() {
		return hasNext();
	}

	@Override
	public String next() throws UncheckedExceptionWrapper {
		try {
			return readLine();
		} catch (final IOException e) {
			throw new UncheckedExceptionWrapper(e);
		}
	}

	private byte nextByte() throws IOException {
		if (buffrem == 0) {
			if (pos == 0)
				throw new EOFException("There is no next byte.");
			while (readBlock() == 0)
				;
		}
		return b[--buffrem];
	}

	/**
	 * Reads a block into {@link #b} and returns the number of bytes read.
	 *
	 * @return the number of bytes read into {@link #b}.
	 * @throws IOException If an {@link IOException} occurs.
	 */
	private int readBlock() throws IOException {
		if (pos == 0)
			return -1;

		raf.seek(Math.max(0, pos - BUFFER_SIZE));
		pos -= buffrem = raf.read(b, 0, pos < BUFFER_SIZE ? (int) pos : BUFFER_SIZE);
		return buffrem;
	}

	/**
	 * Reads data from the file, one line at a time, starting from the last line in
	 * the file. This method will only return an empty string once it has reached
	 * the end of the file. This method (and class) is only designed to read files
	 * encoded in UTF-8 or a subset thereof.
	 *
	 * @return The next line.
	 * @throws IOException If an {@link IOException} occurs.
	 */
	public String readLine() throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int c = 0;

		while (hasNext()) {
			final byte n = nextByte();
			if (n == newLine[newLine.length - c - 1]) {
				if (++c == newLine.length) {// We successfully parsed a whole new line char.
					// Reset newline parsing:
					c = 0;
					break;
				}
				continue;
			}
			if (c != 0)
				for (; c > 0; c--)// Reduce c to 0 and append parsed partial new-line char.
					baos.write(newLine[c]);
			baos.write(n);
		}

		final byte[] barr = baos.toByteArray();// For storing data.
		for (int i = 0; i < barr.length / 2; i++) {
			final byte t = barr[i];
			barr[i] = barr[barr.length - 1 - i];
			barr[barr.length - 1 - i] = t;
		}
		return new String(barr, charset);
	}

	public void setCharset(final Charset charset) {
		final ByteBuffer buff = (this.charset = charset).encode(System.lineSeparator());
		newLine = new byte[buff.remaining()];
		buff.get(newLine);
	}

}
