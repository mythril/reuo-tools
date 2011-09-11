package reuo.resources;

import java.nio.*;

import reuo.resources.format.Formatter;

/**
 * A color index for {@link Bitmap}s with 8-bit image data. Palettes are
 * {@link Loadable} and will reflect 16-bit color entries. The underlying
 * <code>Buffer</code> the pixels are stored in may be direct or non-direct and
 * is not garuanteed to be a specific type of <code>Buffer</code>.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Palette implements Loadable {
	protected Buffer data;
	protected int size;
	protected Formatter formatter;

	public Palette(Buffer data, int size, Formatter formatter) {
		this.data = data;
		this.size = size;
		this.formatter = formatter;
	}

	public int getSize() {
		return (size);
	}

	public Buffer getData() {
		return (data);
	}

	public void load(ByteBuffer in) throws IllegalArgumentException {
		if (formatter != null) {
			data = formatter.format(
					in.duplicate().order(ByteOrder.LITTLE_ENDIAN), size, 1,
					null);
		} else {
			// TODO might be a bug PALETTE
			data = in.duplicate().order(ByteOrder.LITTLE_ENDIAN)
					.limit(in.position() + size * 2);
		}
		in.position(in.position() + size * 2);
	}
}
