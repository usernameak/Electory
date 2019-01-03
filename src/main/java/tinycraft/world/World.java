package tinycraft.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import tinycraft.block.Block;
import tinycraft.entity.Entity;
import tinycraft.math.AABB;

public class World {
	private Chunk[][] chunks = new Chunk[16][16];
	private Set<Chunk> loadedChunks = new HashSet<>();
	private Set<Entity> entities = new HashSet<>();

	public Set<Entity> getEntities() {
		return entities;
	}

	public World() {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				chunks[x][z] = new Chunk(this, x, z);
				chunks[x][z].generate();
				loadedChunks.add(chunks[x][z]);
			}
		}
	}

	public Block getBlockAt(int x, int y, int z) {
		try {
			return chunks[x >> 4][z >> 4].getBlockAt(x & 0xF, y, z & 0xF);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		try {
			chunks[x >> 4][z >> 4].setBlockAt(x & 0xF, y, z & 0xF, block);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public Set<AABB> getBlockAABBsWithinAABB(AABB aabbIn) {
		int x1 = (int) aabbIn.x0;
		int y1 = (int) aabbIn.y0;
		int z1 = (int) aabbIn.z0;
		int x2 = (int) Math.ceil(aabbIn.x1);
		int y2 = (int) Math.ceil(aabbIn.y1);
		int z2 = (int) Math.ceil(aabbIn.z1);
		Set<AABB> aabbs = new HashSet<AABB>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					Block block = getBlockAt(x, y, z);
					if (block != null) {
						aabbs.add(block.getAABB(this, x, y, z, false));
					}
				}
			}
		}

		return aabbs;
	}

	public Set<Chunk> getAllLoadedChunks() {
		return Collections.unmodifiableSet(loadedChunks);
	}
}
