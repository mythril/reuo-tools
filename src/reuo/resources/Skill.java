package reuo.resources;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * A named skill that may be an action. Skills have a resource identifier that
 * must be unqiue to all skills.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Skill implements Loadable{
	final public int id;
	private boolean isAction;
	private String name;
	
	public Skill(int id){
		this.id = id;
	}
	
	public void load(ByteBuffer data){
		isAction = data.get() > 0;
		
		try{
			name = new String(
				data.array(),
				data.position(),
				data.limit() - data.position(),
				"ASCII");
			
			int len = name.indexOf('\0');
			
			if(len >= 0){
				name = name.substring(0, len);
			}
		}catch(UnsupportedEncodingException e){
			name = null;
		}
	}
	
	public boolean isAction(){
		return(isAction);
	}
	
	public String getName(){
		return(name);
	}
}
