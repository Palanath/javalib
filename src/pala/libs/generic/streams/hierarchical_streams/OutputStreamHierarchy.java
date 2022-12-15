package pala.libs.generic.streams.hierarchical_streams;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamHierarchy {
	static OutputStreamHierarchy fromFile(final File file) {
		return new FileOutputStreamHierarchy(file);
	}

	OutputStreamHierarchy getChild(String subpath);

	OutputStream getStream(String subpath) throws IOException;
}
