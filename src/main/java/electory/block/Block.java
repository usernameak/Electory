package electory.block;

import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;
import electory.entity.EntityPlayer;
import electory.item.ItemBlock;
import electory.math.AABB;
import electory.utils.EnumSide;
import electory.world.World;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Block {
	public static Block blockList[] = new Block[32768];

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
	public static Block blockSapling;
	public static Block blockSandstone;

	public int blockID;

	@Setter
	@Accessors(chain = true)
	private String spriteName;

	private boolean breakable = true;
	private boolean solid = true;
	private boolean liquid = false;
	private boolean impassable = true;
	private BlockSound sound = SOUND_WOOD;

	protected IAtlasSprite blockSprite;

	public static final BlockSound SOUND_WOOD = new BlockSound("sfx/break/wood1.ogg");
	public static final BlockSound SOUND_SAND = new BlockSound("sfx/break/sand1.ogg");

	public IBlockRenderer getRenderer() {
		return doesBlockAffectAO() ? IBlockRenderer.cubeAO : IBlockRenderer.cube;
	}

	public IAtlasSprite getAtlasSprite() {
		return blockSprite;
	}

	public IAtlasSprite getAtlasSprite(EnumSide side) {
		return getAtlasSprite();
	}

	public IAtlasSprite getAtlasSprite(World world, int x, int y, int z, EnumSide side) {
		return getAtlasSprite(side);
	}

	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		blockSprite = manager.registerSprite(spriteName);
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
		new ItemBlock(id);
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
		return isSolid();
	}

	public BlockSound getSound() {
		return sound;
	}

	public Block setSound(BlockSound sound) {
		this.sound = sound;
		return this;
	}

	public boolean interactWithBlock(EntityPlayer player, World world, int x, int y, int z, EnumSide side) {
		return false;
	}

	public void blockPlacedByPlayer(EntityPlayer player, World world, int x, int y, int z, EnumSide side) {

	}

	static {
		blockCobblestone = new Block(1).setSpriteName("/img/blocks/cobblestone.png");
		blockGrass = new BlockGrass(2);
		blockPlanks = new Block(3).setSpriteName("/img/blocks/planks.png");
		blockDirt = new Block(4).setSpriteName("/img/blocks/dirt.png");
		blockRootStone = new Block(5).setSpriteName("/img/blocks/rootstone.png").setBreakable(false);
		blockGlass = new Block(6).setSpriteName("/img/blocks/glass.png").setSolid(false);
		blockLog = new BlockLog(7);
		blockLeaves = new Block(8).setSpriteName("/img/blocks/leaves.png");
		blockWater = new BlockWater(9).setSpriteName("/img/blocks/water.png").setSolid(false).setLiquid(true).setImpassable(false);
		blockSand = new Block(10).setSpriteName("/img/blocks/sand.png").setSound(SOUND_SAND);
		blockStone = new Block(11).setSpriteName("/img/blocks/stone.png");
		blockGravel = new Block(12).setSpriteName("/img/blocks/gravel.png");
		blockTallGrass = new BlockTallGrass(13).setSpriteName("/img/blocks/tallgrass.png").setSolid(false).setImpassable(false);
		blockSapling = new BlockSapling(14).setSpriteName("/img/blocks/sapling.png").setSolid(false).setImpassable(false);
		blockSandstone = new Block(15).setSpriteName("/img/blocks/sandstone.png");
	}

	public boolean canBeReplaced() {
		return false;
	}
}
