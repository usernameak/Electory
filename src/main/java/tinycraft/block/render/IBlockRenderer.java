package tinycraft.block.render;

import tinycraft.render.ChunkRenderer;
import tinycraft.render.TriangleBuffer;
import tinycraft.world.World;

public interface IBlockRenderer {
	public static final BlockRendererCube cube = new BlockRendererCube();
	
	int getTriangleCount(World world, ChunkRenderer renderer, int x, int y, int z);
	void getTriangles(World world, ChunkRenderer renderer, int x, int y, int z, TriangleBuffer buffer);
}
