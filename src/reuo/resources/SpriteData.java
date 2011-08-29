package reuo.resources;

import java.io.UnsupportedEncodingException;
import java.nio.*;
import java.util.EnumSet;

public class SpriteData implements Loadable{
	public EnumSet<SpriteProperties> properties;
	public int weight;
	public int quality;
	public int unknown1;
	public int unknown2;
	public int quantity;
	public int animation;
	public int unknown3;
	public int hue;
	public int unknown4;
	public int unknown5;
	public int height;
	public String name;
	
	public SpriteData(){
		
	}

	public void load(ByteBuffer in) throws BufferUnderflowException {
		properties = SpriteProperties.unpackInt(in.getInt());
		weight = in.get();
		quality = in.get();
		unknown1 = in.getShort();
		unknown2 = in.get();
		quantity = in.get();
		animation = in.getShort();
		unknown3 = in.get();
		hue = in.get();
		unknown4 = in.get();
		unknown5 = in.get();
		height = in.get();
		
		try{
			name = new String(in.array(), in.position(), 20, "ASCII");
			if( name.indexOf((char) 0) >= 0){
				name = name.substring(0, name.indexOf((char) 0));
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			name = "";
		}
	}
}
