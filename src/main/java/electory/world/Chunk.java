package electory.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import electory.block.Block;
import electory.client.render.world.ChunkRenderer;
import electory.nbt.ByteArrayTag;
import electory.nbt.CompoundTag;
import electory.nbt.ShortArrayTag;
import electory.nbt.Tag;
import electory.utils.EnumSide;
import electory.utils.MetaSerializer;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

public class Chunk {
	private short blockArray[] = new short[16 * 256 * 16];
	private short lightArray[] = new short[16 * 256 * 16];
	private Object metaArray[] = new Object[16 * 256 * 16];
	private byte biomeArray[] = new byte[16 * 16];
	private short heightMap[] = new short[16 * 16];
	
	// private SortedMap<Integer, ChunkPosition> scheduledBlockUpdates = new TreeMap<>(); // TODO: 

	public final ChunkRenderer chunkRenderer = new ChunkRenderer(this);

	public final World world;

	private int chunkX, chunkZ;

	public boolean isPopulated = false;

	public Chunk(World world, int chunkX, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public void tryPopulateWithNeighbours(IChunkProvider provider) {
		if (!isPopulated
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
		}
	}

	public void buildHeightMap() {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int y;
				for (y = 255; y >= 0; y--) {
					Block block = getBlockAt(x, y, z);
					if (block != null && block.isSolid()) {
						break;
					}
				}

				// System.out.println(y);
				heightMap[x * 16 + z] = (short) y;
			}
		}
	}

	public short getHeightAt(int x, int z) {
		if (x < 0 || z < 0 || x >= 16 || z >= 16) {
			return 0;
		}
		return heightMap[x * 16 + z];
	}

	public BiomeGenBase getBiomeAt(int x, int z) {
		if (x < 0 || z < 0 || x >= 16 || z >= 16) {
			return null;
		}
		return BiomeGenBase.biomeList[biomeArray[x * 16 + z]];
	}

	public void setBiomeAt(int x, int z, BiomeGenBase biome) {
		if (x < 0 || z < 0 || x >= 16 || z >= 16) {
			return;
		}
		biomeArray[x * 16 + z] = (byte) biome.biomeID;
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

	public int getSunLightLevelAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return 0xF;
		}

		return (byte) (lightArray[x + y * 16 + z * 16 * 256] & 0xF);
	}

	public int getWorldSunLightLevelFast(int x, int y, int z) {
		int cx = x - getChunkBlockCoordX();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || y < 0 || cz < 0 || cx >= 16 || y >= 256 || cz >= 16) {
			return world.getSunLightLevelAt(x, y, z);
		}
		return (byte) (lightArray[cx + y * 16 + cz * 16 * 256] & 0xF);
	}

	public void setSunLightLevelAt(int x, int y, int z, int val) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return;
		}

		lightArray[x + y * 16 + z * 16 * 256] = (short) ((lightArray[x + y * 16 + z * 16 * 256] & 0xFFF0) | val);
	}

	public void setWorldSunLightLevelFast(int x, int y, int z, int val) {
		int cx = x - getChunkBlockCoordX();
		int cz = z - getChunkBlockCoordZ();
		if (cx < 0 || y < 0 || cz < 0 || cx >= 16 || y >= 256 || cz >= 16) {
			world.setSunLightLevelAt(x, y, z, val);
			return;
		}
		lightArray[cx + y * 16 + cz * 16 * 256] = (short) ((lightArray[cx + y * 16 + cz * 16 * 256] & 0xFFF0) | val);
	}

	@SuppressWarnings("unchecked")
	public <T> T getBlockMetadataAt(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return null;
		}
		return (T) metaArray[x + y * 16 + z * 16 * 256];
	}

	public void setBlockMetadataAt(int x, int y, int z, Object meta) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return;
		}
		metaArray[x + y * 16 + z * 16 * 256] = meta;
	}

	public void setBlockWithMetadataAt(int x, int y, int z, Block block, Object meta, int flags) {
		if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
			return;
		}
		blockArray[x + y * 16 + z * 16 * 256] = (block == null ? 0 : (short) block.blockID);
		metaArray[x + y * 16 + z * 16 * 256] = meta;
		if ((flags & World.FLAG_SKIP_LIGHT_UPDATE) == 0) {
			recalculateSkyLightForBlock(x, y, z);
		}
		if ((flags & World.FLAG_SKIP_RENDER_UPDATE) == 0) {
			scheduleChunkUpdate();
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
	}

	public void setBlockAt(int x, int y, int z, Block block, int flags) {
		setBlockWithMetadataAt(x, y, z, block, null, flags);
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		setBlockAt(x, y, z, block, 0);
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

	private void recalculateSkyLight(Queue<WorldPosition> bfsSkyQueue) {
		while (!bfsSkyQueue.isEmpty()) {
			WorldPosition pos = bfsSkyQueue.poll();

			for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
				if (pos.y + side.offsetY < 0 || pos.y + side.offsetY >= 256) {
					continue;
				}

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
		}
	}

	public void recalculateSkyLight() {
		Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
		for (int x = getChunkBlockCoordX(); x < getChunkBlockCoordX() + 16; x++) {
			for (int z = getChunkBlockCoordZ(); z < getChunkBlockCoordZ() + 16; z++) {
				Block block = getWorldBlockFast(x, 255, z);
				if (block != null) {
					int opacity = block.getSkyLightOpacity();
					if (opacity == 15) {
						continue;
					}
					setWorldSunLightLevelFast(x, 255, z, 15 - opacity);
				} else {
					setWorldSunLightLevelFast(x, 255, z, 15);
				}
				bfsSkyQueue.add(new WorldPosition(x, 255, z));
			}
		}
		recalculateSkyLight(bfsSkyQueue);
	}

	public void recalculateSkyLightForBlock(int x, int y, int z) {
		Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
		int wx = x + getChunkBlockCoordX();
		int wz = z + getChunkBlockCoordZ();
		for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
			bfsSkyQueue.add(new WorldPosition(wx + side.offsetX, y + side.offsetY, wz + side.offsetZ));
		}
		recalculateSkyLight(bfsSkyQueue);
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
		for(Map.Entry<String, Tag<?>> entry : tag) {
			metaArray[Integer.parseInt(entry.getKey())] = MetaSerializer.deserializeObject((CompoundTag) entry.getValue());
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
