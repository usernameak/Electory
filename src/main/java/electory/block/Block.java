package electory.block;

import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;
import electory.math.AABB;
import electory.utils.EnumSide;
import electory.world.World;

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
	public static Block blockGravel;
	public static Block blockTallGrass;

	public int blockID;

	private int spriteNumber = 0;
	private boolean breakable = true;
	private boolean solid = true;
	private boolean liquid = false;
	private boolean impassable = true;
	private BlockSound sound = SOUND_WOOD;

	protected IAtlasSprite blockSprite;
	
	public static final BlockSound SOUND_WOOD = new BlockSound("sfx/break/wood1.ogg");
	public static final BlockSound SOUND_SAND = new BlockSound("sfx/break/sand1.ogg");

	public Block setSpriteNumber(int spriteNumber) {
		this.spriteNumber = spriteNumber;
		return this;
	}

	public IBlockRenderer getRenderer() {
		return doesBlockAffectAO() ? IBlockRenderer.cubeAO : IBlockRenderer.cube;
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

	public boolean isImpassable() {
		return impassable;
	}

	public Block setImpassable(boolean impassable) {
		this.impassable = impassable;
		return this;
	}

	public Block(int id) {
		blockList[id] = this;
		this.blockID = id;
	}

	public AABB getAABB(World world, int x, int y, int z, boolean isSimulating) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}

	public boolean shouldRenderInVBO(int vbo) {
		return vbo == WorldRenderer.VBO_BASE;
	}

	public byte getSkyLightOpacity() {
		return 15;
	}
	
	public boolean doesBlockAffectAO() {
		return false;//isSolid();
	}

	public BlockSound getSound() {
		return sound;
	}

	public Block setSound(BlockSound sound) {
		this.sound = sound;
		return this;
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
		blockWater = new BlockWater(9).setSpriteNumber(16).setSolid(false).setLiquid(true).setImpassable(false);
		blockSand = new Block(10).setSpriteNumber(11).setSound(SOUND_SAND);
		blockStone = new Block(11).setSpriteNumber(18);
		blockGravel = new Block(12).setSpriteNumber(9);
		blockTallGrass = new BlockTallGrass(13).setSpriteNumber(20).setSolid(false).setImpassable(false);
	}

	public boolean canBeReplaced() {
		return false;
	}
}
