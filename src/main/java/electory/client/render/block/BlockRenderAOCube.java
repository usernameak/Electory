package electory.client.render.block;

import electory.block.Block;
import electory.client.render.IAtlasSprite;
import electory.client.render.TriangleBuffer;
import electory.client.render.world.ChunkRenderer;
import electory.utils.EnumSide;
import electory.world.Chunk;
import electory.world.World;

public class BlockRenderAOCube extends BlockRendererCube {

	private int calculateAOLightLevel(Chunk chunk, int x, int y, int z, int bx, int by, int bz, int vx, int vy, int vz/*int side1, int side2, int corner, int lightLevel*/) {
		int side1 = 0;
		int side2 = 0;

		if(vx == 0) {
			side1 = chunk.getWorldSunLightLevelFast(x + bx, y + by + vy, z + bz);
			side2 = chunk.getWorldSunLightLevelFast(x + bx, y + by, z + bz + vz);
		} else if(vy == 0) {
			side1 = chunk.getWorldSunLightLevelFast(x + bx + vx, y + by, z + bz);
			side2 = chunk.getWorldSunLightLevelFast(x + bx, y + by, z + bz + vz);
		} else if(vz == 0) {
			side1 = chunk.getWorldSunLightLevelFast(x + bx + vx, y + by, z + bz);
			side2 = chunk.getWorldSunLightLevelFast(x + bx, y + by + vy, z + bz);
		}

		int corner = chunk.getWorldSunLightLevelFast(x + bx + vx, y + by + vy, z + bz + vz);
		
		int lightLevel = chunk.getWorldSunLightLevelFast(x + bx, y + by, z + bz);

		if (side1 == 0 && side2 == 0) {
			return lightLevel / 4;
		}
		return (side1 + side2 + corner + lightLevel) / 4;
	}

	@Override
	public void bakeBlockSide(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z,
			int wx, int wz, TriangleBuffer buffer, int lightLevel, IAtlasSprite sprite) {
		// AO coefficients: upper case is positive, lower is negative

		//buffer.setColor(ChunkRenderer.lightColors[lightLevel]);

		if (dir == EnumSide.UP) {
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, +1, 0, -1, 0, -1);
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, +1, 0, -1, 0, +1);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, +1, 0, +1, 0, +1);
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, +1, 0, +1, 0, -1);
			if(ao00 + ao11 < ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, -1, 0, -1, 0, -1);
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, -1, 0, -1, 0, +1);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, -1, 0, +1, 0, +1);
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, -1, 0, +1, 0, -1);
			if(ao00 + ao11 > ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, +1, -1, -1, 0);
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, +1, +1, -1, 0);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, +1, +1, +1, 0);
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, +1, -1, +1, 0);
			if(ao00 + ao11 < ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, -1, -1, +1, 0);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, -1, +1, +1, 0);
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, -1, +1, -1, 0);
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, 0, 0, -1, -1, -1, 0);
			if(ao00 + ao11 > ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, +1, 0, 0, 0, +1, -1);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, +1, 0, 0, 0, +1, +1);
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, +1, 0, 0, 0, -1, +1);
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, +1, 0, 0, 0, -1, -1);
			if(ao00 + ao11 > ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			int ao00 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, -1, 0, 0, 0, -1, -1);
			int ao01 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, -1, 0, 0, 0, -1, +1);
			int ao11 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, -1, 0, 0, 0, +1, +1);
			int ao10 = calculateAOLightLevel(renderer.getChunk(), wx, y, wz, -1, 0, 0, 0, +1, -1);
			if(ao00 + ao11 < ao01 + ao10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(ChunkRenderer.lightColors[ao00]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao01]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[ao11]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[ao10]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		}
	}
}
