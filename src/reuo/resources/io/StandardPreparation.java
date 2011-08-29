package reuo.resources.io;

import java.io.File;
import java.util.EnumSet;

import reuo.resources.Resource.Property;
import reuo.resources.format.Formatter;

public class StandardPreparation<P extends Enum<P> & Property> extends Preparation<P>{
	public Formatter formatter;
	
	public StandardPreparation(
		File resrc,
		Formatter formatter,
		EnumSet<P> properties
	){
		super(resrc, properties);
		this.formatter = formatter;
	}
	
	public boolean hasFormatter(){
		return (formatter != null);
	}
}
