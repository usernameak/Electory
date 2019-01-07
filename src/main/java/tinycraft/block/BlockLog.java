package tinycraft.block;

import tinycraft.client.render.IAtlasSprite;
import tinycraft.client.render.IAtlasSpriteManager;
import tinycraft.utils.EnumSide;

public class BlockLog extends Block {

	public BlockLog(int id) {
		super(id);
		setSpriteNumber(12);
	}
	
	protected IAtlasSprite topSprite;

	@Override
	public IAtlasSprite getAtlasSprite(EnumSide side) {
		switch (side) {
		case DOWN:
		case UP:
			return topSprite;
		default:
			return super.getAtlasSprite(side);
		}
	}

	@Override
	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		super.registerAtlasSprites(manager);
		topSprite = manager.registerSpriteByID(14);
	}
}
