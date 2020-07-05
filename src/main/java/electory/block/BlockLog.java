package electory.block;

import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.entity.EntityPlayer;
import electory.utils.EnumAxis;
import electory.utils.EnumSide;
import electory.world.World;

public class BlockLog extends Block {

	public BlockLog() {
		super();
		setSpriteName("/img/blocks/log_side.png");
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
	public IAtlasSprite getAtlasSprite(World world, int x, int y, int z, EnumSide side) {
		EnumAxis meta = world.getBlockMetadataAt(x, y, z);

		if (meta == null) {
			meta = EnumAxis.Y;
		}

		if (side.axis == meta) {
			return topSprite;
		} else {
			return super.getAtlasSprite();
		}
	}

	@Override
	public Class<?> getMetadataClass() {
		return EnumAxis.class;
	}

	@Override
	public void blockPlacedByPlayer(EntityPlayer player, World world, int x, int y, int z, EnumSide side) {
		super.blockPlacedByPlayer(player, world, x, y, z, side);
		world.setBlockMetadataAt(x, y, z, side.axis);
	}

	@Override
	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		super.registerAtlasSprites(manager);
		topSprite = manager.registerSprite("/img/blocks/log_top.png");
	}
}
