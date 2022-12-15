package pala.libs.generic.util;

/**
 * An unchecked exception designed specifically for wrapping other exceptions.
 * The wrapped exception need not be unchecked; it can be any object that is an
 * instance of {@link Throwable}.
 *
 * @author Palanath
 *
 */
public class UncheckedExceptionWrapper extends RuntimeException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private final Class<? extends Throwable> c;

	public UncheckedExceptionWrapper(final Throwable cause) {
		super(cause);
		c = cause.getClass();
	}

	public Class<? extends Throwable> getType() {
		return c;
	}

}
