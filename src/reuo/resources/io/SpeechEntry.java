package reuo.resources.io;

import java.util.*;

public class SpeechEntry extends Entry{
	final List<Entry> translations = new ArrayList<Entry>();
	
	public SpeechEntry(int id, long offset, int length, int extra){
		super(id, offset, length, extra);
	}
	
	@Override
	public boolean isValid(){
		return true;
	}
}
