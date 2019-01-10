package electory.block;

import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.utils.EnumSide;

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
