package reuo.resources.io;

import java.nio.ByteBuffer;
import java.util.*;

public interface DataLayout<P extends Enum<P>>{
	public void reset();
	public EnumSet<P> getProperties();
	public SortedMap<Integer, Integer> getResolution();
	public boolean resolve(Map<Integer, ByteBuffer> ins);
}
