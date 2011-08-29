package reuo.resources;

import java.util.*;

public class DecorationBlock{
	Map<Integer, Decoration> decorations = new TreeMap<Integer, Decoration>();
	
	public DecorationBlock(Map<Integer, Decoration> decorations){
		this.decorations = decorations;
	}
}
