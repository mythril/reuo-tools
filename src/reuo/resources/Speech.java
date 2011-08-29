package reuo.resources;

/**
 * A word that contains translations or variants. The word may be referenced by
 * it's unique identifier. In the network protocol this is used to reduce the
 * transmission of common words as they can be referenced by their identifiers.
 * This also provides for rudementrary translations. Not all words have the same
 * amount of translations or variants.
 * <h3>Unknown</h3>
 * The layout of the translations currently not known. All speech data is stored
 * as UTF-8 characters, however the order these translations are stored seems
 * relavent but is unknown.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Speech{
	final public int id;
	final public String[] words;
	
	public Speech(int id, String[] words){
		this.id = id;
		this.words = words;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < words.length; i++){
			if(i != 0)
				buffer.append(", ");
			
			buffer.append(words[i]);
		}
		
		return buffer.toString();
	}
	
	/**
	 * Gets the identifier of the word
	 * 
	 * @return the identifier (<code>this.id</code>)
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Gets the translations of this word.
	 * 
	 * @return the translations of the word
	 */
	public String[] getWords(){
		return words;
	}
	
	/**
	 * Gets the number of translations this word has
	 * 
	 * @return the number of translations
	 */
	public int getTranslationCount(){
		return words.length;
	}
	
	/**
	 * Gets a specific translation of the word
	 * 
	 * @param translation the index of the translation
	 * @return the translated word; or <code>null</code> if no such
	 *         translation exists
	 */
	public String getWord(int translation){
		if(translation >= 0 && translation < words.length){
			return words[translation];
		}
		
		return null;
	}
}
