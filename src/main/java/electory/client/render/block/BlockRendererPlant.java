package electory.client.render.block;

import electory.block.Block;
import electory.client.gui.GuiRenderState;
import electory.client.render.IAtlasSprite;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.ShaderManager;
import electory.client.render.world.ChunkRenderer;
import electory.world.World;

public class BlockRendererPlant implements IBlockRenderer {

	public int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z) {
		return 8;
	}

	public void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z,
			TriangleBuffer buffer) {
		int skyLightLevel = renderer.getChunk().getWorldSunLightLevelFast(x, y, z);
		buffer.setColor(ChunkRenderer.lightColors[skyLightLevel]);
		IAtlasSprite sprite = block.getAtlasSprite();

		buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());

		buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());

		buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());

		buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
	}

	@Override
	public void renderBlockInGUI(Block block, GuiRenderState rs) {
		IAtlasSprite sprite = block.getAtlasSprite();
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.bindTexture("/terrain.png");

		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.viewMatrix.translate(16, 16, 0);
		rs2.viewMatrix.scale(16.0f, 16.0f, 16.0f);

		ShaderManager.defaultProgram.loadRenderState(rs2);
		TriangleBuffer buffer = Tessellator.instance.getBuffer();

		buffer.addQuadVertexWithUV(-1, 1, 0, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 0, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, -1, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(-1, -1, 0, sprite.getMinU(), sprite.getMinV());

		Tessellator.instance.draw();
	}

}
