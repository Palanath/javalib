package pala.libs.generic.networking.sockets;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * This class simply provides the {@link #acceptConnection()} method which
 * returns a {@link Client} on wrapping the newly accepted {@link Socket}.
 * </p>
 * <p>
 * This class acts as the server portion of the API that encapsulates the
 * {@link Client} class.
 * </p>
 *
 * @author Palanath
 *
 */
public class Server implements Closeable {
	private final ServerSocket server;

	public Server(final ServerSocket server) {
		this.server = server;
	}

	/**
	 * Accepts an incoming connection. This method blocks until it has a connection
	 * to accept. The underlying {@link Socket} that gets accepted is wrapped in a
	 * {@link Client} object, and returned.
	 *
	 * @return A {@link Client} representing this machine's end of the connection.
	 *         The {@link Client} sends the serialization stream header, which gets
	 *         accepted by the {@link Client} on the other end preparing the stream
	 *         for {@link Serializable} objects.
	 * @throws IOException In case an {@link IOException} occurs while
	 *                     {@link ServerSocket#accept() accepting the connection} or
	 *                     while {@link Client#Client(Socket) creating the client}.
	 */
	public Client acceptConnection() throws IOException {
		return new Client(server.accept());
	}

	@Override
	public void close() throws IOException {
		server.close();
	}

}
