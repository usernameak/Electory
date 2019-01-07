package tinycraft.world;

import java.io.IOException;

import tinycraft.block.Block;
import tinycraft.client.render.world.ChunkRenderer;
import tinycraft.utils.io.ArrayDataInput;
import tinycraft.utils.io.ArrayDataOutput;

public class Chunk {
	private short blockArray[] = new short[16 * 256 * 16];

	public final ChunkRenderer chunkRenderer = new ChunkRenderer(this);

	public final World world;

	private int chunkX, chunkZ;

	public Chunk(World world, int chunkX, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public Block getBlockAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return null;
		}
		return Block.blockList[blockArray[x + y * 16 + z * 16 * 256]];
	}
	
	public Block getWorldBlockFast(int x, int y, int z) {
		int cx = x - getChunkBlockCoordX();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || y < 0 || cz < 0 || cx >= 16 || y >= 256 || cz >= 16) {
			return world.getBlockAt(x, y, z);
		}
		return Block.blockList[blockArray[cx + y * 16 + cz * 16 * 256]];
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return;
		}
		scheduleChunkUpdate();
		blockArray[x + y * 16 + z * 16 * 256] = (block == null ? 0 : (short) block.blockID);
		if (x == 0) {
			Chunk nearChunk = world.getChunkFromChunkCoord(chunkX - 1, chunkZ);
			if (nearChunk != null) {
				nearChunk.scheduleChunkUpdate();
			}
		} else if (x == 15) {
			Chunk nearChunk = world.getChunkFromChunkCoord(chunkX + 1, chunkZ);
			if (nearChunk != null) {
				nearChunk.scheduleChunkUpdate();
			}
		}
		if (z == 0) {
			Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkZ - 1);
			if (nearChunk != null) {
				nearChunk.scheduleChunkUpdate();
			}
		} else if (z == 15) {
			Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkZ + 1);
			if (nearChunk != null) {
				nearChunk.scheduleChunkUpdate();
			}
		}
	}

	public void scheduleChunkUpdate() {
		getRenderer().needsUpdate = true;
	}
	
	public void unload() {
		getRenderer().destroy();
	}

	public ChunkRenderer getRenderer() {
		return chunkRenderer;
	}
	
	public void notifyNeighbourChunks() {
		Chunk nearChunk = world.getChunkFromChunkCoord(chunkX - 1, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX + 1, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkZ - 1);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkZ + 1);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public int getChunkBlockCoordX() {
		return chunkX << 4;
	}

	public int getChunkBlockCoordZ() {
		return chunkZ << 4;
	}

	public void generate() {
		DSNoise noise = new DSNoise(60000000, 60000000 / 100.0f, world.seed);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int h = (int) Math.round(noise.val(x + getChunkBlockCoordX() + 30000000, z + getChunkBlockCoordZ() + 30000000)
						* 48.0f + 48.0f);
				setBlockAt(x, 0, z, Block.blockRootStone);
				for (int y = 1; y < h - 5; y++) {
					setBlockAt(x, y, z, Block.blockStone);
				}
				if (h >= 64) {
					for (int y = h - 5; y < h; y++) {
						setBlockAt(x, y, z, Block.blockDirt);
					}
					setBlockAt(x, h, z, Block.blockGrass);
				} else {
					for (int y = h - 5; y <= h; y++) {
						setBlockAt(x, y, z, Block.blockSand);
					}
					for (int y = h + 1; y < 64; y++) {
						setBlockAt(x, y, z, Block.blockWater);
					}
				}
			}
		}
	}

	public void writeChunkData(ArrayDataOutput dos) throws IOException {
		dos.write(blockArray);
	}

	public void readChunkData(ArrayDataInput dis) throws IOException {
		dis.read(blockArray);
		scheduleChunkUpdate();
	}
}
