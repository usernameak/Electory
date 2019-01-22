package electory.client.render.block;

import electory.block.Block;
import electory.client.render.IAtlasSprite;
import electory.client.render.TriangleBuffer;
import electory.client.render.world.ChunkRenderer;
import electory.utils.EnumSide;
import electory.world.World;

public class BlockRenderAOCube extends BlockRendererCube {

	private int calculateAOLightLevel(int side1, int side2, int corner, int lightLevel) {
		if (side1 == 0 && side2 == 0) {
			return lightLevel / 4;
		}
		return (side1 + side2 + corner + lightLevel) / 4;
	}

	@Override
	public void bakeBlockSide(World world, Block block, ChunkRenderer renderer, EnumSide dir, int x, int y, int z,
			int wx, int wz, TriangleBuffer buffer, int lightLevel, IAtlasSprite sprite) {
		// AO coefficients: upper case is positive, lower is negative

		int sunLightAvg_xyz = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y - 1, wz - 1);
		int sunLightAvg_xyZ = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y - 1, wz + 1);
		int sunLightAvg_xYz = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y + 1, wz - 1);
		int sunLightAvg_xYZ = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y + 1, wz + 1);
		int sunLightAvg_Xyz = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y - 1, wz - 1);
		int sunLightAvg_XyZ = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y - 1, wz + 1);
		int sunLightAvg_XYz = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y + 1, wz - 1);
		int sunLightAvg_XYZ = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y + 1, wz + 1);
		int sunLightAvg_xy = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y - 1, wz);
		int sunLightAvg_xY = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y + 1, wz);
		int sunLightAvg_Xy = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y - 1, wz);
		int sunLightAvg_XY = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y + 1, wz);
		int sunLightAvg_yz = renderer.getChunk().getWorldSunLightLevelFast(wx, y - 1, wz - 1);
		int sunLightAvg_yZ = renderer.getChunk().getWorldSunLightLevelFast(wx, y - 1, wz + 1);
		int sunLightAvg_Yz = renderer.getChunk().getWorldSunLightLevelFast(wx, y + 1, wz - 1);
		int sunLightAvg_YZ = renderer.getChunk().getWorldSunLightLevelFast(wx, y + 1, wz + 1);
		int sunLightAvg_zx = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y, wz - 1);
		int sunLightAvg_zX = renderer.getChunk().getWorldSunLightLevelFast(wx - 1, y, wz + 1);
		int sunLightAvg_Zx = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y, wz - 1);
		int sunLightAvg_ZX = renderer.getChunk().getWorldSunLightLevelFast(wx + 1, y, wz + 1);

		buffer.setColor(ChunkRenderer.lightColors[lightLevel]);

		if (dir == EnumSide.UP) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_xY,
																			sunLightAvg_Yz,
																			sunLightAvg_xYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_xY,
																			sunLightAvg_YZ,
																			sunLightAvg_xYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_XY,
																			sunLightAvg_YZ,
																			sunLightAvg_XYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_XY,
																			sunLightAvg_Yz,
																			sunLightAvg_XYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.DOWN) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Xy,
																			sunLightAvg_yz,
																			sunLightAvg_Xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Xy,
																			sunLightAvg_yZ,
																			sunLightAvg_XyZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_xy,
																			sunLightAvg_yZ,
																			sunLightAvg_xyZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Xy,
																			sunLightAvg_yz,
																			sunLightAvg_Xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMinV());
		} else if (dir == EnumSide.SOUTH) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Zx,
																			sunLightAvg_yZ,
																			sunLightAvg_xyZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_ZX,
																			sunLightAvg_yZ,
																			sunLightAvg_XyZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_ZX,
																			sunLightAvg_YZ,
																			sunLightAvg_XYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Zx,
																			sunLightAvg_YZ,
																			sunLightAvg_xYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
		} else if (dir == EnumSide.NORTH) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zx,
																			sunLightAvg_Yz,
																			sunLightAvg_xYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zX,
																			sunLightAvg_Yz,
																			sunLightAvg_XYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zX,
																			sunLightAvg_yz,
																			sunLightAvg_Xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zx,
																			sunLightAvg_yz,
																			sunLightAvg_xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.EAST) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zX,
																			sunLightAvg_XY,
																			sunLightAvg_XYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_ZX,
																			sunLightAvg_XY,
																			sunLightAvg_XYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y + 1, z + 1, sprite.getMinU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_ZX,
																			sunLightAvg_Xy,
																			sunLightAvg_XYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z + 1, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zX,
																			sunLightAvg_Xy,
																			sunLightAvg_Xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x + 1, y, z, sprite.getMaxU(), sprite.getMaxV());
		} else if (dir == EnumSide.WEST) {
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zx,
																			sunLightAvg_xy,
																			sunLightAvg_xyz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z, sprite.getMinU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Zx,
																			sunLightAvg_xy,
																			sunLightAvg_xyZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y, z + 1, sprite.getMaxU(), sprite.getMaxV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_Zx,
																			sunLightAvg_xY,
																			sunLightAvg_xYZ,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z + 1, sprite.getMaxU(), sprite.getMinV());
			buffer.setColor(ChunkRenderer.lightColors[calculateAOLightLevel(sunLightAvg_zx,
																			sunLightAvg_xY,
																			sunLightAvg_xYz,
																			lightLevel)]);
			buffer.addQuadVertexWithUV(x, y + 1, z, sprite.getMinU(), sprite.getMinV());
		}
	}
}
