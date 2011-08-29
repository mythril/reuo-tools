package reuo.resources.io;

import java.io.File;
import java.util.EnumSet;

import reuo.resources.Resource;
import reuo.resources.Resource.Property;

public class Preparation<P extends Enum<P> & Property> {
	public File resource;
	public EnumSet<P> properties;
	
	public Preparation(File resrc, EnumSet<P> properties){
		resource = resrc;
		this.properties = properties;
	}
	
	public boolean isValid(){
		return resource.exists();
	}
	
	public enum None implements Resource.Property{
		;
		public Class<?> getType() {
			return null;
		}
	}
}
