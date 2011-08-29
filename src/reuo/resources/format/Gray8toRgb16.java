package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;

public class Gray8toRgb16 implements Formatter {
	private static Gray8toRgb16 formatter = new Gray8toRgb16();
	
	public static Gray8toRgb16 getFormatter(){
		return(formatter);
	}

	public Buffer format(ReadableByteChannel channel, int width, int height)
			throws IOException {
		return null;
	}

	public Buffer format(ByteBuffer from, int width, int height, Rect coords) {
		/*int curColor = 0;
		int expanded = 0;
		
		while(from.hasRemaining()){
			curColor = (from.get()/* & 0x3f*//*);
			expanded = (curColor << )
		}*/
		
		return null;
	}

}
