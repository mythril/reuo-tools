package reuo.resources;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class TileData{
	int properties;
	int textureId;
	String name;
	
	public TileData(ByteBuffer data) {
		properties = data.getInt();
		textureId = data.getShort();
		try{
			name = new String(data.array(), data.position(), 20, "ASCII");
			if( name.indexOf((char) 0) >= 0){
				name = name.substring(0, name.indexOf((char) 0));
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			name = "";
		}
	}
	
	public TileData(int textureId, String name){
		this(textureId, name, 0);
	}
	
	public TileData(int textureId, String name, int properties){
		this.textureId = textureId;
		this.name = name;
		this.properties = properties;
	}
	
	public String getName(){ return name; }
	public int getProperties(){ return properties; }
	public int getTextureId(){ return textureId; }
}
