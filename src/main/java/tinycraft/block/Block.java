package tinycraft.block;

import tinycraft.block.render.IBlockRenderer;
import tinycraft.math.AABB;
import tinycraft.render.IAtlasSprite;
import tinycraft.render.IAtlasSpriteManager;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class Block {
	public static Block blockList[] = new Block[256];
	
	public static Block blockStone;
	public static Block blockGrass;
	
	public int blockID;
	
	private int spriteNumber = 0;
	
	protected IAtlasSprite blockSprite;
	
	public Block setSpriteNumber(int spriteNumber) {
		this.spriteNumber = spriteNumber;
		return this;
	}

	public IBlockRenderer getRenderer() {
		return IBlockRenderer.cube;
	}
	
	public IAtlasSprite getAtlasSprite() {
		return blockSprite;
	}
	
	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		blockSprite = manager.registerSpriteByID(spriteNumber);
	}
	
	public boolean isSolidOnSide(EnumSide side) {
		return isSolid();
	}
	
	public boolean isSolid() {
		return true;
	}

	public Block(int id) {
		blockList[id] = this;
		this.blockID = id;
	}
	
	public AABB getAABB(World world, int x, int y, int z, boolean isSimulating) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	static {
		blockStone = new Block(1).setSpriteNumber(1);
		blockGrass = new Block(2).setSpriteNumber(2);
	}
}
