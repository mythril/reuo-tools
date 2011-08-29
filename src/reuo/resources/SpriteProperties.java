package reuo.resources;

import java.util.*;

public enum SpriteProperties {
	BACKGROUND(0x1),
	WEAPON(0x2),
	TRANSPARENT(0x4),
	TRANSLUCENT(0x8),
	WALL(0x10),
	DAMAGING(0x20),
	IMPASSABLE(0x40),
	WET(0x80),
	UNKNOWN1(0x100),
	SURFACE(0x200),
	BRIDGE(0x400),
	GENERIC(0x800),
	WINDOW(0x1000),
	NOSHOOT(0x2000),
	ARTICLE_A(0x4000),
	ARTICLE_AN(0x8000),
	INTERNAL(0x10000),
	FOLIAGE(0x20000),
	PARTIAL_HUE(0x40000),
	UNKNOWN_2(0x80000),
	MAP(0x100000),
	CONTAINER(0x200000),
	WEARABLE(0x400000),
	LIGHT_SOURCE(0x800000),
	ANIMATION(0x1000000),
	NO_DIAGONAL(0x2000000),
	UNKNOWN_3(0x4000000),
	ARMOR(0x8000000),
	ROOF(0x10000000),
	DOOR(0x20000000),
	STAIR_BACK(0x40000000),
	STAIR_RIGHT(0x80000000);
	
	final private int value;
	final private static HashMap<Integer, SpriteProperties> flags = new HashMap<Integer, SpriteProperties>();
	static {
		for(SpriteProperties p:values()){
			flags.put(p.getValue(), p);
		}
	}
	
	private SpriteProperties(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static SpriteProperties fromValue(int value){
		return flags.get(value);
	}
	
	public static int packEnumSet(EnumSet<SpriteProperties> es){
		int packed = 0;
		for(SpriteProperties p:es){
			packed |= p.getValue();
		}
		return packed;
	}
	
	public static EnumSet<SpriteProperties> unpackInt(int flags){
		EnumSet<SpriteProperties> sdfes = EnumSet.noneOf(SpriteProperties.class);
		
		for (SpriteProperties sdf : values()) {
			int value = sdf.getValue();
			
			if((flags & value)  == value){
				sdfes.add(sdf);
			}
		}
		
		return null;
	}
}
