package electory.world;

public enum BiomeGenBase {
	plains, extremeHills, extremeHillsEdge, forest, forestHills, ocean/* {
		{
			fillerBlock = Block.blockGravel;
			topBlock = Block.blockSand;
		}
		
		@Override
		public IHeightMapGenerator createHeightMapGenerator(long seed) {
			return new BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 128, 1f / 128), 32, 63, 32, 63);
		}
	}*/;

	public int biomeID;
	/*public Block fillerBlock;
	public Block topBlock;*/

	private BiomeGenBase() {
		this.biomeID = ordinal();
		/*this.fillerBlock = Block.blockDirt;
		this.topBlock = Block.blockGrass;*/
	}
/*
	public IHeightMapGenerator createHeightMapGenerator(long seed) {
		return new BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 32, 1f / 32), 64, 72, 64, 72);
	}
*/
	public static BiomeGenBase[] biomeList = BiomeGenBase.values();
}
