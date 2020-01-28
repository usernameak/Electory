package electory.client.render.block;

import electory.block.Block;
import electory.client.gui.GuiRenderState;
import electory.client.render.TriangleBuffer;
import electory.client.render.world.ChunkRenderer;
import electory.world.World;

public interface IBlockRenderer {
	public static final BlockRendererCube cube = new BlockRendererCube();
	public static final BlockRendererCube cubeAO = new BlockRenderAOCube();
	public static final BlockRendererPlant plant = new BlockRendererPlant();
	
	int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cy, int cz);
	void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cy, int cz, TriangleBuffer buffer);
	
	void renderBlockInGUI(Block block, GuiRenderState rs);
}
