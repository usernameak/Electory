package tinycraft.block;

import tinycraft.client.render.IAtlasSprite;
import tinycraft.client.render.IAtlasSpriteManager;
import tinycraft.utils.EnumSide;

public class BlockGrass extends Block {

	public BlockGrass(int id) {
		super(id);
		setSpriteNumber(2);
	}

	protected IAtlasSprite sideSprite;
	protected IAtlasSprite bottomSprite;

	@Override
	public IAtlasSprite getAtlasSprite(EnumSide side) {
		switch (side) {
		case DOWN:
			return bottomSprite;
		case EAST:
		case NORTH:
		case SOUTH:
		case WEST:
			return sideSprite;
		case UP:
		default:
			return super.getAtlasSprite(side);
		}
	}

	@Override
	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		super.registerAtlasSprites(manager);
		sideSprite = manager.registerSpriteByID(5);
		bottomSprite = manager.registerSpriteByID(4);
	}
}
