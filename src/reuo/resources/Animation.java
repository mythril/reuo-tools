package reuo.resources;

import java.nio.*;
import java.util.ArrayList;

import reuo.resources.format.Formatter;
import reuo.util.Rect;

public class Animation implements Loadable {
	public class Frame extends PalettedBitmap {
		// Stub for later extension
		public Frame(int id, int width, int height, Palette pal, Buffer data,
				Rect insets) {
			super(id, width, height, pal, data, insets);
		}
	}

	public final int id;
	public final Formatter formatter;

	public Animation(int id, Formatter formatter) {
		this.id = id;
		this.formatter = formatter;
	}

	Palette pal;
	int frameCount;

	ArrayList<Frame> frames;
	
	public ArrayList<Frame> getFrames() {
		return frames;
	}
	
	public Frame getFrame(int frame) {
		return frames.get(frame);
	}

	private static String bin(int out) {
		StringBuffer repeated = new StringBuffer();

		for (int i = 0; i < (Integer.numberOfLeadingZeros(out)); i++) {
			repeated.append("0");
		}

		return repeated.append(Integer.toBinaryString(out)).toString();
	}

	private static void binout(int out) {
		System.out.println(bin(out));
	}

	private static String hex(int out) {
		StringBuffer repeated = new StringBuffer("0x");

		for (int i = 0; i < (Integer.numberOfLeadingZeros(out) / 4); i++) {
			repeated.append("0");
		}

		return repeated.append(Integer.toHexString(out).toUpperCase())
				.toString();
	}

	private static void hexout(int out) {
		System.out.println(hex(out));
	}

	@Override
	public void load(ByteBuffer in) throws BufferUnderflowException {
//		System.out.printf("Starting position in buffer: %1$d\n",
//				(int) in.position());
//		System.out.printf("Starting capacity in buffer: %1$d\n",
//				(int) in.capacity());
		// System.out.printf("Starting position in buffer: %1$d\n", (int)
		// in.position());

		in.order(ByteOrder.LITTLE_ENDIAN);
		
		// Load Palette
		pal = new Palette(in, 256, formatter);
		pal.load(in);

//		System.out.printf("Position in buffer after Palette load: %1$d\n",
//				(int) in.position());

		// Load frame lookup table
		int start = in.position();
		frameCount = in.getInt();
//		System.out.printf("frameCount: %1$d\n", frameCount);
		int[] lookup = new int[frameCount];
		frames = new ArrayList<Frame>(frameCount);

		for (int x = 0; x < frameCount; x += 1) {
			lookup[x] = start + (in.getInt() & 0xFFFFFFFF);
		}

//		System.out.printf("Position in buffer after Frame lookup load: %1$d\n",
//				(int) in.position());

		// Load frame data
		for (int i : lookup) {
//			System.out.printf("Lookup 'i': %1$d, 0x%1$H\n", i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.position(i);
			int centerX = in.getShort(); // signed
//			System.out.printf("centerX: %1$d\n", centerX);
			int centerY = in.getShort(); // signed
//			System.out.printf("centerY: %1$d\n", centerY);
			int width = in.getShort() & 0xFFFF;
//			System.out.printf("width: %1$d\n", width);
			int height = in.getShort() & 0xFFFF;
//			System.out.printf("height: %1$d\n", height);
			ByteBuffer palPixels = ByteBuffer.allocate(width * height);

			// centerX = centerY = 0;

			// int header = Integer.reverse(in.getInt() & 0xFFFFFFFF);
			// int header = Integer.reverseBytes(in.getInt() & 0xFFFFFFFF);
			int header = in.getInt() & 0xFFFFFFFF;

			// Plot pixels
			while (header != 0x7FFF7FFF) {
//				System.out.printf("Header: 0x%1$H\n", header);
//				hexout(header);
//				binout(header);

				int runLength = ((header & 0xFFF) ^ 0x200) - 0x200; 
//				System.out.printf("runLength: %1$d\n", runLength);
//				binout(runLength);

				int runX = (header >> 22); // ought to be signed
//				System.out.printf("runX: %1$d\n", runX);
//				binout(runX);

				int runY = (((header >> 12) & 0x3FF) ^ 0x200) - 0x200; // ought
																		// to be
																		// signed
				
//				System.out.printf("runY: %1$d\n", runY);
//				binout(runY);

				int uX = centerX + runX;
				int uY = height + (centerY + runY);
				
				//System.out.println("Final X:" + uX );
				//System.out.println("Final Y:" + uY );
				
				// TODO crop the frame and include insets
				int putPos = (uY * width) + uX;

				palPixels.position(putPos);

				while (runLength != 0) {
					palPixels.put(in.get());
					runLength -= 1;
				}
				//System.out.println(id);
				//System.exit(0);
				header = in.getInt() & 0xFFFFFFFF;
			}

			frames.add(new Frame(0, width, height, pal, palPixels, (Rect) null));
		}
	}
}
