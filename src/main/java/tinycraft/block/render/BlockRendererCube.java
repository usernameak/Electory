package tinycraft.block.render;

import tinycraft.block.Block;
import tinycraft.render.ChunkRenderer;
import tinycraft.render.TriangleBuffer;
import tinycraft.utils.EnumSide;
import tinycraft.world.World;

public class BlockRendererCube implements IBlockRenderer {

	public int getTriangleCount(World world, ChunkRenderer renderer, int x, int y, int z) {
		int count = 0;
		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = world.getBlockAt(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null || !adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))) {
				count += 2;
			}
		}
		return count;
	}

	public void getTriangles(World world, ChunkRenderer renderer, int x, int y, int z, TriangleBuffer buffer) {

		for (EnumSide dir : EnumSide.VALID_DIRECTIONS) {
			Block adjBlock = world.getBlockAt(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (adjBlock == null || !adjBlock.isSolidOnSide(EnumSide.getOrientation(EnumSide.OPPOSITES[dir.ordinal()]))) {
				if(dir == EnumSide.UP) {
					buffer.addQuadVertexWithUV(x, y + 1, z, 0, 1);
					buffer.addQuadVertexWithUV(x, y + 1, z + 1, 0, 0);
					buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, 1, 0);
					buffer.addQuadVertexWithUV(x + 1, y + 1, z, 1, 1);
				} else if(dir == EnumSide.DOWN) {
					buffer.addQuadVertexWithUV(x + 1, y, z, 1, 1);
					buffer.addQuadVertexWithUV(x + 1, y, z + 1, 1, 0);
					buffer.addQuadVertexWithUV(x, y, z + 1, 0, 0);
					buffer.addQuadVertexWithUV(x, y, z, 0, 1);
				} else if(dir == EnumSide.SOUTH) {
					buffer.addQuadVertexWithUV(x, y, z + 1, 0, 1);
					buffer.addQuadVertexWithUV(x + 1, y, z + 1, 0, 0);
					buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, 1, 0);
					buffer.addQuadVertexWithUV(x, y + 1, z + 1, 1, 1);
				} else if(dir == EnumSide.NORTH) {
					buffer.addQuadVertexWithUV(x, y + 1, z, 1, 1);
					buffer.addQuadVertexWithUV(x + 1, y + 1, z, 1, 0);
					buffer.addQuadVertexWithUV(x + 1, y, z, 0, 0);
					buffer.addQuadVertexWithUV(x, y, z, 0, 1);
				} else if(dir == EnumSide.EAST) {
					buffer.addQuadVertexWithUV(x + 1, y + 1, z, 1, 1);
					buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, 1, 0);
					buffer.addQuadVertexWithUV(x + 1, y, z + 1, 0, 0);
					buffer.addQuadVertexWithUV(x + 1, y, z, 0, 1);
				} else if(dir == EnumSide.WEST) {
					buffer.addQuadVertexWithUV(x, y, z, 0, 1);
					buffer.addQuadVertexWithUV(x, y, z + 1, 0, 0);
					buffer.addQuadVertexWithUV(x, y + 1, z + 1, 1, 0);
					buffer.addQuadVertexWithUV(x, y + 1, z, 1, 1);
				}
			}
		}
	}

}
