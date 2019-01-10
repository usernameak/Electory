package electory.world;

import electory.block.Block;

public enum BiomeGenBase {
	plains, extremeHills, extremeHillsEdge, forest, forestHills;
	
	public int biomeID;
	public Block fillerBlock;
	public Block topBlock;

	private BiomeGenBase() {
		this.biomeID = ordinal();
		this.fillerBlock = Block.blockDirt;
		this.topBlock = Block.blockGrass;
	}
	
	public static BiomeGenBase[] biomeList = BiomeGenBase.values();
}
