package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.Skill;
import reuo.resources.io.Preparation.None;

public class SkillLoader extends SimpleIndexedLoader<Skill> {// implements Preparable<StoredIndexPreparation<Preparation.None>>{
	FileChannel skillSource;
	
	public SkillLoader(){
		
	}
	
	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel());
		skillSource = new FileInputStream(prep.resource).getChannel();
	}
	
	public void reset() {
		super.reset();
		skillSource = null;
	}

	@Override
	public Skill get(int id) throws IOException{
		Entry entry = getEntry(id);
		
		if(!entry.isValid()){
			return(null);
		}
		
		CacheReference ref = cache.get(entry.id);
		Skill skill;
		
		if(ref != null){
			skill = ref.get();
			
			if(skill != null){
				return(skill);
			}
		}
		
		ByteBuffer data = ByteBuffer.allocate(entry.length);
		data.order(ByteOrder.LITTLE_ENDIAN);
		
		data.clear();
		skillSource.position(entry.offset).read(data);
		data.rewind();
		
		skill = new Skill(id);
		skill.load(data);
		
		ref = new CacheReference(id, skill);
		return(skill);
	}

}
