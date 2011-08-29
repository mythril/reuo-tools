package reuo.resources.io;

import java.io.*;

public interface Preparable<P extends Preparation<?>> {
	public void reset();
	public void prepare(P prep) throws IOException;
}
