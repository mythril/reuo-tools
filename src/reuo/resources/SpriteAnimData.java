package reuo.resources;

import java.nio.*;

public class SpriteAnimData implements Loadable {
	public int[] frames;
	public int unknown;
	public int frameCount;
	public int frameInterval;
	public int frameStartInterval;
	
	public SpriteAnimData(){
		
	}
	
	public void load(ByteBuffer in) throws BufferUnderflowException {
		byte[] bFrames = new byte[64];
		in.get(bFrames);
		unknown = in.get();
		this.frames = new int[in.get()];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = bFrames[i];
		}
		frameInterval = in.get();
		frameStartInterval = in.get();
	}
}
