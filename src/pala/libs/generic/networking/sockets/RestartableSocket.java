package pala.libs.generic.networking.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class RestartableSocket implements AutoCloseable {
	private final InetAddress address;
	private final int port;
	private Socket socket;

	public RestartableSocket(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * <p>
	 * Gets the current {@link Socket}, or returns <code>null</code> if there is no
	 * {@link Socket}. The {@link Socket} may be in a valid state (meaning the next
	 * IO operation succeeds), a broken state (meaning a previous IO operation
	 * failed, and so will subsequent operations), or a state where it is about to
	 * break (where the next IO operation may be the first to fail). If the
	 * {@link Socket} is in an unusable state, {@link #restart()} may be called to
	 * reopen it.
	 * </p>
	 * 
	 * @return The current {@link Socket}.
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * <p>
	 * Restarts this {@link Socket}. This method should only be called when it is
	 * expected that reopening the {@link Socket} will succeed before an infinite
	 * number of attempts.
	 * </p>
	 * <p>
	 * This method first attempts to close the {@link Socket} if there is one
	 * currently (regardless of whether it is in a usable or unusable state), then
	 * attempts to reopen it. If reopening the {@link Socket} results in an
	 * {@link IOException}, the exception is caught and suppressed, and opening is
	 * reattempted. This repeats until the successful, upon which any
	 * {@link IOException} the latest {@link IOException} that was suppressed is
	 * thrown, or if no {@link IOException} occurred, the {@link Socket} is returned
	 * for convenience.
	 * </p>
	 * <p>
	 * This method will successfully open its connection <b>regardless</b> of
	 * whether an {@link IOException} is thrown, because it suppresses
	 * {@link IOException}s and continues looping. If an {@link IOException} occurs
	 * while restarting the {@link Socket}, it is suppressed until restarting
	 * succeeds (that is, until the {@link Socket} is successfully opened), then it
	 * is thrown. <i>This method therefore should not be called unless it is
	 * expected that the {@link Socket} will eventually be able to be reopened</i>
	 * (otherwise, this method equates to an infinite loop which continuously
	 * suppresses and stores more and more {@link IOException}s.)
	 * </p>
	 * 
	 * @return The {@link Socket}, after it was started successfully.
	 * @throws IOException The {@link IOException} that occurred, if any.
	 */
	public Socket restart() throws IOException {
		IOException suppressed = null;
		close();
		while (socket == null) {
			try {
				socket = new Socket(address, port);
			} catch (IOException e) {
				e.addSuppressed(suppressed);
				suppressed = e;
			}
		}
		if (suppressed != null)
			throw suppressed;
		return socket;
	}

	@Override
	public void close() throws IOException {
		if (socket != null)
			try {
				socket.close();
			} finally {
				socket = null;
			}
	}
}
