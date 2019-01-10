package electory.client.render;

import electory.block.Block;

public class AtlasManager {
	public static IAtlasSpriteManager blockSpriteManager = new AtlasSpriteManagerImpl(16, 16);
	
	public static void registerAllTerrainSprites() {
		for(Block block : Block.blockList) {
			if(block != null) {
				block.registerAtlasSprites(blockSpriteManager);
			}
		}
	}
}
