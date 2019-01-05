package tinycraft.block.render;

import tinycraft.TinyCraft;
import tinycraft.block.Block;
import tinycraft.render.ChunkRenderer;
import tinycraft.render.IAtlasSprite;
import tinycraft.render.Tessellator;
import tinycraft.render.TriangleBuffer;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class BlockRendererCube implements IBlockRenderer {

	public int getTriangleCount(World world, ChunkRenderer renderer, int x, int y, int z) {
		int count = 0;
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = world.getBlockAt(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null
					|| !adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))) {
				count += 2;
			}
		}
		return count;
	}

	public void getTriangles(World world, ChunkRenderer renderer, int x, int y, int z, TriangleBuffer buffer) {
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = world.getBlockAt(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			Block block = world.getBlockAt(x, y, z);
			if (adjBlock == null
					|| !adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))) {
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
		}
	}

	@Override
	public void renderBlockInGUI(Block block) {
		IAtlasSprite sprite = block.getAtlasSprite();
		TinyCraft.getInstance().textureManager.bindTexture("/terrain.png");
		TriangleBuffer buffer = Tessellator.instance.getBuffer();

		buffer.addQuadVertexWithUV(0, 16, 0, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(16, 16, 0, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(16, 0, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 0, 0, sprite.getMinU(), sprite.getMinV());

		Tessellator.instance.draw();
	}

}
