package reuo.resources.io;

public class TextureEntry extends Entry{
	public TextureEntry(int id, long offset, int length, int extra){
		super(id, offset, length, extra);
	}
	
	public int getWidth(){
		return(extra == 0 ? 64 : 128);
	}
	
	public int getHeight(){
		return(extra == 0 ? 64 : 128);
	}
}
