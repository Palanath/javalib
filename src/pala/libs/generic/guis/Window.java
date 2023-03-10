package pala.libs.generic.guis;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A {@link Window} should aim to stylize itself using the
 *
 * @author Palanath
 *
 */
public abstract class Window {

	public static class WindowLoadFailureException extends Exception {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

		public WindowLoadFailureException() {
		}

		public WindowLoadFailureException(final String message) {
			super(message);
		}

		public WindowLoadFailureException(final String message, final Throwable cause) {
			super(message, cause);
		}

		protected WindowLoadFailureException(final String message, final Throwable cause,
				final boolean enableSuppression, final boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public WindowLoadFailureException(final Throwable cause) {
			super(cause);
		}

	}

	public static final ApplicationProperties INITIAL_DEFAULT_APPLICATION_PROPERTIES = new ApplicationProperties();
	private static ApplicationProperties defaultApplicationProperties = INITIAL_DEFAULT_APPLICATION_PROPERTIES;

	static {
		INITIAL_DEFAULT_APPLICATION_PROPERTIES.themeStylesheet
				.put("pala/apps/arlith/graphics/stylesheets/default-styles.css");
	}

	private static final Object STAGE_WINDOW_KEY = new Object();

	public static boolean clean(final Stage stage) {
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY)) {
			((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
			stage.getProperties().remove(STAGE_WINDOW_KEY);
			return true;
		}
		return false;
	}

	/**
	 * Calls {@link #destroy()} on the {@link Window} being displayed on the
	 * specified {@link Stage}, if any. This process will also remove the
	 * {@link Scene} created by the {@link Window} from the {@link Stage}.
	 *
	 * @param stage The {@link Stage} to destroy the {@link Window} of.
	 */
	public static void destroyStage(final Stage stage) {
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY)) {
			((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
			stage.getProperties().remove(STAGE_WINDOW_KEY);
			stage.setScene(null);
		}
	}

	public static ApplicationProperties getDefaultApplicationProperties() {
		return defaultApplicationProperties;
	}

	public static void setDefaultApplicationProperties(final ApplicationProperties defaultApplicationProperties) {
		Window.defaultApplicationProperties = defaultApplicationProperties == null
				? INITIAL_DEFAULT_APPLICATION_PROPERTIES
				: defaultApplicationProperties;
	}

	private boolean called;

	/**
	 * Cleans up this window. This method should refrain from calling any methods on
	 * the {@link Stage} that this {@link Window} was originally displayed with, and
	 * should be callable multiple times with subsequent calls causing no
	 * detrimental side effects or unexpected behavior. A single call should stille
	 * be sufficient to destroy and clean up a {@link Window} however. This method
	 * does not have to (and should not) change the {@link Scene} of the
	 * {@link Stage} that this {@link Window} was originally displayed with.
	 */
	public abstract void destroy();

	/**
	 * Shows this {@link Window} on the specified {@link Stage}. A {@link Window}
	 * may only be shown on a single stage, and may only be shown once. If this
	 * method is called more than once an exception will be thrown. If the
	 * {@link #destroy()} method of the previous {@link Window} on the given
	 * {@link Stage} (if any) throws an exception, the exception will be propagated
	 * upwards. This process will prevent the previous {@link Window} from being
	 * removed from the given {@link Stage}. It is safe to call this method again if
	 * a failure occurs because of destruction of the previous {@link Window}, as
	 * this {@link Window} will not be marked as shown and will not have its
	 * {@link #show(Stage, ApplicationProperties)} method called.
	 *
	 * @param stage The {@link Stage} to show on.
	 * @throws WindowLoadFailureException In case this window fails to show itself
	 *                                    on the given {@link Stage}.
	 */
	public final void display(final Stage stage) throws WindowLoadFailureException {
		display(stage, getDefaultApplicationProperties());
	}

	public final synchronized void display(final Stage stage, final ApplicationProperties properties)
			throws WindowLoadFailureException {
		if (called)
			throw new RuntimeException("Cannot show a Window object twice.");
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY))
			((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
		stage.getProperties().put(STAGE_WINDOW_KEY, this);
		try {
			show(stage, properties);
		} catch (final Exception e) {
			throw new WindowLoadFailureException(e);
		}
		called = true;
	}

	public final synchronized void displayCarelessly(final Stage stage, final ApplicationProperties properties)
			throws WindowLoadFailureException {

		if (called)
			throw new RuntimeException("Cannot \"show\" a Window object twice.");
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY))
			try {
				((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
			} catch (final Exception e) {
			}
		stage.getProperties().put(STAGE_WINDOW_KEY, this);
		show(stage, properties);
		called = true;

	}

	public final boolean displayed() {
		return called;
	}

	protected abstract void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException;

	public final boolean showing(final Stage stage) {
		return stage.getProperties().get(STAGE_WINDOW_KEY) == this;
	}
}
