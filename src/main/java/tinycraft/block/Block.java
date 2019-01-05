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
	public static Block blockPlanks;
	public static Block blockDirt;
	
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
	
	public IAtlasSprite getAtlasSprite(EnumSide side) {
		return getAtlasSprite();
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
		blockGrass = new BlockGrass(2);
		blockPlanks = new Block(3).setSpriteNumber(3);
		blockDirt = new Block(4).setSpriteNumber(4);
	}
}
