package electory.client.render;

import electory.block.Block;
import electory.client.render.texture.TextureManager;
import electory.client.stitcher.AtlasSpriteManagerImplStitch;
import electory.utils.GlobalUnitRegistry;

public class AtlasManager {
	public static IAtlasSpriteManager blockSpriteManager = new AtlasSpriteManagerImplStitch(TextureManager.TERRAIN_TEXTURE);
	
	public static void registerAllTerrainSprites() {
		for(Block block : GlobalUnitRegistry.getAllBlocks()) {
			if(block != null) {
				block.registerAtlasSprites(blockSpriteManager);
			}
		}
		blockSpriteManager.buildAtlas();
	}
}
