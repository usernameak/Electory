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

public class BlockRendererCube implements IBlockRenderer {

	public int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cz) {
		int count = 0;
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = renderer.getChunk().getWorldBlockFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null
					|| (!adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))
							&& !(adjBlock == block))) {
				count += 2;
			}
		}
		return count;
	}

	public void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z, int cx, int cz,
			TriangleBuffer buffer) {
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = renderer.getChunk().getWorldBlockFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null
					|| (!adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))
							&& !(adjBlock == block))) {
				int skyLightLevel = renderer.getChunk()
						.getWorldSunLightLevelFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
				buffer.setColor(ChunkRenderer.lightColors[skyLightLevel]);
				bakeBlockSide(world, block, renderer, dir, cx, y, cz, x, z, buffer, skyLightLevel);
			}
		}
	}

	public void bakeBlockSide(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z, int wx, int wz,
			TriangleBuffer buffer, int lightLevel) {
		if (dir == EnumSide.UP) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.UP);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.DOWN);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.SOUTH);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.NORTH);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.EAST);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			IAtlasSprite sprite = block.getAtlasSprite(world, wx, y, wz, EnumSide.WEST);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		}
	}

	@Override
	public void renderBlockInGUI(Block block, GuiRenderState rs) {
		IAtlasSprite sprite = block.getAtlasSprite();
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.bindTexture(TextureManager.TERRAIN_TEXTURE);

		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.viewMatrix.translate(16, 16, 0);
		rs2.viewMatrix.scale(16.0f, 16.0f, 16.0f);
		/*
		 * rs2.modelViewMatrix.perspective(70.0f, 1.0f, 0.1f, 1000.f);
		 * rs2.modelViewMatrix.lookAt(5f, 5f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);
		 */

		// GL11.glDepthRange(0.1f, 1000.f);

		ShaderManager.defaultProgram.loadRenderState(rs2);
		TriangleBuffer buffer = Tessellator.instance.getBuffer();

		buffer.addQuadVertexWithUV(-1, 1, 0, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 0, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, -1, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(-1, -1, 0, sprite.getMinU(), sprite.getMinV());

		/*
		 * for(EnumSide dir : EnumSide.VALID_DIRECTIONS) { bakeBlockSide(block, dir,
		 * -0.5f, -0.5f, -0.5f, buffer); }
		 */

		Tessellator.instance.draw();
	}

}
