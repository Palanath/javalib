package pala.libs.generic.streams.hierarchical_streams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface InputStreamHierarchy {
	static InputStreamHierarchy fromFile(final File file) {
		return new FileInputStreamHierarchy(file);
	}

	InputStreamHierarchy getChild(String subpath);

	InputStream getStream(String subpath) throws IOException;
}
