package pala.libs.generic.data.files;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.BiConsumer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import pala.libs.generic.json.JSONObject;
import pala.libs.generic.json.JSONParser;
import pala.libs.generic.streams.CharacterStream;

public class SimplePropertyObject extends PropertyObject {

	private final javafx.beans.property.ObjectProperty<File> file = new SimpleObjectProperty<>();
	private final ReadOnlyBooleanWrapper dirty = new ReadOnlyBooleanWrapper();
	private BiConsumer<File, Exception> errorHandler = DEFAULT_EXCEPTION_HANDLER;

	private static final BiConsumer<File, Exception> DEFAULT_EXCEPTION_HANDLER = (t, u) -> u.printStackTrace();

	@Override
	protected void markDirty() {
		dirty.set(true);
	}

	/**
	 * Saves this {@link SimplePropertyObject} to disk.
	 */
	public void save() {
		if (file.get() != null)
			try {
				save(file.get());
			} catch (IOException e) {
				errorHandler.accept(file.get(), e);
				return;
			}
		dirty.set(false);
	}

	/**
	 * Discards changes and reloads from disk.
	 */
	public void reload() {
		if (file.get() != null)
			try (FileReader reader = new FileReader(getFile())) {
				load((JSONObject) new JSONParser().parse(CharacterStream.from(reader)));
			} catch (IOException | PropertyException e) {
				errorHandler.accept(file.get(), e);
				return;
			}
		dirty.set(false);
	}

	public final ReadOnlyBooleanProperty dirtyProperty() {
		return this.dirty.getReadOnlyProperty();
	}

	public final boolean isDirty() {
		return this.dirtyProperty().get();
	}

	public final javafx.beans.property.ObjectProperty<File> fileProperty() {
		return this.file;
	}

	public final File getFile() {
		return this.fileProperty().get();
	}

	public final void setFile(final File file) {
		this.fileProperty().set(file);
	}

}
