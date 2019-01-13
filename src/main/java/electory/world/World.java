package electory.world;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.joml.Vector3d;

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
	private Set<Entity> entities = new HashSet<>();

	public long seed = ThreadLocalRandom.current().nextLong();

	public Random random = new Random(seed);

	private Vector3d spawnPoint = new Vector3d(/* random.nextInt(2048) - 1024 */ 0f, 256f,
			/* random.nextInt(2048) - 1024 */ 0f);

	public static final int FLAG_SKIP_RENDER_UPDATE = 1;
	public static final int FLAG_SKIP_LIGHT_UPDATE = 2;
	
	public static final int CHUNKLOAD_DISTANCE = 8;
	public static final int CHUNKLOAD_DISTANCE2 = CHUNKLOAD_DISTANCE * 2;

	public IChunkProvider generationChunkProvider = new ChunkGenerator(this, seed);
	public IChunkProvider chunkProvider = new ChunkProviderSP(this);

	private EntityPlayer playerToSpawn = null;

	public Set<Entity> getEntities() {
		return entities;
	}

	public World() {
	}

	public Block getBlockAt(int x, int y, int z) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? null : chunk.getBlockAt(x & 0xF, y, z & 0xF);
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setBlockAt(x & 0xF, y, z & 0xF, block);
		}
	}

	public Chunk getChunkFromChunkCoord(int x, int z) {
		return chunkProvider.provideChunk(x, z);
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
		oldBlock.getSound().play(this, x, y, z);
		
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

	public void update() {
		getEntities().removeIf(Entity::shouldDespawn);
		getEntities().stream().forEach(Entity::update);
		getEntities().stream().forEach(Entity::postUpdate);
		chunkLoadingTick();
	}

	private void chunkLoadingTick() {		
		EntityPlayer player = TinyCraft.getInstance().player;
		Set<ChunkPosition> chunksToLoad = new HashSet<>();
		Vector3d ppos = player != null ? player.getInterpolatedPosition(0.0f)
				: (playerToSpawn != null ? playerToSpawn.getInterpolatedPosition(0.0f) : spawnPoint);
		int startX = (((int) ppos.x) >> 4) - CHUNKLOAD_DISTANCE;
		int startZ = (((int) ppos.z) >> 4) - CHUNKLOAD_DISTANCE;
		for (int x = startX; x < startX + CHUNKLOAD_DISTANCE2; x++) {
			for (int z = startZ; z < startZ + CHUNKLOAD_DISTANCE2; z++) {
				chunksToLoad.add(new ChunkPosition(x, z));
			}
		}
		Set<ChunkPosition> chunksToUnload = new HashSet<>(chunkProvider.getLoadedChunkMap().keySet());
		chunksToUnload.removeAll(chunksToLoad);

		chunksToLoad.removeAll(chunkProvider.getLoadedChunkMap().keySet());
		{
			Iterator<ChunkPosition> it = chunksToLoad.iterator();
			boolean needsLoadNext = false;
			do {
				needsLoadNext = false;
				if (it.hasNext()) {
					ChunkPosition cpos = it.next();
					if (chunkProvider.canLoadChunk(cpos.x, cpos.z)) {
						chunkProvider.loadChunk(cpos.x, cpos.z);
						/*if (!chunk.isPopulated) {
							generationChunkProvider.populate(null, chunk.getChunkX(), chunk.getChunkZ());
						}*/
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
				chunkProvider.unloadChunk(chunkProvider.provideChunk(cpos.x, cpos.z), null, true);
			}
		}
	}

	private boolean checkSpawnAreaLoaded() {
		if (TinyCraft.getInstance().player == null) {
			if (playerToSpawn == null) {
				if (chunkProvider.isChunkLoaded((int) Math.floor(spawnPoint.x) >> 4,
												(int) Math.floor(spawnPoint.z) >> 4)) {
					EntityPlayer player = new EntityPlayer(this);
					player.setPosition(	spawnPoint.x + 0.5f,
										getHeightAt((int) Math.floor(spawnPoint.x), (int) Math.floor(spawnPoint.z))
												+ 1.0f,
										spawnPoint.z + 0.5f,
										false);
					addEntity(player);
					TinyCraft.getInstance().setPlayer(player);
					return true;
				}
			} else {
				Vector3d spawnPos = playerToSpawn.getInterpolatedPosition(1.0f);
				if (chunkProvider.isChunkLoaded((int) Math.floor(spawnPos.x) >> 4, (int) Math.floor(spawnPos.z) >> 4)) {
					TinyCraft.getInstance().setPlayer(playerToSpawn);
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
		for (Chunk chunk : chunkProvider.getAllLoadedChunks()) {
			chunkProvider.save(this, chunk);
		}
		{
			CompoundTag tag = new CompoundTag();
			tag.putLong("seed", seed);
			tag.putDouble("spawn_x", spawnPoint.x);
			tag.putDouble("spawn_y", spawnPoint.y);
			tag.putDouble("spawn_z", spawnPoint.z);
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
			chunkProvider.coldUnloadAllChunks();
			TinyCraft.getInstance().player = null;
			{
				CompoundTag tag = (CompoundTag) NBTUtil.readTag(new File(getWorldSaveDir(), "world_info.sav"));
				seed = tag.getLong("seed");
				spawnPoint.set(tag.getDouble("spawn_x"), tag.getDouble("spawn_y"), tag.getDouble("spawn_z"));
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
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? 0 : chunk.getSunLightLevelAt(x & 0xF, y, z & 0xF);
	}

	public void setSunLightLevelAt(int x, int y, int z, int val) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setSunLightLevelAt(x & 0xF, y, z & 0xF, val);
		}
	}

	public BiomeGenBase getBiomeAt(int x, int z) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? BiomeGenBase.plains : chunk.getBiomeAt(x & 0xF, z & 0xF);
	}

	public short getHeightAt(int x, int z) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? 0 : chunk.getHeightAt(x & 0xF, z & 0xF);
	}

	@Override
	public void chunkSaved(Chunk chunk) {

	}

	public void unload() {
		try {
			save();
			chunkProvider.waitUntilAllChunksSaved();
			Iterator<Chunk> it = chunkProvider.getAllLoadedChunks().iterator();
			while (it.hasNext()) {
				Chunk chunk = it.next();
				chunkProvider.unloadChunk(chunk, it, false);
			}
			entities.clear();
		} catch (IOException e) {
			throw new CrashException(e);
		}
	}

	public void playSFX(String path, float x, float y, float z, float radius) {
		TinyCraft.getInstance().soundManager.playSFX(path, "world;" + path, x, y, z, radius);
	}
}

