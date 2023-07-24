package pala.libs.generic.data.files;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

public class AutosavingPropertyObject extends PropertyObject {

	private File file;
	private BiConsumer<File, IOException> exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

	private static final BiConsumer<File, IOException> DEFAULT_EXCEPTION_HANDLER = (t, u) -> u.printStackTrace();

	public BiConsumer<File, IOException> getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(BiConsumer<File, IOException> exceptionHandler) {
		this.exceptionHandler = exceptionHandler == null ? DEFAULT_EXCEPTION_HANDLER : exceptionHandler;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public AutosavingPropertyObject(File file) {
		this.file = file;
	}

	@Override
	protected void markDirty() {
		if (file != null)
			try {
				save(file);
			} catch (IOException e) {
				exceptionHandler.accept(file, e);
			}
	}

}
