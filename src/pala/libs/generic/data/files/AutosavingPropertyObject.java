package pala.libs.generic.data.files;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

public class AutosavingPropertyObject extends PropertyObject {

	private File file;
	private BiConsumer<File, IOException> exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

	private static final BiConsumer<File, IOException> DEFAULT_EXCEPTION_HANDLER = (t, u) -> u.printStackTrace();

	protected BiConsumer<File, IOException> getExceptionHandler() {
		return exceptionHandler;
	}

	protected void setExceptionHandler(BiConsumer<File, IOException> exceptionHandler) {
		this.exceptionHandler = exceptionHandler == null ? DEFAULT_EXCEPTION_HANDLER : exceptionHandler;
	}

	protected File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}

	protected AutosavingPropertyObject(File file) {
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
