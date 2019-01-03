package tinycraft.block;

import tinycraft.block.render.IBlockRenderer;
import tinycraft.math.AABB;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class Block {
	public static Block blockList[] = new Block[256];
	
	public static Block blockStone;
	
	public int blockID;
	
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.cube;
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
		blockStone = new Block(1);
	}
}
