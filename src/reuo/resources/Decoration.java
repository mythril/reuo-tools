package reuo.resources;

import java.nio.ByteBuffer;

public class Decoration{
	private int tileId;
	private int x, y, z;
	
	public Decoration(ByteBuffer data){
		tileId = data.getShort();
		x = data.get();
		y = data.get();
		z = data.get();
		data.getInt();
	}
	
	public int TileId(){ return tileId; }
	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getZ(){ return z; }
}