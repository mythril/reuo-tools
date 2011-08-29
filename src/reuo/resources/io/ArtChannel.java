package reuo.resources.io;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.MapMode;

import reuo.resources.*;
import reuo.util.Rect;

public class ArtChannel extends Cropper implements ReadableByteChannel{
	FileChannel in;
	ByteBuffer lineOffsets;
	ByteBuffer data;
	int width, height;
	int x, y;
	int run, xskip;
	
	public ArtChannel(FileChannel source){
		this.in = source;
	}
	
	public void close() throws IOException{
		in.close();
	}
	
	public boolean isOpen(){
		return in.isOpen();
	}
	
	public Rect getInsets(){
		crop.right = width - crop.right;
		crop.bottom = height - crop.bottom;
		
		return super.getInsets();
	}
	
	public void prepare(int length, int width, int height) throws IOException{
		long offset = in.position();
		this.width = width;
		this.height = height;
		
		lineOffsets = in.map(MapMode.READ_ONLY, offset, height * 2);
		data = in.map(MapMode.READ_ONLY, offset + height * 2, length);
		lineOffsets.order(ByteOrder.LITTLE_ENDIAN);
		data.order(ByteOrder.LITTLE_ENDIAN);
		
		resetCropping();
		y = x = run = xskip = 0;
		nextRun();
	}
	
	private void nextRun(){
		xskip = data.getShort() & 0xffff;
		run = data.getShort() & 0xffff;
		
		if(run == 0 && xskip == 0){
			xskip = width - x;
		}
	}
	
	public int read(ByteBuffer dst) throws IOException{
		int total = dst.limit() - dst.position();
		int pixel;
		
		while((y < height || xskip > 0 || run > 0) && dst.remaining() >= 2){			
			if(xskip > 0){
				dst.putShort((short)0);
				
				x++;
				xskip--;
			}else if(run > 0){
				pixel = data.getShort() | (1 << 15);
				dst.putShort((short)pixel);
				
				crop.top = Math.min(crop.top, y);
				crop.bottom = Math.max(crop.bottom, y);
				crop.left = Math.min(crop.left, x);
				crop.right = Math.max(crop.right, x);
				
				x++;
				run--;
				
				if(run == 0){
					nextRun();
				}
			}else{
				x = 0;
				
				if(++y < height){
					data.position(lineOffsets.getShort(y * 2) * 2);
					nextRun();
				}
			}
		}
		
		return total;
	}
}
