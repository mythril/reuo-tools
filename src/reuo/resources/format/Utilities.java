package reuo.resources.format;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.*;

import javax.swing.JTable;

import reuo.resources.*;
import reuo.resources.view.AsyncLoaderModel;
import reuo.util.Rect;

final public class Utilities{
	final public static DataMetrics readArray(ByteBuffer in){
		return readArray(in, in.limit() - in.position());
	}
	
	final public static DataMetrics readArray(
		ByteBuffer in,
		int length
	){
		DataMetrics metrics;
		
		if(in.isDirect()){
			metrics = new DataMetrics(new byte[length], 0, length);
			in.get(metrics.array);
		}else{
			metrics = new DataMetrics(in.array(), in.position(), length);
			in.position(in.position() + length);
		}
		
		return metrics;
	}
	
	final public static void prune(JTable table, AsyncLoaderModel<?> model){
		int min = 0, max = 0;
		Rectangle bounds = table.getVisibleRect();
		
		min = table.rowAtPoint(new Point(bounds.x, bounds.y));
		max = table.rowAtPoint(new Point(bounds.x, bounds.y + bounds.height-1));
		
		model.pruneTo(min, max);
	}
	
	final public static void paint(
		Graphics g,
		Bitmap bitMap,
		Image image,
		int x,
		int y
	){
		Rect insets = bitMap.getInsets();
		
		if(insets != null){
			x += insets.left;
			y += insets.top;
		}
		
		g.drawImage(image, x, y, null);
	}
	
	final public static ColorModel getColorModel(Bitmap bmp, int alphaBits){
		return null;
	}
	
	final public static BufferedImage getImage(Bitmap bmp, int alphaBits){
		//long start = System.currentTimeMillis();
		
		if(bmp == null || bmp.getData() == null){
			return null;
		}
		
		if(bmp.getImageWidth() <= 0 || bmp.getImageHeight() <= 0){
			return null;
		}
		
		BufferedImage image;
		boolean hasAlpha = alphaBits > 0;
		ColorModel colorModel = null;
		SampleModel sampleModel = null;
		int bits = 0;
		int bitsPerComponent = 0;
		DataBuffer buffer = null;
		
		if(bmp.getData() instanceof ByteBuffer){
			byte[] array = ((ByteBuffer)bmp.getData()).array();
			buffer = new DataBufferByte(array, array.length);
			
			if(hasAlpha){
				bits = 32;
			}else{
				bits = 24;
			}
			bitsPerComponent = 8;
		}else if(bmp.getData() instanceof ShortBuffer){
			short[] array = ((ShortBuffer)bmp.getData()).array();
			buffer = new DataBufferUShort(array, array.length);
			bits = 16;
			bitsPerComponent = 5;
		}else if(bmp.getData() instanceof IntBuffer){
			int[] array = ((IntBuffer)bmp.getData()).array();
			buffer = new DataBufferInt(array, array.length);
			bits = 32;
			bitsPerComponent = 8;
		}
		
		//if(bitsPerComponent != 8 && bitsPerComponent != 16 && bitsPerComponent != 32){
		if(bitsPerComponent == 5){
			int mask = (int)(Math.pow(2, bitsPerComponent)) - 1;
			int alphaMask = (int)(Math.pow(2, alphaBits)) - 1;
			
			int[] masks = new int[3 + (hasAlpha ? 1 : 0)];
			masks[0] = mask << bitsPerComponent * 2;
			masks[1] = mask << bitsPerComponent;
			masks[2] = mask;
			if(hasAlpha) masks[3] = alphaMask << (bits-1);
			
			if(hasAlpha){
				colorModel = new DirectColorModel(
					bits,
					masks[0], masks[1], masks[2], masks[3]
				);
			}else{
				colorModel = new DirectColorModel(
					bits - alphaBits,
					masks[0], masks[1], masks[2]
				);
			}
			
			sampleModel = new SinglePixelPackedSampleModel(
				buffer.getDataType(),
				bmp.getImageWidth(), bmp.getImageHeight(),
				masks
			);
		}else{
			colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB),
				true, true,
				Transparency.BITMASK, //true ? Transparency.BITMASK : Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE
			);
			
			sampleModel = new PixelInterleavedSampleModel(
				DataBuffer.TYPE_BYTE,
				bmp.getImageWidth(), bmp.getImageHeight(),
				4, bmp.getImageWidth() * 4,
				new int[] { 0, 1, 2, 3 }
			);
		}
		
		image = new BufferedImage(
			colorModel,
			WritableRaster.createWritableRaster(sampleModel, buffer, null),
			false,
			null
		);
		
		//System.out.printf("%dms\n", System.currentTimeMillis() - start);
		return image;
	}
	
	final public static Bitmap paletteToBitmap(Palette toConvert){
		return new Bitmap(-1, toConvert.getSize(), 1, toConvert.getData());
	}
}
