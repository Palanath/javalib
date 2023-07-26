package pala.libs.generic.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketException;

import pala.libs.generic.JavaTools;

public interface PortReader {
	void handle(byte[] incomingMessage);

	/**
	 * <p>
	 * Creates a {@link ServerSocket} and listens to it for incoming connections on
	 * a new {@link Thread}. Every time a new connection is received, the
	 * {@link Thread} reads all the bytes from the connection and closes it, then
	 * calls {@link #handle(byte...)} with the read bytes.
	 * </p>
	 * <p>
	 * The caller is expected to stop the listening process and clean up the
	 * {@link PortReader} by closing the {@link ServerSocket} returned by this
	 * method. When this is done, the {@link Thread} listening for new connections
	 * will receive a {@link SocketException} while it waits for a new connection,
	 * or when it tries to start waiting for a new connection. Upon receiving a
	 * {@link SocketException}, the {@link Thread} will consume the exception and
	 * shut down.
	 * </p>
	 * <p>
	 * If the {@link Thread} encounters any other type of {@link Exception}, it
	 * prints the {@link Exception}'s stacktrace and then shuts down, closing the
	 * {@link ServerSocket} as well.
	 * </p>
	 * <p>
	 * The {@link Thread} is started before this method returns. If an
	 * {@link IOException} occurs while creating the {@link ServerSocket}, it is
	 * propagated to the caller. This can happen e.g. if the provided port is
	 * already bound to by another server.
	 * </p>
	 * 
	 * @param port The port to open the {@link ServerSocket} on.
	 * @return The {@link ServerSocket} that gets created on the specified port.
	 * @throws IOException If an {@link IOException} occurs while creating the
	 *                     {@link ServerSocket}.
	 */
	default ServerSocket listen(int port) throws IOException {
		return listen(new ServerSocket(port));
	}

	/**
	 * <p>
	 * Listens to the provided {@link ServerSocket} for incoming connections on a
	 * new {@link Thread}. Every time a new connection is received, the
	 * {@link Thread} reads all the bytes from the connection and closes it, then
	 * calls {@link #handle(byte...)} with the read bytes.
	 * </p>
	 * <p>
	 * The caller is expected to stop the listening process and clean up the
	 * {@link PortReader} by closing the {@link ServerSocket}. When this is done,
	 * the {@link Thread} listening for new connections will receive a
	 * {@link SocketException} while it waits for a new connection, or when it tries
	 * to start waiting for a new connection. Upon receiving a
	 * {@link SocketException}, the {@link Thread} will consume the exception and
	 * shut down.
	 * </p>
	 * <p>
	 * If the {@link Thread} encounters any other type of {@link Exception}, it
	 * prints the {@link Exception}'s stacktrace and then shuts down, closing the
	 * {@link ServerSocket} as well.
	 * </p>
	 * <p>
	 * The {@link Thread} is started before this method returns. If an
	 * {@link IOException} occurs while creating the {@link ServerSocket}, it is
	 * propagated to the caller. This can happen e.g. if the provided port is
	 * already bound to by another server.
	 * </p>
	 * 
	 * @param socket The {@link ServerSocket} to listen to.
	 * @return The same {@link ServerSocket} provided.
	 */
	default ServerSocket listen(ServerSocket socket) {
		Thread t = new Thread(() -> {
			try (ServerSocket s = socket) {
				while (true) {
					byte[] bytes;
					try (InputStream is = s.accept().getInputStream()) {
						bytes = JavaTools.readAllBytes(is);
					}
					handle(bytes);
				}
			} catch (SocketException e) {// Thrown when socket is closed.
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
		return socket;
	}
}
