package pala.libs.generic.modularity;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pala.libs.generic.Datamap;

public class ModuleLoader<M> {
	private final Class<M> moduleClass;

	private final String manifestLocation;
	private final String launchClassKey;

	public ModuleLoader(final Class<M> moduleClass, final String manifestLocation) {
		this(moduleClass, manifestLocation, "launch-class");
	}

	public ModuleLoader(final Class<M> moduleClass, final String manifestLocation, final String launchClassKey) {
		this.moduleClass = moduleClass;
		this.manifestLocation = manifestLocation;
		this.launchClassKey = launchClassKey;
	}

	public ReadModule<M> read(final File file) throws ModuleLoadException {

		try (ZipFile jar = new JarFile(file)) {
			final ZipEntry entry = jar.getEntry(manifestLocation);
			if (entry == null)
				throw new ModuleLoadException("Invalid module; The manifest file could not be located inside the jar.");
			final Datamap datamap = Datamap.read(jar.getInputStream(entry));

			final String launchClass = datamap.get(launchClassKey);
			final URL location = file.toURI().toURL();

			if (launchClass == null)
				throw new ModuleLoadException(
						"Invalid module manifest file. The manifest must denote a launch class for the module.");

			return new ReadModule<>(moduleClass, new URLClassLoader(new URL[] { location }), file, launchClass);

		} catch (final Exception e) {
			throw new ModuleLoadException("An unexpected error occurred while loading a module.", e);
		}
	}

}
