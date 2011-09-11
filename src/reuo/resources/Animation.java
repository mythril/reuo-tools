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

	@Override
	public void load(ByteBuffer in) throws BufferUnderflowException {
		System.out.printf("Starting position in buffer: %1$d\n",
				(int) in.position());
		System.out.printf("Starting capacity in buffer: %1$d\n",
				(int) in.capacity());
		// System.out.printf("Starting position in buffer: %1$d\n", (int)
		// in.position());

		in.order(ByteOrder.LITTLE_ENDIAN);

		// Load Palette
		pal = new Palette(in, 256, formatter);
		pal.load(in);

		System.out.printf("Position in buffer after Palette load: %1$d\n",
				(int) in.position());

		// Load frame lookup table
		int start = in.position();
		frameCount = (in.getInt() & 0xFFFFFFFF);
		System.out.printf("frameCount: %1$d\n", (int) frameCount);
		long[] lookup = new long[frameCount];
		frames = new ArrayList<Frame>(frameCount);

		for (int x = 0; x < frameCount; x += 1) {
			lookup[x] = start + (in.getInt() & 0xFFFFFFFF);
		}

		System.out.printf("Position in buffer after Frame lookup load: %1$d\n",
				(int) in.position());

		// Load frame data
		for (long i : lookup) {
			System.out.printf("Lookup 'i': %1$d, 0x%1$H\n", (int) i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.position((int) i);
			int centerX = in.getShort(); // signed
			System.out.printf("centerX: %1$d\n", (int) centerX);
			int centerY = in.getShort(); // signed
			System.out.printf("centerY: %1$d\n", (int) centerY);
			int width = in.getShort() & 0xFFFF;
			System.out.printf("width: %1$d\n", (int) width);
			int height = in.getShort() & 0xFFFF;
			System.out.printf("height: %1$d\n", (int) height);
			ByteBuffer palPixels = ByteBuffer.allocate(width * height);

			centerX = centerY = 0;

			long header = Integer.reverse(in.getInt() & 0xFFFFFFFF);
			System.out.println(header == (int) header);

			// Plot pixels
			while (header != 0x7FFF7FFF) {
				System.out.printf("Header: 0x%1$H\n", header);
				binout((int)header);

				int runLength = (int) (header & 0xFFF);
				System.out.printf("runLength: %1$d\n", (int) runLength);
				binout(runLength);

				int runX = (int) (header << 10); // ought to be signed
				runX = runX & 0x3ff;
				//runX = ((runX & 0x200) == 0x200) ? ((runX & 0x1FF) | 0x80000000) : runX;
				
				System.out.printf("runX: %1$d\n", (int) runX);
				binout(runX);

				int runY = (int) (header >>> 22); // ought to be signed
				// runY = (runY ^ 0x200) - 0x200;
				runY = runY & 0x3FF;
				System.out.printf("runY: %1$d\n", (int) runY);
				binout(runY);

				int uX = centerX + runX;
				int uY = centerY + runY;

				// TODO crop the frame and include insets
				int putPos = (uY * width) + uX;

				palPixels.position(putPos);

				while (runLength != 0) {
					palPixels.put(in.get());
					runLength -= 1;
				}
				header = in.getInt() & 0xFFFFFFFF;
			}

			frames.add(new Frame((int) 0, width, height, pal, palPixels,
					(Rect) null));
		}
	}
}
