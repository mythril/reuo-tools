package reuo.resources.io;

public class FontEntry extends Entry{
	public FontEntry(int id, long offset, int length, int extra){
		super(id, offset, length, extra);
	}
	
	@Override
	public boolean isValid(){
		return(super.isValid() && extra != 0);
	}
}
