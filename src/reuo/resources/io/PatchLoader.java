package reuo.resources.io;

/*
public class PatchLoader extends Loader<PatchLoader.Entry, ByteBuffer>{
	public enum Type{
		MAP0("map0.mul"),
		MAP0_STATIC_INDEX("staidx0.mul"),
		MAP0_STATIC("statics0.mul"),
		ART_INDEX("art.mul"),
		ART("art.mul"),
		ANIM_INDEX("anim.idx"),
		ANIM("anim.mul"),
		SOUND_INDEX("soundidx.mul"),
		SOUND("sound.mul"),
		TEXTURE_INDEX("texidx.mu;"),
		TEXTURE("texmaps.mul"),
		GUMP_INDEX("gumpidx.mul"),
		GUMP("gumpart.mul"),
		MULTI_INDEX("multi.idx"),
		MULTI("multi.mul"),
		SKILL_INDEX("skills.idx"),
		SKILL("skills.mul"),
		TILE_DATA("tiledata.mul"),
		ANIM_DATA("animdata.mul")
		;
		
		final String file;
		
		private Type(String file){
			this.file = file;
		}
	}
	
	FileChannel patchSource;
	
	public PatchLoader(FileChannel patchSource){
		super(patchSource, 12 + 8);
		
		this.patchSource = patchSource;
	}
	
	public class Entry extends Loader.Entry{
		Type type;
		int id;
		
		public boolean isValid(){
			return(offset != -1 && length > 0);
		}
	}

	@Override
	public ByteBuffer get(int id) throws IllegalArgumentException, IOException{
		return null;
	}

	@Override
	protected Entry getEntryFromBuffer(ByteBuffer buffer){
		return null;
	}

	@Override
	public boolean isCached(int id){
		return false;
	}
}
*/