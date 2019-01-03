package tinycraft.world;

import tinycraft.block.Block;
import tinycraft.render.ChunkRenderer;

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
		if(x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return null;
		}
		return Block.blockList[blockArray[x + y * 16 + z * 16 * 256]];
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		if(x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return;
		}
		getRenderer().needsUpdate = true;
		blockArray[x + y * 16 + z * 16 * 256] = (block == null ? 0 : (short) block.blockID);
	}
	
	public ChunkRenderer getRenderer() {
		return chunkRenderer; 
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
		DSNoise noise = new DSNoise(60000000, 60000000 / 100.0f, 0L);
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				for(int y = 0; y < noise.val(x + getChunkBlockCoordX() + 30000000, z + getChunkBlockCoordZ() + 30000000) * 8.0f + 63.0f; y++) {
					setBlockAt(x, y, z, Block.blockStone);
				}
			}
		}
	}
}
