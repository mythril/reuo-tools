package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.Speech;
import reuo.resources.io.Preparation.None;

public class SpeechLoader extends MemoryIndexedLoader<SpeechEntry, Speech> {// implements Preparable<StandardPreparation<Preparation.None>>{
	FileChannel channel;
	
	public SpeechLoader(){
		
	}
	
	public void prepare(StandardPreparation<None> prep) throws IOException {
		this.channel = new FileInputStream(prep.resource).getChannel();
		generateIndex();
	}

	public void reset() {
		super.reset();
		channel = null;
	}
	
	@Override
	protected void generateIndex() throws IOException{
		ByteBuffer header = ByteBuffer.allocate(4);
		header.order(ByteOrder.BIG_ENDIAN);
		
		synchronized(channel){
			long size = channel.size();
			long pos = 0;
			
			while(pos < size){
				header.clear();
				channel.read(header, pos);
				header.rewind();
				
				Entry translation = new Entry(
					header.getShort(),
					pos,
					header.getShort(),
					0);
				/*new Entry();
				translation.id = header.getShort() & 0xffff;
				translation.length = header.getShort() & 0xffff;
				translation.offset = pos;
				*/
				
				if(translation.isValid()){
					SpeechEntry entry = getEntry(translation.id);
					
					if(entry == null){
						entry = new SpeechEntry(translation.id, 0, 0, 0);
						//entry.id = translation.id;
						entries.put(entry.id, entry);
					}
					
					entry.translations.add(translation);
				}
				
				pos += 4 + translation.length;
			}
		}
	}

	@Override
	public Speech get(int id) throws IOException{
		CacheReference ref = cache.get(id);
		Speech speech;
		
		if(ref != null){
			speech = ref.get();
			
			if(speech != null){
				return speech;
			}
		}
		
		SpeechEntry entry = getEntry(id);
		String[] words = new String[entry.translations.size()];
		
		for(int i=0; i < words.length; i++){
			Entry wordEntry = entry.translations.get(i);
			
			ByteBuffer buffer = ByteBuffer.allocate(wordEntry.length);
			buffer.order(ByteOrder.BIG_ENDIAN);

			buffer.clear();
			channel.read(buffer, wordEntry.offset + 4);
			buffer.rewind();
			
			words[i] = new String(buffer.array(), "UTF-8");
		}
		
		speech = new Speech(id, words);
		new CacheReference(id, speech);
		return(speech);
	}
}
