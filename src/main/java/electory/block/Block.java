package electory.block;

import electory.client.TinyCraft;
import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;
import electory.entity.EntityPlayer;
import electory.event.RegisterBlocksEvent;
import electory.math.AABB;
import electory.utils.EnumSide;
import electory.utils.GlobalUnitRegistry;
import electory.utils.IUnit;
import electory.utils.Unit;
import electory.world.World;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Block implements IUnit {
	@Setter
	@Accessors(chain = true)
	private String spriteName;
	
	public int blockID;
	public int blockSubID;
	public String anyName = "block.null.name";
	public boolean breakable = true;
	public boolean solid = true;
	public boolean liquid = false;
	public boolean impassable = true;
	public BlockSound sound = SOUND_WOOD;

	protected IAtlasSprite blockSprite;

	public static final BlockSound SOUND_WOOD = new BlockSound("sfx/break/wood1.ogg");
	public static final BlockSound SOUND_SAND = new BlockSound("sfx/break/sand1.ogg");
	public static final Block grassBlock = new BlockGrass(1);
	public static final Block blockLog = new BlockLog(2);
	public static final Block blockLeaves = new Block(3).setSpriteName("/img/blocks/leaves.png");
	public static final Block blockWater = new BlockWater(4).setSpriteName("/img/blocks/water.png").setSolid(false).setLiquid(true).setImpassable(false);
	public static final Block blockTallGrass = new BlockTallGrass(5).setSpriteName("/img/blocks/tallgrass.png").setSolid(false).setImpassable(false);
	public static final Block blockSapling = new BlockSapling(6).setSpriteName("/img/blocks/sapling.png").setSolid(false).setImpassable(false);
	public static final Block blockSandstone = new Block(7).setSpriteName("/img/blocks/sandstone.png");
	
	public Block(int par1) {
		GlobalUnitRegistry.registerUnit(new Unit(this.blockID = par1, this.blockSubID = 0), this);
	}
	
	public Block(int par1, int par2) {
		GlobalUnitRegistry.registerUnit(new Unit(this.blockID = par1, this.blockSubID = par2), this);
	}
	
	public static void registerBlocks() {
		TinyCraft.getInstance().eventRegistry.emit(new RegisterBlocksEvent());
	}
	
	public IBlockRenderer getRenderer() {
		return doesBlockAffectAO() ? IBlockRenderer.cubeAO : IBlockRenderer.cube;
	}

	public IAtlasSprite getAtlasSprite() {
		return this.blockSprite;
	}

	public IAtlasSprite getAtlasSprite(EnumSide side) {
		return getAtlasSprite();
	}

	public IAtlasSprite getAtlasSprite(World world, int x, int y, int z, EnumSide side) {
		return getAtlasSprite(side);
	}

	public void registerAtlasSprites(IAtlasSpriteManager manager) {
		this.blockSprite = manager.registerSprite(spriteName);
	}

	public boolean isSolidOnSide(EnumSide side) {
		return this.isSolid();
	}

	public Block setAnyName(String anyName) {
		this.anyName = anyName;
		return this;
	}
	
	public boolean isSolid() {
		return this.solid;
	}

	public Block setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}

	public boolean isLiquid() {
		return this.liquid;
	}

	public Block setLiquid(boolean liquid) {
		this.liquid = liquid;
		return this;
	}

	public boolean isBreakable() {
		return this.breakable;
	}

	public Block setBreakable(boolean breakable) {
		this.breakable = breakable;
		return this;
	}

	public boolean isImpassable() {
		return this.impassable;
	}

	public Block setImpassable(boolean impassable) {
		this.impassable = impassable;
		return this;
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
		return this.isSolid();
	}

	public BlockSound getSound() {
		return this.sound;
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

	public boolean canBeReplaced() {
		return false;
	}
}
