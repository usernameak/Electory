package tinycraft.client.render.block;

import tinycraft.block.Block;
import tinycraft.client.gui.GuiRenderState;
import tinycraft.client.render.TriangleBuffer;
import tinycraft.client.render.world.ChunkRenderer;
import tinycraft.world.World;

public interface IBlockRenderer {
	public static final BlockRendererCube cube = new BlockRendererCube();
	
	int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z);
	void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z, TriangleBuffer buffer);
	
	void renderBlockInGUI(Block block, GuiRenderState rs);
}
