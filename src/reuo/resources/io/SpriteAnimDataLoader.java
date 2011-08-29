package reuo.resources.io;

import java.io.IOException;

import reuo.resources.SpriteAnimData;
import reuo.resources.io.Preparation.None;
//TODO SpriteAnimDataLoader implement Preparable
public class SpriteAnimDataLoader extends FixedLengthLoader<SpriteAnimData> implements Preparable<StandardPreparation<Preparation.None>>{

	public void prepare(StandardPreparation<None> prep) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SpriteAnimData get(int id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static long getOffset(int id){
		return 0;
	}
	
	@Override
	public int getCapacity() {
		return 16384;
	}
}
