package reuo.resources;

import java.nio.*;

import reuo.util.Rect;

public class PalettedBitmap extends Bitmap{
	private Palette pal;
	private Palette override;
	
	public PalettedBitmap(int id, int width, int height, Palette pal, Buffer data, Rect insets) {
		super(id, width, height, data, insets);
		this.pal = pal;
	}
	
	public PalettedBitmap(int id, int width, int height, Buffer data,
			Rect insets) {
		this(id, width, height, null, data, insets);
	}

	public Palette getPalette() {
		if (override != null) {
			return override;
		}
		return pal;
	}
	
	public void setPalette(Palette set) {
		override = set;
	}
	
}
