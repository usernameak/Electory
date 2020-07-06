package electory.client.render.block;

import electory.block.Block;
import electory.client.gui.GuiRenderState;
import electory.client.render.IAtlasSprite;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.ShaderManager;
import electory.client.render.texture.TextureManager;
import electory.client.render.world.ChunkRenderer;
import electory.utils.EMath;
import electory.utils.EnumSide;
import electory.world.Chunk;
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
						.getWorldLightLevelFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, World.LIGHT_LEVEL_TYPE_SKY);
				int blockLightLevel = renderer.getChunk()
						.getWorldLightLevelFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, World.LIGHT_LEVEL_TYPE_BLOCK);
				buffer.setColor(EMath.addIntColors(ChunkRenderer.lightColors[skyLightLevel], ChunkRenderer.lightColorsBlock[blockLightLevel]));
				bakeBlockSide(	world,
								block,
								renderer,
								dir,
								cx,
								y,
								cz,
								x,
								z,
								buffer,
								skyLightLevel,
								blockLightLevel,
								block.getAtlasSprite(world, x, y, z, dir));
			}
		}
	}

	public void bakeBlockSide2(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z, int wx, int wz,
			TriangleBuffer buffer, int skyLightLevel, int blockLightLevel, IAtlasSprite sprite) {
		if (dir == EnumSide.UP) {
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		}
	}

	public void bakeBlockSide(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z,
			int wx, int wz, TriangleBuffer buffer, int skyLightLevel, int blockLightLevel, IAtlasSprite sprite) {
		bakeBlockSide2(world, block, renderer, dir, x, y, z, wx, wz, buffer, skyLightLevel, blockLightLevel, sprite);
	}

	@Override
	public void renderBlockInGUI(Block block, GuiRenderState rs) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.bindTexture(TextureManager.TERRAIN_TEXTURE);

		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.viewMatrix.translate(16, 16, 0);
		rs2.viewMatrix.scale(16.0f, 16.0f, 1.0f);
		rs2.viewMatrix.ortho(-0.8f, 0.8f, 0.8f, -0.8f, 0.01f, 20f);
		rs2.viewMatrix.lookAt(1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);

		ShaderManager.defaultProgram.loadRenderState(rs2);
		TriangleBuffer buffer = Tessellator.instance.getBuffer();

		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			bakeBlockSide2(null, block, null, dir, 0, 0, 0, 0, 0, buffer, 15, 0, block.getAtlasSprite(dir));
		}

		Tessellator.instance.draw();
	}

}
