package pala.libs.generic.networking.sockets;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import pala.libs.generic.util.Box;

/**
 * <p>
 * The {@link Client} class provides reading and writing {@link Serializable}
 * objects over a {@link Socket}. It can be paired with a {@link Server} to make
 * the development of connections that communicate with {@link Serializable}
 * objects easy.
 * </p>
 * <p>
 * If attempting unsupported/lower-level communication with this class, please
 * note that this class creates a new {@link ObjectOutputStream} over its given
 * {@link Socket}'s {@link Socket#getOutputStream() output stream}, and then
 * creates a new {@link ObjectInputStream} over the {@link Socket}'s
 * {@link Socket#getInputStream() input stream}. These two operations
 * <q>[write] the serialization stream header to the underlying stream</q>, and
 * expect the same stream header from the other end of the connection,
 * respectively.
 * </p>
 *
 * @author Palanath
 *
 */
public class Client implements Closeable {
	private enum CommunicationCommands {
		CONNECTION_CHECK, COMMUNICATION_RESPONSE;
	}

	private Socket socket;
	private final ObjectInputStream in;

	private final ObjectOutputStream out;

	/**
	 * Creates a {@link Client} object out of the connected socket.
	 *
	 * @param socket The socket to use for the client.
	 * @throws IOException In case {@link Socket#getOutputStream()} or
	 *                     {@link Socket#getInputStream()} throws an
	 *                     {@link IOException}, or
	 */
	public Client(final Socket socket) throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream((this.socket = socket).getInputStream());
	}

	/**
	 * Closes this {@link Client}'s underlying {@link Socket}.
	 *
	 * @throws IOException As specified by {@link Socket#close()}.
	 */
	@Override
	public void close() throws IOException {
		if (socket == null)
			return;
		socket.close();
		socket = null;
	}

	/**
	 * Returns the underlying {@link Socket} that's backing this {@link Client}.
	 * Although operations that are performed on the {@link Socket} directly are
	 * unsupported by this class, the {@link Socket} is exposed so that information
	 * can be retrieved from it, (e.g. the {@link InetAddress} that its connected
	 * to).
	 *
	 * @return The {@link Socket} used by this {@link Client} object.
	 */
	public Socket getSocket() {
		return socket;
	}

	public boolean isConnected() {
		return socket != null;
	}

	/**
	 * <p>
	 * Blocks until a {@link Serializable} piece of data is received from the
	 * {@link #out internal ObjectInputStream hooked up to the Socket}.
	 * </p>
	 * <p>
	 * Please note that: <b>All {@link Exception}s are fatal to this {@link Client}
	 * object</b> as they may leave the {@link Client}'s underlying {@link #in input
	 * stream} in an indeterminate state.
	 * </p>
	 *
	 * @return The next {@link Serializable} received.
	 * @throws ClassNotFoundException As specified by
	 *                                {@link ObjectInputStream#readObject()}.
	 * @throws IOException            If an {@link IOException} occurs while reading
	 *                                the object.
	 */
	public synchronized Serializable read() throws ClassNotFoundException, IOException {
		Object readObject = in.readObject();
		while (readObject instanceof CommunicationCommands)
			// switch ((CommunicationCommands) readObject) {
//				case CONNECTION_CHECK:
//					send(CommunicationCommands.COMMUNICATION_RESPONSE);
			// This will be dealt with later.
//			}
			readObject = in.readObject();
		return (Serializable) readObject;
	}

	/**
	 * <p>
	 * Blocks until a {@link Serializable} piece of information written by the
	 * {@link Client} at the other endpoint is received, or until the given timeout.
	 * </p>
	 * <p>
	 * As with {@link #read()}, all exceptions that this method may arouse are fatal
	 * to the underlying socket, and, thus, to this {@link Client} object. After an
	 * exception, the {@link Client} object should be discarded.
	 * </p>
	 * <p>
	 * This method expects a returnable {@link Serializable} object to be received
	 * from the other endpoint of this connection within the specified amount of
	 * time. Because of this, it sets an {@link Socket#setSoTimeout(int) SO Timeout}
	 * on the underlying socket right before calling the read method, and then waits
	 * for the object. If a returnable, {@link Serializable} object is received
	 * within the time, the method returns the object. If a
	 * {@link CommunicationCommands Communication Command} is received instead, it
	 * is handled and then the reading process is repeated with the SO Timeout set
	 * to the delay time specified as an argument to this method minus the current
	 * time plus the time recorded at this method's calling, so long as the new
	 * timeout is valid. If not valid, the method simply returns null.
	 * </p>
	 *
	 * @param millisTimeout The timeout in milliseconds.
	 * @return A {@link Box} containing the read value, or <code>null</code> if
	 *         there was a timeout.
	 * @throws ClassNotFoundException If a {@link ClassNotFoundException} occurs
	 *                                while reading an object from the underlying
	 *                                {@link ObjectInputStream#readObject()}.
	 * @throws IOException            If an {@link IOException} occurs while reading
	 *                                an object from the underlying
	 *                                {@link ObjectInputStream#readObject()}.
	 */
	public synchronized Box<Serializable> read(final int millisTimeout) throws ClassNotFoundException, IOException {
		socket.setSoTimeout(millisTimeout);
		final long startTime = System.currentTimeMillis();
		try {
			Object readObject = in.readObject();
			while (readObject instanceof CommunicationCommands) {
				// Handle CommunicationCommands here.

				final long remainingTime = millisTimeout - System.currentTimeMillis() + startTime;
				if (remainingTime < 0)
					return null;
				socket.setSoTimeout((int) remainingTime);
				readObject = in.readObject();
			}
			return new Box<>((Serializable) readObject);
		} catch (final InterruptedIOException e) {
			return null;
		} finally {
			socket.setSoTimeout(0);
		}
	}

	/**
	 * Sends the specified {@link Serializable} over the connection.
	 *
	 * @param item The {@link Serializable} to send.
	 */
	public void send(final Serializable item) throws IOException {
		out.writeObject(item);
		out.flush();
	}

	/**
	 * Sends a "test" object. If a {@link Client}, like this, is on the other end,
	 * it should ignore the object. This allows the user to check if a connection is
	 * ok, since an exception will be thrown if it isn't.
	 */
	public IOException testConnection() {
		return trySend(CommunicationCommands.CONNECTION_CHECK);
	}

	/**
	 * Attempts to close this {@link Client} by calling {@link #close()}. If an
	 * {@link IOException} is produced by the call, it is caught and returned.
	 * Otherwise, the method returns <code>null</code>.
	 *
	 * @return <code>null</code> on a successful close but an {@link IOException}
	 *         otherwise.
	 */
	public IOException tryClose() {
		try {
			close();
		} catch (final IOException e) {
			return e;
		}
		return null;
	}

	public IOException trySend(final Serializable item) {
		try {
			send(item);
			return null;
		} catch (final IOException e) {
			return e;
		}
	}

}
