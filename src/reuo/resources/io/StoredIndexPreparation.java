package reuo.resources.io;

import java.io.File;
import java.util.EnumSet;

import reuo.resources.Resource.Property;
import reuo.resources.format.Formatter;

public class StoredIndexPreparation<P extends Enum<P> & Property> extends StandardPreparation<P> {
	File index;
	
	public StoredIndexPreparation(
		File idx,
		File resrc,
		Formatter formatter,
		EnumSet<P> properties
	){
		super(resrc,formatter,properties);
		index = idx;
	}
	
	@Override
	public boolean isValid() {
		boolean validity = true;
		if(!index.exists()){
			validity = false;
		}else if(!resource.exists()){
			validity = false;
		}
		return validity;
	}
}
