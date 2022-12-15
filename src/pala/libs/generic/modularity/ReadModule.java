package pala.libs.generic.modularity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;

public class ReadModule<M> {

	private final Class<M> moduleClass;

	private final URLClassLoader loader;

	private final File file;

	private final String launchClass;

	ReadModule(final Class<M> loadedModuleClass, final URLClassLoader loader, final File file,
			final String launchClass) {
		this.moduleClass = loadedModuleClass;
		this.loader = loader;
		this.file = file;
		this.launchClass = launchClass;
	}

	public File getFile() {
		return file;
	}

	public URLClassLoader getLoader() {
		return loader;
	}

	public M load() throws ModuleLoadException {
		Class<?> cls;
		try {
			cls = loader.loadClass(launchClass);
		} catch (final ClassNotFoundException e) {
			throw new ModuleLoadException(
					"Failed to load the launch class for the module: \"" + file + "\"; the class could not be found.",
					e);
		}
		if (!moduleClass.isAssignableFrom(cls))
			throw new ModuleLoadException(
					"The loaded launch class for the module: \"" + file + "\" is not an instance of the Module class.");

		@SuppressWarnings("unchecked")
		final Class<? extends M> clz = (Class<? extends M>) cls;

		try {
			return clz.getDeclaredConstructor().newInstance();
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
				| InstantiationException e) {
			throw new ModuleLoadException("Unable to instantiate the module, \"" + file + "\"'s module class.", e);
		} catch (final IllegalAccessException e) {
			throw new ModuleLoadException("Unable to instantiate the module \"" + file
					+ "\". The Module Loader has no access to the module's class's constructor.", e);
		}
	}

}
