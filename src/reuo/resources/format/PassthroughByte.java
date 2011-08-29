package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;

public class PassthroughByte implements Formatter {
	private static PassthroughByte formatter = new PassthroughByte();
	
	public static PassthroughByte getFormatter(){
		return(formatter);
	}

	public Buffer format(ReadableByteChannel channel, int width, int height)
			throws IOException {
		ByteBuffer retBuf = ByteBuffer.allocate(width * height);
		retBuf.order(ByteOrder.LITTLE_ENDIAN);
		retBuf.clear();
		channel.read(retBuf);
		retBuf.rewind();
		return retBuf;
	}

	public Buffer format(ByteBuffer from, int width, int height, Rect coords) {
		return from.duplicate();
	}
}
