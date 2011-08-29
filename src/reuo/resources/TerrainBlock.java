package reuo.resources;


public class TerrainBlock extends Block<TerrainCell>{
	public TerrainBlock(){
		super(8, new TerrainCell[8 * 8]);
	}
}
