package pala.libs.generic.modularity;

public class ModuleLoadException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ModuleLoadException() {
	}

	public ModuleLoadException(final String message) {
		super(message);
	}

	public ModuleLoadException(final String message, final Throwable cause) {
		super(message, cause);
	}

	protected ModuleLoadException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ModuleLoadException(final Throwable cause) {
		super(cause);
	}

}
