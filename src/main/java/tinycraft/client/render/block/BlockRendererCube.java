package tinycraft.client.render.block;

import tinycraft.block.Block;
import tinycraft.client.gui.GuiRenderState;
import tinycraft.client.render.IAtlasSprite;
import tinycraft.client.render.Tessellator;
import tinycraft.client.render.TriangleBuffer;
import tinycraft.client.render.shader.ShaderManager;
import tinycraft.client.render.world.ChunkRenderer;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class BlockRendererCube implements IBlockRenderer {

	public int getTriangleCount(World world, Block block, ChunkRenderer renderer, int x, int y, int z) {
		int count = 0;
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = renderer.getChunk().getWorldBlockFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null || (!adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))
					&& !(adjBlock == block))) {
				count += 2;
			}
		}
		return count;
	}

	public void getTriangles(World world, Block block, ChunkRenderer renderer, int x, int y, int z, TriangleBuffer buffer) {
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = renderer.getChunk().getWorldBlockFast(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null || (!adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))
					&& !(adjBlock == block))) {
				bakeBlockSide(block, dir, x, y, z, buffer);
			}
		}
	}

	public static void bakeBlockSide(Block block, EnumSide dir, float x, float y, float z, TriangleBuffer buffer) {
		if (dir == EnumSide.UP) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.UP);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.DOWN);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.SOUTH);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.NORTH);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.EAST);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			IAtlasSprite sprite = block.getAtlasSprite(EnumSide.WEST);
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
		ShaderManager.defaultProgram.bindTexture("/terrain.png");

		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.modelViewMatrix.translate(8, 8, 0);
		rs2.modelViewMatrix.scale(8.0f, 8.0f, 8.0f);
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
