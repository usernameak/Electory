package tinycraft.block;

import tinycraft.client.render.IAtlasSprite;
import tinycraft.client.render.IAtlasSpriteManager;
import tinycraft.client.render.block.IBlockRenderer;
import tinycraft.client.render.world.WorldRenderer;
import tinycraft.math.AABB;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class Block {
	public static Block blockList[] = new Block[256];
	
	public static Block blockCobblestone;
	public static Block blockGrass;
	public static Block blockPlanks;
	public static Block blockDirt;
	public static Block blockRootStone;
	public static Block blockGlass;
	public static Block blockLog;
	public static Block blockLeaves;
	public static Block blockWater;
	public static Block blockSand;
	public static Block blockStone;
	
	public int blockID;
	
	private int spriteNumber = 0;
	private boolean breakable = true;
	private boolean solid = true;
	private boolean liquid = false;
	
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
		return solid;
	}
	
	public Block setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}
	
	public boolean isLiquid() {
		return liquid;
	}
	
	public Block setLiquid(boolean liquid) {
		this.liquid = liquid;
		return this;
	}

	public boolean isBreakable() {
		return breakable;
	}

	public Block setBreakable(boolean breakable) {
		this.breakable = breakable;
		return this;
	}

	public Block(int id) {
		blockList[id] = this;
		this.blockID = id;
	}
	
	public AABB getAABB(World world, int x, int y, int z, boolean isSimulating) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public boolean shouldRenderInPass(int pass) {
		return pass == WorldRenderer.RENDERPASS_BASE;
	}
	
	static {
		blockCobblestone = new Block(1).setSpriteNumber(1);
		blockGrass = new BlockGrass(2);
		blockPlanks = new Block(3).setSpriteNumber(3);
		blockDirt = new Block(4).setSpriteNumber(4);
		blockRootStone = new Block(5).setSpriteNumber(6).setBreakable(false);
		blockGlass = new Block(6).setSpriteNumber(7).setSolid(false);
		blockLog = new BlockLog(7);
		blockLeaves = new Block(8).setSpriteNumber(15);
		blockWater = new BlockWater(9).setSpriteNumber(16).setSolid(false).setLiquid(true);
		blockSand = new Block(10).setSpriteNumber(11);
		blockStone = new Block(11).setSpriteNumber(18);
	}
}
