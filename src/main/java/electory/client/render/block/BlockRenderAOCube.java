package electory.client.render.block;

import electory.block.Block;
import electory.client.render.IAtlasSprite;
import electory.client.render.TriangleBuffer;
import electory.client.render.world.ChunkRenderer;
import electory.utils.EMath;
import electory.utils.EnumSide;
import electory.world.Chunk;
import electory.world.World;

import static electory.utils.EMath.addIntColors;

public class BlockRenderAOCube extends BlockRendererCube {

	private int calculateAOLightLevel(Chunk chunk, int lightLevelType, int x, int y, int z, int bx, int by, int bz, int vx, int vy, int vz/*int side1, int side2, int corner, int lightLevel*/) {
		int side1 = 0;
		int side2 = 0;

		if(vx == 0) {
			side1 = chunk.getWorldLightLevelFast(x + bx, y + by + vy, z + bz, lightLevelType);
			side2 = chunk.getWorldLightLevelFast(x + bx, y + by, z + bz + vz, lightLevelType);
		} else if(vy == 0) {
			side1 = chunk.getWorldLightLevelFast(x + bx + vx, y + by, z + bz, lightLevelType);
			side2 = chunk.getWorldLightLevelFast(x + bx, y + by, z + bz + vz, lightLevelType);
		} else if(vz == 0) {
			side1 = chunk.getWorldLightLevelFast(x + bx + vx, y + by, z + bz, lightLevelType);
			side2 = chunk.getWorldLightLevelFast(x + bx, y + by + vy, z + bz, lightLevelType);
		}

		int corner = chunk.getWorldLightLevelFast(x + bx + vx, y + by + vy, z + bz + vz, lightLevelType);
		
		int lightLevel = chunk.getWorldLightLevelFast(x + bx, y + by, z + bz, lightLevelType);

		if (side1 == 0 && side2 == 0) {
			return lightLevel / 4;
		}
		return (side1 + side2 + corner + lightLevel) / 4;
	}

	@Override
	public void bakeBlockSide(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z,
			int wx, int wz, TriangleBuffer buffer, int lightLevel, int blockLightLevel, IAtlasSprite sprite) {
		// AO coefficients: upper case is positive, lower is negative

		//buffer.setColor(ChunkRenderer.lightColors[lightLevel]);

		if (dir == EnumSide.UP) {
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, +1, 0, -1, 0, -1);
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, +1, 0, -1, 0, +1);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, +1, 0, +1, 0, +1);
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, +1, 0, +1, 0, -1);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, +1, 0, -1, 0, -1);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, +1, 0, -1, 0, +1);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, +1, 0, +1, 0, +1);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, +1, 0, +1, 0, -1);
			if(aos00 + aos11 < aos01 + aos10 || aob00 + aob11 < aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, -1, 0, -1, 0, -1);
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, -1, 0, -1, 0, +1);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, -1, 0, +1, 0, +1);
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, -1, 0, +1, 0, -1);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, -1, 0, -1, 0, -1);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, -1, 0, -1, 0, +1);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, -1, 0, +1, 0, +1);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, -1, 0, +1, 0, -1);
			if(aos00 + aos11 > aos01 + aos10 || aob00 + aob11 > aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, +1, -1, -1, 0);
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, +1, +1, -1, 0);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, +1, +1, +1, 0);
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, +1, -1, +1, 0);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, +1, -1, -1, 0);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, +1, +1, -1, 0);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, +1, +1, +1, 0);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, +1, -1, +1, 0);
			if(aos00 + aos11 < aos01 + aos10 || aob00 + aob11 < aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, -1, -1, +1, 0);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, -1, +1, +1, 0);
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, -1, +1, -1, 0);
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, 0, 0, -1, -1, -1, 0);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, -1, -1, +1, 0);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, -1, +1, +1, 0);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, -1, +1, -1, 0);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, 0, 0, -1, -1, -1, 0);
			if(aos00 + aos11 > aos01 + aos10 || aob00 + aob11 > aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, +1, 0, 0, 0, +1, -1);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, +1, 0, 0, 0, +1, +1);
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, +1, 0, 0, 0, -1, +1);
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, +1, 0, 0, 0, -1, -1);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, +1, 0, 0, 0, +1, -1);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, +1, 0, 0, 0, +1, +1);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, +1, 0, 0, 0, -1, +1);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, +1, 0, 0, 0, -1, -1);
			if(aos00 + aos11 > aos01 + aos10 || aob00 + aob11 > aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			int aos00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, -1, 0, 0, 0, -1, -1);
			int aos01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, -1, 0, 0, 0, -1, +1);
			int aos11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, -1, 0, 0, 0, +1, +1);
			int aos10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_SKY, wx, y, wz, -1, 0, 0, 0, +1, -1);
			int aob00 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, -1, 0, 0, 0, -1, -1);
			int aob01 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, -1, 0, 0, 0, -1, +1);
			int aob11 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, -1, 0, 0, 0, +1, +1);
			int aob10 = calculateAOLightLevel(renderer.getChunk(), World.LIGHT_LEVEL_TYPE_BLOCK, wx, y, wz, -1, 0, 0, 0, +1, -1);
			if(aos00 + aos11 < aos01 + aos10 || aob00 + aob11 < aob01 + aob10) {
				buffer.setNextQuadInvOrder(true);
			}
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos00], ChunkRenderer.lightColorsBlock[aob00]));
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos01], ChunkRenderer.lightColorsBlock[aob01]));
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos11], ChunkRenderer.lightColorsBlock[aob11]));
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(addIntColors(ChunkRenderer.lightColors[aos10], ChunkRenderer.lightColorsBlock[aob10]));
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		}
	}
}
