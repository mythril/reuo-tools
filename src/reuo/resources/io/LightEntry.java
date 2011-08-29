package reuo.resources.io;

public class LightEntry extends Entry{
	public LightEntry(int id, long offset, int length, int extra){
		super(id, offset, length, extra);
	}
	
	@Override
	public boolean isValid(){
		return(offset >= 0 && length > 0 && extra != 0);
	}
	
	public int getWidth(){
		return((extra >> 16) & 0xffff);
	}
	
	public int getHeight(){
		return(extra & 0xffff);
	}
}
