package electory.block;

import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.utils.EnumSide;

public class BlockGrass extends Block {

	public BlockGrass() {
		super();
		setSpriteName("/img/blocks/grass_top.png");
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
		sideSprite = manager.registerSprite("/img/blocks/grass_side.png");
		bottomSprite = manager.registerSprite("/img/blocks/dirt.png");
	}
}
