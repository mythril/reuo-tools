package reuo.resources;

import java.nio.*;

import reuo.resources.format.Formatter;

/**
 * Stores Bitmap glyphs for ASCII characters. Characters are offset by 32 with '<code>\0x20</code>'
 * being the index <code>0</code>, and <code>224</code> being the length of
 * the array of glyphs.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Font implements Loadable{
	private int id;
	final public Formatter formatter;
	private Bitmap[] glyphs;
	private int[] unknowns;
	
	/**
	 * Initializes the font with a resource identifier and a formatter that will
	 * be used to format the pixels and provide meta-information about the pixel
	 * format.
	 * 
	 * @param id the resource identifier
	 * @param formatter the pixel formatter
	 */
	public Font(int id, Formatter formatter){
		//super(id);
		this.id = id;
		this.formatter = formatter;
	}
	
	/**
	 * Gets the resource identifier of this Font.
	 * 
	 * @return the resource identifier (<code>this.id</code>)
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Gets the underlying array of glyphs. Changes made to this array are
	 * backed by this font.
	 * 
	 * @return the array of glyphs
	 */
	public Bitmap[] getGlyphs(){
		return glyphs;
	}
	
	/**
	 * Gets the array of glyph characters. This should be at least of length 224
	 * and may contain <code>null</code> elements. Any elements outside this
	 * range are undefined.
	 * 
	 * @return the bitmap array (<code>this.glyphs</code>)
	 * @throws IndexOutOfBoundsException if the requested character is outside
	 *             the supported ASCII range
	 * @see #glyphs
	 */
	public Bitmap getGlyph(char glyph) throws IndexOutOfBoundsException{
		return glyphs[(int)glyph - 32];
	}
	
	@Override
	public String toString(){
		return String.valueOf(id);
	}
	
	public void load(ByteBuffer in) throws IllegalArgumentException{
		glyphs = new Bitmap[224];
		unknowns = new int[224];
		// System.out.printf("len: %d\n",glyphs.length);
		int width, height;
		
		for(int i = 0; i < glyphs.length; i++){
			// System.out.printf("Index: %d\n",i);
			width = in.get() & 0xFF;
			height = in.get() & 0xFF;
			unknowns[i] = in.get();
			
			// System.out.printf("Width: %d\nHeight: %d\n",width,height);
			
			ByteBuffer glyphBuffer = in.duplicate();
			glyphBuffer.limit(glyphBuffer.position() + width * height * 2);
			glyphBuffer = glyphBuffer.slice().order(ByteOrder.LITTLE_ENDIAN);
			in.position(in.position() + glyphBuffer.limit());
			
			glyphs[i] = new Bitmap(-1, width, height, formatter.format(
				glyphBuffer,
				width,
				height,
				null));
			
		}
	}
}