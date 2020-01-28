package electory.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import electory.block.Block;
import electory.client.render.world.ChunkRenderer;
import electory.nbt.ByteArrayTag;
import electory.nbt.CompoundTag;
import electory.nbt.ShortArrayTag;
import electory.nbt.Tag;
import electory.utils.EnumSide;
import electory.utils.MetaSerializer;
import electory.utils.MultiLock;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

public class Chunk {
	private short blockArray[] = new short[World.CHUNK_SIZE * World.CHUNK_SIZE * World.CHUNK_SIZE];
	private short lightArray[] = new short[World.CHUNK_SIZE * World.CHUNK_SIZE * World.CHUNK_SIZE];
	private Object metaArray[] = new Object[World.CHUNK_SIZE * World.CHUNK_SIZE * World.CHUNK_SIZE];
	private byte biomeArray[] = new byte[World.CHUNK_SIZE * World.CHUNK_SIZE];
	private short heightMap[] = new short[World.CHUNK_SIZE * World.CHUNK_SIZE];

	// private SortedMap<Integer, ChunkPosition> scheduledBlockUpdates = new
	// TreeMap<>(); // TODO:

	public final ChunkRenderer chunkRenderer = new ChunkRenderer(this);

	public final World world;

	private int chunkX, chunkY, chunkZ;

	public boolean isPopulated = false;

	public final ReadWriteLock renderLock = new ReentrantReadWriteLock();

	public Chunk(World world, int chunkX, int chunkY, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.chunkZ = chunkZ;
	}

	public void tryPopulateWithNeighbours(IChunkProvider provider) {
		// for()
		/*if (!isPopulated
				&& provider.isChunkLoaded(chunkX + 1, chunkZ + 1)
				&& provider.isChunkLoaded(chunkX, chunkZ + 1)
				&& provider.isChunkLoaded(chunkX + 1, chunkZ)) {
			provider.populate(null, chunkX, chunkZ);
		}

		if (provider.isChunkLoaded(chunkX - 1, chunkZ)
				&& !provider.provideChunk(chunkX - 1, chunkZ).isPopulated
				&& provider.isChunkLoaded(chunkX, chunkZ + 1)
				&& provider.isChunkLoaded(chunkX - 1, chunkZ + 1)) {
			provider.populate(null, chunkX - 1, chunkZ);
		}

		if (provider.isChunkLoaded(chunkX, chunkZ - 1)
				&& !provider.provideChunk(chunkX, chunkZ - 1).isPopulated
				&& provider.isChunkLoaded(chunkX + 1, chunkZ)
				&& provider.isChunkLoaded(chunkX + 1, chunkZ - 1)) {
			provider.populate(null, chunkX, chunkZ - 1);
		}

		if (provider.isChunkLoaded(chunkX - 1, chunkZ - 1)
				&& !provider.provideChunk(chunkX - 1, chunkZ - 1).isPopulated
				&& provider.isChunkLoaded(chunkX - 1, chunkZ)
				&& provider.isChunkLoaded(chunkX, chunkZ - 1)) {
			provider.populate(null, chunkX - 1, chunkZ - 1);
		}*/
	}

	public void buildHeightMap() {
		for (int x = 0; x < World.CHUNK_SIZE; x++) {
			for (int z = 0; z < World.CHUNK_SIZE; z++) {
				int y;
				for (y = 255; y >= 0; y--) {
					Block block = getBlockAt(x, y, z);
					if (block != null && block.isSolid()) {
						break;
					}
				}

				// System.out.println(y);
				heightMap[x * World.CHUNK_SIZE + z] = (short) y;
			}
		}
	}

	public short getHeightAt(int x, int z) {
		if (x < 0 || z < 0 || x >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return 0;
		}
		return heightMap[x * World.CHUNK_SIZE + z];
	}

	public BiomeGenBase getBiomeAt(int x, int z) {
		if (x < 0 || z < 0 || x >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return null;
		}
		return BiomeGenBase.biomeList[biomeArray[x * World.CHUNK_SIZE + z]];
	}

	public void setBiomeAt(int x, int z, BiomeGenBase biome) {
		if (x < 0 || z < 0 || x >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return;
		}

		Lock lock = renderLock.writeLock();
		lock.lock();
		try {
			biomeArray[x * World.CHUNK_SIZE + z] = (byte) biome.biomeID;
		} finally {
			lock.unlock();
		}
	}

	public Block getBlockAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return null;
		}
		return world.blockIdRegistry.getBlockById(blockArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE]);
	}

	public Block getWorldBlockFast(int x, int y, int z) {
		int cx = x - getChunkBlockCoordX();
		int cy = y - getChunkBlockCoordY();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || cy < 0 || cz < 0 || cx >= World.CHUNK_SIZE || cy >= World.CHUNK_SIZE || cz >= World.CHUNK_SIZE) {
			return world.getBlockAt(x, y, z);
		}
		return world.blockIdRegistry.getBlockById(blockArray[cx + cy * World.CHUNK_SIZE + cz * World.CHUNK_SIZE * World.CHUNK_SIZE]);
	}

	public int getSunLightLevelAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return 0xF;
		}

		return (byte) (lightArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE] & 0xF);
	}

	public int getWorldSunLightLevelFast(int x, int y, int z) {
		int cx = x - getChunkBlockCoordX();
		int cy = y - getChunkBlockCoordY();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || cy < 0 || cz < 0 || cx >= World.CHUNK_SIZE || cy >= World.CHUNK_SIZE || cz >= World.CHUNK_SIZE) {
			return world.getSunLightLevelAt(x, y, z);
		}
		return (byte) (lightArray[cx + cy * World.CHUNK_SIZE + cz * World.CHUNK_SIZE * World.CHUNK_SIZE] & 0xF);
	}

	public void setSunLightLevelAt(int x, int y, int z, int val) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return;
		}
		Lock lock = renderLock.writeLock();
		lock.lock();
		try {
			lightArray[x
					+ y * World.CHUNK_SIZE
					+ z
							* World.CHUNK_SIZE
							* World.CHUNK_SIZE] = (short) ((lightArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE] & 0xFFF0) | val);
		} finally {
			lock.unlock();
		}
	}

	public void setWorldSunLightLevelFast(int x, int y, int z, int val) {
		int cx = x - getChunkBlockCoordX();
		int cy = y - getChunkBlockCoordY();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || cy < 0 || cz < 0 || cx >= World.CHUNK_SIZE || cy >= World.CHUNK_SIZE || cz >= World.CHUNK_SIZE) {
			world.setSunLightLevelAt(x, y, z, val);
			return;
		}
		setSunLightLevelAt(cx, cy, cz, val);
	}

	@SuppressWarnings("unchecked")
	public <T> T getBlockMetadataAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return null;
		}
		return (T) metaArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE];
	}

	public void setBlockMetadataAt(int x, int y, int z, Object meta) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return;
		}
		Lock lock = renderLock.writeLock();
		lock.lock();
		try {
			metaArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE] = meta;
		} finally {
			lock.unlock();
		}
	}

	public void setBlockWithMetadataAt(int x, int y, int z, Block block, Object meta, int flags) {
		if (x < 0 || y < 0 || z < 0 || x >= World.CHUNK_SIZE || y >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return;
		}
		Lock lock = renderLock.writeLock();
		lock.lock();
		try {
			blockArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE] = (short) world.blockIdRegistry.getBlockId(block);
			metaArray[x + y * World.CHUNK_SIZE + z * World.CHUNK_SIZE * World.CHUNK_SIZE] = meta;
			if (block != null && block.isSolid()) {
				if (heightMap[x * World.CHUNK_SIZE + z] < y) {
					heightMap[x * World.CHUNK_SIZE + z] = (short) y;
				}
			} else {
				if (heightMap[x * World.CHUNK_SIZE + z] == y) {
					int h = y - 1;
					for (; h >= 0; h--) {
						Block b = getBlockAt(x, h, z);
						if (b != null) {
							break;
						}
					}
					if (h < 0) {
						h = 0;
					}
					heightMap[x * World.CHUNK_SIZE + z] = (short) h;
				}
			}
		} finally {
			lock.unlock();
		}
		if ((flags & World.FLAG_SKIP_LIGHT_UPDATE) == 0) {
			recalculateSkyLightForBlock(x, y, z);
		}
		if ((flags & World.FLAG_SKIP_RENDER_UPDATE) == 0) {
			scheduleChunkUpdate();
			if (x == 0) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX - 1, chunkY, chunkZ);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			} else if (x == World.CHUNK_SIZE - 1) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX + 1, chunkY, chunkZ);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			}
			if (y == 0) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY - 1, chunkZ);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			} else if (y == World.CHUNK_SIZE - 1) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY + 1, chunkZ);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			}
			if (z == 0) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ - 1);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			} else if (z == World.CHUNK_SIZE - 1) {
				Chunk nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ + 1);
				if (nearChunk != null) {
					nearChunk.scheduleChunkUpdate();
				}
			}
		}

	}

	public void setBlockAt(int x, int y, int z, Block block, int flags) {
		setBlockWithMetadataAt(x, y, z, block, null, flags);
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		setBlockAt(x, y, z, block, 0);
	}

	public void scheduleChunkUpdate() {
		getRenderer().markDirty();
	}

	public void unload() {
		getRenderer().destroy();
	}

	public ChunkRenderer getRenderer() {
		return chunkRenderer;
	}

	public void notifyNeighbourChunks() {
		Chunk nearChunk = world.getChunkFromChunkCoord(chunkX - 1, chunkY, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX + 1, chunkY, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY - 1, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		 nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY + 1, chunkZ);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ - 1);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ + 1);
		if (nearChunk != null) {
			nearChunk.scheduleChunkUpdate();
		}
	}
	/*
	public Lock getNeighbourWriteLock() {
		Set<Lock> locks = new HashSet<>();
		Chunk nearChunk = world.getChunkFromChunkCoord(chunkX - 1, chunkY, chunkZ);
		if (nearChunk != null) {
			locks.add(nearChunk.renderLock.writeLock());
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX + 1, chunkY, chunkZ);
		if (nearChunk != null) {
			locks.add(nearChunk.renderLock.writeLock());
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ - 1);
		if (nearChunk != null) {
			locks.add(nearChunk.renderLock.writeLock());
		}
		nearChunk = world.getChunkFromChunkCoord(chunkX, chunkY, chunkZ + 1);
		if (nearChunk != null) {
			locks.add(nearChunk.renderLock.writeLock());
		}
		return new MultiLock(locks);
	}
*/
	public int getChunkX() {
		return chunkX;
	}

	public int getChunkY() {
		return chunkY;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public int getChunkBlockCoordX() {
		return chunkX << World.CHUNK_BITSHIFT_SIZE;
	}

	public int getChunkBlockCoordY() {
		return chunkY << World.CHUNK_BITSHIFT_SIZE;
	}

	public int getChunkBlockCoordZ() {
		return chunkZ << World.CHUNK_BITSHIFT_SIZE;
	}

	private void recalculateSkyLight(Queue<WorldPosition> bfsSkyQueue) {
		/*while (!bfsSkyQueue.isEmpty()) {
			WorldPosition pos = bfsSkyQueue.poll();

			for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
				/*if (pos.y + side.offsetY < 0 || pos.y + side.offsetY >= World.CHUNK_SIZE) {
					continue;
				}*/
/*
				Block block = getWorldBlockFast(pos.x + side.offsetX, pos.y + side.offsetY, pos.z + side.offsetZ);
				int curLightLevel = getWorldSunLightLevelFast(pos.x, pos.y, pos.z);

				if ((block == null || block.getSkyLightOpacity() == 0)
						&& curLightLevel == 15
						&& side == EnumSide.DOWN) {
					setWorldSunLightLevelFast(pos.x + side.offsetX, pos.y + side.offsetY, pos.z + side.offsetZ, 15);
					bfsSkyQueue
							.add(new WorldPosition(pos.x + side.offsetX, pos.y + side.offsetY, pos.z + side.offsetZ));
				} else {
					int opacity = block == null ? 0 : block.getSkyLightOpacity();
					opacity = opacity <= 0 ? 1 : opacity;

					int oldLightLevel = getWorldSunLightLevelFast(	pos.x + side.offsetX,
																	pos.y + side.offsetY,
																	pos.z + side.offsetZ);

					if (oldLightLevel < curLightLevel - 1) {

						int newLightLevel = curLightLevel - opacity;
						newLightLevel = newLightLevel < 0 ? 0 : newLightLevel;
						setWorldSunLightLevelFast(	pos.x + side.offsetX,
													pos.y + side.offsetY,
													pos.z + side.offsetZ,
													newLightLevel);
						if (newLightLevel > 0) {
							bfsSkyQueue.add(new WorldPosition(pos.x + side.offsetX, pos.y + side.offsetY,
									pos.z + side.offsetZ));
						}
					}
				}
			}
		}*/
	}

	public void recalculateSkyLight() {
		/*Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
		int y = getChunkBlockCoordY() + World.CHUNK_SIZE - 1;
		for (int x = getChunkBlockCoordX(); x < getChunkBlockCoordX() + World.CHUNK_SIZE; x++) {
			for (int z = getChunkBlockCoordZ(); z < getChunkBlockCoordZ() + World.CHUNK_SIZE; z++) {
				Block block = getWorldBlockFast(x, y, z);
				if (block != null) {
					int opacity = block.getSkyLightOpacity();
					if (opacity == 15) {
						continue;
					}
					//setWorldSunLightLevelFast(x, y, z, 15 - opacity);
				} else {
					//setWorldSunLightLevelFast(x, y, z, 15);
				}
				bfsSkyQueue.add(new WorldPosition(x, y, z));
			}
		}
		recalculateSkyLight(bfsSkyQueue);*/
	}

	public void recalculateSkyLightForBlock(int x, int y, int z) {
		/*Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
		int wx = x + getChunkBlockCoordX();
		int wy = y + getChunkBlockCoordX();
		int wz = z + getChunkBlockCoordZ();
		for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
			bfsSkyQueue.add(new WorldPosition(wx + side.offsetX, wy + side.offsetY, wz + side.offsetZ));
		}
		recalculateSkyLight(bfsSkyQueue);*/
	}

	private CompoundTag writeMetaArray() {
		CompoundTag tag = new CompoundTag();
		for (int i = 0; i < metaArray.length; i++) {
			if (metaArray[i] != null) {
				tag.put(String.valueOf(i), MetaSerializer.serializeObject(metaArray[i]));
			}
		}
		return tag;
	}

	private void readMetaArray(CompoundTag tag) {
		Arrays.fill(metaArray, null);
		for (Map.Entry<String, Tag<?>> entry : tag) {
			metaArray[Integer.parseInt(entry.getKey())] = MetaSerializer
					.deserializeObject((CompoundTag) entry.getValue());
		}
	}

	public void writeChunkData(ArrayDataOutput dos) throws IOException {
		CompoundTag tag = new CompoundTag();
		tag.put("blockArray", new ShortArrayTag(blockArray));
		tag.put("lightArray", new ShortArrayTag(lightArray));
		tag.put("biomeArray", new ByteArrayTag(biomeArray));
		tag.put("heightMap", new ShortArrayTag(heightMap));
		tag.put("metaArray", writeMetaArray());
		tag.putBoolean("isPopulated", isPopulated);
		tag.serialize(dos, 0);
	}

	public void readChunkData(ArrayDataInput dis) throws IOException {
		CompoundTag tag = (CompoundTag) Tag.deserialize(dis, 0);
		blockArray = ((ShortArrayTag) tag.get("blockArray")).getValue();
		lightArray = ((ShortArrayTag) tag.get("lightArray")).getValue();
		biomeArray = ((ByteArrayTag) tag.get("biomeArray")).getValue();
		heightMap = ((ShortArrayTag) tag.get("heightMap")).getValue();
		readMetaArray(tag.getCompoundTag("metaArray"));
		isPopulated = tag.getBoolean("isPopulated");
		scheduleChunkUpdate();
	}
}
