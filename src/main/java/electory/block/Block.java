package electory.block;

import electory.client.TinyCraft;
import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;
import electory.entity.EntityPlayer;
import electory.event.RegisterBlocksEvent;
import electory.item.Item;
import electory.item.ItemBlock;
import electory.math.AABB;
import electory.utils.EnumSide;
import electory.utils.IRegistriable;
import electory.utils.NamedRegistry;
import electory.world.World;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Block implements IRegistriable {
	public static final NamedRegistry<Block> REGISTRY = new NamedRegistry<>();

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

	public Block() {
		
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

	public static void registerBlocks() {
		REGISTRY.register("grass", new BlockGrass());
		REGISTRY.register("log", new BlockLog());
		REGISTRY.register("leaves", new Block().setSpriteName("/img/blocks/leaves.png"));
		REGISTRY.register(	"water",
								new BlockWater().setSpriteName("/img/blocks/water.png")
										.setSolid(false)
										.setLiquid(true)
										.setImpassable(false));
		REGISTRY.register("tallgrass", new BlockTallGrass().setSpriteName("/img/blocks/tallgrass.png").setSolid(false).setImpassable(false));
		REGISTRY.register("sapling", new BlockSapling().setSpriteName("/img/blocks/sapling.png").setSolid(false).setImpassable(false));
		REGISTRY.register("sandstone", new Block().setSpriteName("/img/blocks/sandstone.png"));
		
		TinyCraft.getInstance().eventRegistry.emit(new RegisterBlocksEvent());
		
		for(Block block : REGISTRY.getAllBlocks()) {
			Item.REGISTRY.register(block.getRegistryName(), new ItemBlock(block));
		}
	}

	public boolean canBeReplaced() {
		return false;
	}
	
	private String registryName;

	@Override
	public void setRegistryName(String name) {
		this.registryName = name;
	}

	@Override
	public String getRegistryName() {
		return registryName;
	}

    public Class<?> getMetadataClass() {
		return null;
    }
}
