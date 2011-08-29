package reuo.resources.io;

import java.nio.*;

public class TileFormatter{
	public TileFormatter(){

	}
	
	final private static int STATE_ZEROS = 0;
	final private static int STATE_PIXELS = 1;
	int state = 0;
	
	public ByteBuffer format(ByteBuffer src){
		int x = 0, y = 0;
		int start = 21, end = 23;
		
		ByteBuffer dst = ByteBuffer.allocate(44 * 44 * 2);
		dst.order(ByteOrder.LITTLE_ENDIAN);
		dst.clear();
		
		while(dst.remaining() >= 2 && y < 44){
			if(state == STATE_ZEROS){
				dst.putShort((short)0);
				
				if(++x == start){
					state = STATE_PIXELS;
				}
			}else{
				dst.putShort((short)(src.getShort() | (1 << 15)));
				
				if(++x == end){
					state = STATE_ZEROS;
				}
			}
			
			if(x >= 44){
				if(y < 21){
					start--;
					end++;
				}else if(y > 21){
					start++;
					end--;
				}
				
				x = 0;
				// y++;
				
				if(++y == 21 || y == 22){
					state = STATE_PIXELS;
				}
			}
		}
		
		return dst;
	}
}
