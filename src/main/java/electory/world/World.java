package electory.world;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.joml.Vector3f;

import electory.block.Block;
import electory.client.TinyCraft;
import electory.entity.Entity;
import electory.entity.EntityMap;
import electory.entity.EntityPlayer;
import electory.entity.particle.EntityBlockParticle;
import electory.math.AABB;
import electory.nbt.CompoundTag;
import electory.nbt.ListTag;
import electory.nbt.NBTUtil;
import electory.utils.CrashException;
import electory.utils.EnumSide;
import electory.world.gen.ChunkGenerator;

public class World implements IChunkSaveStatusHandler {
	private Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();
	private Set<Entity> entities = new HashSet<>();

	public long seed = ThreadLocalRandom.current().nextLong();

	public Random random = new Random(seed);

	private Vector3f spawnPoint = new Vector3f(/* random.nextInt(2048) - 1024 */ 0f, 256f,
			/* random.nextInt(2048) - 1024 */ 0f);

	public static final int FLAG_SKIP_RENDER_UPDATE = 1;
	public static final int FLAG_SKIP_LIGHT_UPDATE = 2;

	public IChunkProvider generationChunkProvider = new ChunkGenerator(this, seed);
	public IChunkProvider chunkProvider = new ChunkProviderSP(this);

	private EntityPlayer playerToSpawn = null;

	public Set<Entity> getEntities() {
		return entities;
	}

	public World() {
	}

	public Block getBlockAt(int x, int y, int z) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		return chunk == null ? null : chunk.getBlockAt(x & 0xF, y, z & 0xF);
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		if (chunk != null) {
			chunk.setBlockAt(x & 0xF, y, z & 0xF, block);
		}
	}

	public Chunk getChunkFromChunkCoord(int x, int z) {
		return loadedChunks.get(new ChunkPosition(x, z));
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void breakBlockWithParticles(int x, int y, int z) {
		Block oldBlock = getBlockAt(x, y, z);
		setBlockAt(x, y, z, null);
		if (oldBlock != null) {
			for (int i = 0; i < 8; i++) {
				EntityBlockParticle particle = new EntityBlockParticle(this,
						oldBlock.getAtlasSprite(EnumSide.getOrientation(random.nextInt(6))));
				particle.setPosition(x + 0.5f, y + 0.5f, z + 0.5f, false);
				particle.setVelocity(	random.nextFloat() * 0.1f - 0.05f,
										random.nextFloat() * 0.1f - 0.05f,
										random.nextFloat() * 0.1f - 0.05f);
				addEntity(particle);
			}
		}
	}

	public void interactWithBlock(EntityPlayer player, int x, int y, int z, EnumSide side) {
		AABB blockAABB = player.selectedBlock.getAABB(this, x + side.offsetX, y + side.offsetY, z + side.offsetZ, true);
		if (entities.stream()
				.noneMatch(entity -> !entity.canBlockPlacedInto() && entity.getAABB().intersects(blockAABB))) {
			setBlockAt(x + side.offsetX, y + side.offsetY, z + side.offsetZ, player.selectedBlock);
		}
	}

	public Set<AABB> getBlockAABBsWithinAABB(AABB aabbIn, boolean ignorePassable) {
		int x1 = (int) Math.floor(aabbIn.x0);
		int y1 = (int) Math.floor(aabbIn.y0);
		int z1 = (int) Math.floor(aabbIn.z0);
		int x2 = (int) Math.ceil(aabbIn.x1);
		int y2 = (int) Math.ceil(aabbIn.y1);
		int z2 = (int) Math.ceil(aabbIn.z1);
		Set<AABB> aabbs = new HashSet<AABB>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					Block block = getBlockAt(x, y, z);
					if (block != null && (!ignorePassable || block.isImpassable())) {
						aabbs.add(block.getAABB(this, x, y, z, false));
					}
				}
			}
		}

		return aabbs;
	}

	public boolean isAABBWithinLiquid(AABB aabbIn) {
		int x1 = (int) Math.floor(aabbIn.x0);
		int y1 = (int) Math.floor(aabbIn.y0);
		int z1 = (int) Math.floor(aabbIn.z0);
		int x2 = (int) Math.ceil(aabbIn.x1);
		int y2 = (int) Math.ceil(aabbIn.y1);
		int z2 = (int) Math.ceil(aabbIn.z1);
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					Block block = getBlockAt(x, y, z);
					if (block != null && block.isLiquid()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public Collection<Chunk> getAllLoadedChunks() {
		return Collections.unmodifiableCollection(loadedChunks.values());
	}

	public void loadChunk(Chunk chunk) {
		ChunkPosition cpos = new ChunkPosition(chunk.getChunkX(), chunk.getChunkZ());
		loadedChunks.put(cpos, chunk);

		chunk.getRenderer().init();
		chunk.notifyNeighbourChunks();
	}

	public Chunk prepareChunk(ChunkPosition cpos) {
		// Chunk chunk = new Chunk(this, cpos.x, cpos.z);
		Chunk chunk = chunkProvider.provideChunk(cpos.x, cpos.z);
		return chunk;
	}

	public void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave) {
		chunk.unload();
		ChunkPosition cpos = new ChunkPosition(chunk.getChunkX(), chunk.getChunkZ());

		if (it != null) {
			it.remove();
		} else {
			loadedChunks.remove(cpos);
		}

		chunkProvider.save(this, chunk);
	}

	public boolean isChunkLoaded(int x, int z) {
		return loadedChunks.containsKey(new ChunkPosition(x, z));
	}

	public void update() {
		getEntities().removeIf(Entity::shouldDespawn);
		getEntities().stream().forEach(Entity::update);
		getEntities().stream().forEach(Entity::postUpdate);
		chunkLoadingTick();
	}

	private void chunkLoadingTick() {
		EntityPlayer player = TinyCraft.getInstance().player;
		Set<ChunkPosition> chunksToLoad = new HashSet<>();
		Vector3f ppos = player != null ? player.getInterpolatedPosition(0.0f) : spawnPoint;
		int startX = (((int) ppos.x) >> 4) - 8;
		int startZ = (((int) ppos.z) >> 4) - 8;
		for (int x = startX; x < startX + 16; x++) {
			for (int z = startZ; z < startZ + 16; z++) {
				chunksToLoad.add(new ChunkPosition(x, z));
			}
		}
		Set<ChunkPosition> chunksToUnload = new HashSet<>(loadedChunks.keySet());
		chunksToUnload.removeAll(chunksToLoad);

		chunksToLoad.removeAll(loadedChunks.keySet());
		{
			Iterator<ChunkPosition> it = chunksToLoad.iterator();
			boolean needsLoadNext = false;
			do {
				needsLoadNext = false;
				if (it.hasNext()) {
					ChunkPosition cpos = it.next();
					if (chunkProvider.canProvideChunk(cpos.x, cpos.z)) {
						Chunk chunk = prepareChunk(cpos);
						loadChunk(chunk);
						if (!chunk.isPopulated) {
							generationChunkProvider.populate(null, chunk.getChunkX(), chunk.getChunkZ());
						}
					} else {
						needsLoadNext = true;
					}
				} else {
					break;
				}
			} while (!checkSpawnAreaLoaded() || needsLoadNext);
		}
		{
			Iterator<ChunkPosition> it = chunksToUnload.iterator();
			if (it.hasNext()) {
				ChunkPosition cpos = it.next();
				unloadChunk(loadedChunks.get(cpos), null, true);
			}
		}
	}

	private boolean checkSpawnAreaLoaded() {
		if (TinyCraft.getInstance().player == null) {
			if (playerToSpawn == null) {
				if (isChunkLoaded((int) Math.floor(spawnPoint.x) >> 4, (int) Math.floor(spawnPoint.z) >> 4)) {
					EntityPlayer player = new EntityPlayer(this);
					player.setPosition(	spawnPoint.x + 0.5f,
										getHeightAt((int) Math.floor(spawnPoint.x), (int) Math.floor(spawnPoint.z))
												+ 1.0f,
										spawnPoint.z + 0.5f,
										false);
					addEntity(player);
					TinyCraft.getInstance().player = player;
					return true;
				}
			} else {
				Vector3f spawnPos = playerToSpawn.getInterpolatedPosition(1.0f);
				if (isChunkLoaded((int) Math.floor(spawnPos.x) >> 4, (int) Math.floor(spawnPos.z) >> 4)) {
					TinyCraft.getInstance().player = playerToSpawn;
					addEntity(playerToSpawn);
					playerToSpawn = null;
					return true;
				}
			}
		}
		return TinyCraft.getInstance().player != null;
	}

	public File getWorldSaveDir() {
		File worldDir = new File(TinyCraft.getInstance().getUserDataDir(), "world");
		worldDir.mkdirs();
		return worldDir;
	}

	public File getChunkSaveDir() {
		File chunkDir = new File(getWorldSaveDir(), "chunk_saves");
		chunkDir.mkdir();
		return chunkDir;
	}

	public void save() throws IOException {
		for (Chunk chunk : loadedChunks.values()) {
			chunkProvider.save(this, chunk);
		}
		{
			CompoundTag tag = new CompoundTag();
			tag.putLong("seed", seed);
			tag.putFloat("spawn_x", spawnPoint.x);
			tag.putFloat("spawn_y", spawnPoint.y);
			tag.putFloat("spawn_z", spawnPoint.z);
			NBTUtil.writeTag(tag, new File(getWorldSaveDir(), "world_info.sav"), false);
		}
		{
			CompoundTag tag = new CompoundTag();
			ListTag<CompoundTag> entityList = new ListTag<>(CompoundTag.class);
			for (Entity entity : getEntities()) {
				if (entity.isPersistent()) {
					CompoundTag entityTag = new CompoundTag();
					entityTag.putInt("type", EntityMap.getEntityId(entity.getClass()));
					entity.writeEntityData(entityTag);
					entityList.add(entityTag);
				}
			}
			tag.put("entityList", entityList);
			NBTUtil.writeTag(tag, new File(getWorldSaveDir(), "world_entities.sav"), false);
		}
	}

	public void load() throws IOException {
		if (new File(getWorldSaveDir(), "world_info.sav").isFile()) {
			entities.clear();
			chunkProvider.reset();
			generationChunkProvider = null;
			loadedChunks.clear();
			TinyCraft.getInstance().player = null;
			{
				CompoundTag tag = (CompoundTag) NBTUtil.readTag(new File(getWorldSaveDir(), "world_info.sav"));
				seed = tag.getLong("seed");
				spawnPoint.set(tag.getFloat("spawn_x"), tag.getFloat("spawn_y"), tag.getFloat("spawn_z"));
				generationChunkProvider = new ChunkGenerator(this, seed);
			}
			{
				CompoundTag tag = (CompoundTag) NBTUtil.readTag(new File(getWorldSaveDir(), "world_entities.sav"));
				@SuppressWarnings("unchecked")
				ListTag<CompoundTag> entityList = (ListTag<CompoundTag>) tag.getListTag("entityList");
				for (CompoundTag entityTag : entityList) {
					int type = entityTag.getInt("type");
					Class<? extends Entity> clazz = EntityMap.getEntityById(type);
					try {
						Entity entity = clazz.getConstructor(World.class).newInstance(this);
						entity.readEntityData(entityTag);
						if (entity instanceof EntityPlayer) {
							playerToSpawn = (EntityPlayer) entity;
						} else {
							addEntity(entity);
						}
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public int getSunLightLevelAt(int x, int y, int z) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		return chunk == null ? 0 : chunk.getSunLightLevelAt(x & 0xF, y, z & 0xF);
	}

	public void setSunLightLevelAt(int x, int y, int z, int val) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		if (chunk != null) {
			chunk.setSunLightLevelAt(x & 0xF, y, z & 0xF, val);
		}
	}

	public BiomeGenBase getBiomeAt(int x, int z) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		return chunk == null ? BiomeGenBase.plains : chunk.getBiomeAt(x & 0xF, z & 0xF);
	}

	public short getHeightAt(int x, int z) {
		Chunk chunk = loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		return chunk == null ? 0 : chunk.getHeightAt(x & 0xF, z & 0xF);
	}

	@Override
	public void chunkSaved(Chunk chunk) {

	}

	public void unload() {
		try {
			save();
			chunkProvider.waitUntilAllChunksSaved();
			Iterator<Chunk> it = loadedChunks.values().iterator();
			while (it.hasNext()) {
				Chunk chunk = it.next();
				unloadChunk(chunk, it, false);
			}
			entities.clear();
		} catch (IOException e) {
			throw new CrashException(e);
		}
	}

}
