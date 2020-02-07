package electory.world;

public enum EnumWorldBiome {
	plains(64, 72), ocean(32, 48), plateau(96, 128)/* {
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
	public int minHeight;
	public int maxHeight;

	private EnumWorldBiome(int minHeight, int maxHeight) {
		this.biomeID = ordinal();
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		/*this.fillerBlock = Block.blockDirt;
		this.topBlock = Block.blockGrass;*/
	}
/*
	public IHeightMapGenerator createHeightMapGenerator(long seed) {
		return new BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 32, 1f / 32), 64, 72, 64, 72);
	}
*/
	public static EnumWorldBiome[] biomeList = EnumWorldBiome.values();
}
