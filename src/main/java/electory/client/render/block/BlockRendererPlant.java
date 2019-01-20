package electory.client.render.block;

import electory.block.Block;
import electory.client.gui.GuiRenderState;
import electory.client.render.IAtlasSprite;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.ShaderManager;
import electory.client.render.texture.TextureManager;
import electory.client.render.world.ChunkRenderer;
import electory.utils.EnumSide;
import electory.world.World;

public class BlockRendererPlant implements IBlockRenderer {

	public int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cz) {
		return 8;
	}

	public void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cz,
			TriangleBuffer buffer) {
		int skyLightLevel = renderer.getChunk().getWorldSunLightLevelFast(x, y, z);
		buffer.setColor(ChunkRenderer.lightColors[skyLightLevel]);
		IAtlasSprite sprite = block.getAtlasSprite(world, x, y, z, EnumSide.SOUTH);

		buffer.addQuadVertexWithUV(cx, y, cz, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx + 1, y, cz + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx + 1, y + 1, cz + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx, y + 1, cz, sprite.getMinU(), sprite.getMinV());

		buffer.addQuadVertexWithUV(cx, y + 1, cz, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx + 1, y + 1, cz + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx + 1, y, cz + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx, y, cz, sprite.getMinU(), sprite.getMaxV());

		buffer.addQuadVertexWithUV(cx + 1, y + 1, cz, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx, y + 1, cz + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx, y, cz + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx + 1, y, cz, sprite.getMinU(), sprite.getMaxV());

		buffer.addQuadVertexWithUV(cx + 1, y, cz, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx, y, cz + 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(cx, y + 1, cz + 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(cx + 1, y + 1, cz, sprite.getMinU(), sprite.getMinV());
	}

	@Override
	public void renderBlockInGUI(Block block, GuiRenderState rs) {
		IAtlasSprite sprite = block.getAtlasSprite();
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.bindTexture(TextureManager.TERRAIN_TEXTURE);

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
