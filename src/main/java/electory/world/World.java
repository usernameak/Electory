package electory.world;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;
import electory.utils.io.IllegalSerializedDataException;
import org.joml.Vector3d;
import org.joml.Vector3f;

import electory.block.Block;
import electory.client.TinyCraft;
import electory.client.audio.AudioSource;
import electory.entity.Entity;
import electory.entity.EntityMap;
import electory.entity.EntityPlayer;
import electory.entity.particle.EntityBlockParticle;
import electory.item.Item;
import electory.item.ItemBlock;
import electory.item.ItemStack;
import electory.math.AABB;
import electory.utils.CrashException;
import electory.utils.EnumSide;
import electory.world.gen.ChunkGenerator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public abstract class World implements IChunkSaveStatusHandler {
	private Set<Entity> entities = new HashSet<>();

	public long seed = ThreadLocalRandom.current().nextLong();

	public Random random = new Random(seed);

	protected Vector3d spawnPoint = new Vector3d(random.nextInt(2048) - 1024, 256f, random.nextInt(2048) - 1024);

	public static final int FLAG_SKIP_RENDER_UPDATE = 1;
	public static final int FLAG_SKIP_LIGHT_UPDATE = 2;
	public static final int FLAG_FAST_LIGHT_UPDATE = 4;
	public static final int FLAG_SKIP_UPDATE = FLAG_SKIP_LIGHT_UPDATE | FLAG_SKIP_RENDER_UPDATE;

	public static final int LIGHT_LEVEL_TYPE_SKY = 0;
	public static final int LIGHT_LEVEL_TYPE_BLOCK = 1;

	public static final int CHUNKLOAD_DISTANCE = 8;
	public static final int CHUNKLOAD_DISTANCE2 = CHUNKLOAD_DISTANCE * 2;

	public IChunkProvider generationChunkProvider = new ChunkGenerator(this, seed);
	public IChunkProvider chunkProvider = new ChunkProviderSP(this);

	public BlockIDRegistry blockIdRegistry = new BlockIDRegistry();

	protected EntityPlayer playerToSpawn = null;

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

	public Chunk getChunkFromWorldCoord(int x, int z) {
		return chunkProvider.provideChunk(x >> 4, z >> 4);
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void breakBlockByPlayer(EntityPlayer player, int x, int y, int z) {
		Block block = getBlockAt(x, y, z);

		breakBlockWithParticles(x, y, z);

		player.inventory.giveItem(new ItemStack(Item.REGISTRY.get(block.getRegistryName())));
	}

	public void breakBlockWithParticles(int x, int y, int z) {
		Block oldBlock = getBlockAt(x, y, z);
		setBlockAt(x, y, z, null);
		if (oldBlock != null) {
			for (int i = 0; i < 8; i++) {
				EntityBlockParticle particle = new EntityBlockParticle(this,
						oldBlock.getAtlasSprite(EnumSide.getOrientation(random.nextInt(6))));
				particle.setPosition(x + 0.5f, y + 0.5f, z + 0.5f, false);
				particle.setVelocity(	random.nextFloat() * 2.f - 1.f,
										random.nextFloat() * .5f,
										random.nextFloat() * 2.f - 1.f);
				addEntity(particle);
			}
		}
		oldBlock.getSound().play(this, x, y, z);

	}

	public void interactWithBlock(EntityPlayer player, int x, int y, int z, EnumSide side) {
		if (!this.getBlockAt(x, y, z).interactWithBlock(player, this, x, y, z, side)) {
			ItemStack stack = player.inventory.getStackInSlot(player.inventory.getSelectedSlot());
			if (stack.item != null && stack.item instanceof ItemBlock) {
				Block block = ((ItemBlock) stack.item).getBlock();
				AABB blockAABB = block.getAABB(this, x + side.offsetX, y + side.offsetY, z + side.offsetZ, true);
				if (entities.stream()
						.noneMatch(entity -> !entity.canBlockPlacedInto() && entity.getAABB().intersects(blockAABB))) {
					if (stack.remove(1)) {
						setBlockAt(x + side.offsetX, y + side.offsetY, z + side.offsetZ, block);
						block.blockPlacedByPlayer(	player,
													this,
													x + side.offsetX,
													y + side.offsetY,
													z + side.offsetZ,
													EnumSide.getOrientation(EnumSide.OPPOSITES[side.ordinal()]));

					}
				}
			}
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
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
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
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
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
		getEntities().forEach(Entity::update);
		getEntities().forEach(Entity::postUpdate);
		chunkLoadingTick();
	}

	protected abstract Collection<EntityPlayer> getPlayers();

	private void chunkLoadingTick() {
		chunkProvider.update();
		generationChunkProvider.update();
		
		LongOpenHashSet chunksToLoad = new LongOpenHashSet();
		for (EntityPlayer player : getPlayers()) {
			Vector3d ppos = player != null ? player.getInterpolatedPosition(0.0f)
					: (playerToSpawn != null ? playerToSpawn.getInterpolatedPosition(0.0f) : spawnPoint);
			int startX = (((int) ppos.x) >> 4) - CHUNKLOAD_DISTANCE;
			int startZ = (((int) ppos.z) >> 4) - CHUNKLOAD_DISTANCE;
			for (int x = startX; x <= startX + CHUNKLOAD_DISTANCE2; x++) {
				for (int z = startZ; z <= startZ + CHUNKLOAD_DISTANCE2; z++) {
					chunksToLoad.add(ChunkPosition.createLong(x, z));
				}
			}
		}
		LongOpenHashSet chunksToUnload = new LongOpenHashSet(chunkProvider.getLoadedChunkMap().keySet());
		chunksToUnload.removeAll(chunksToLoad);

		chunksToLoad.removeAll(chunkProvider.getLoadedChunkMap().keySet());
		{
			LongIterator it = chunksToLoad.iterator();
			int needsLoadNext = Integer.MAX_VALUE;
			while (!checkSpawnAreaLoaded() || needsLoadNext > 0) {
				needsLoadNext--;
				if (it.hasNext()) {
					long cpos = it.nextLong();
					int cx = (int) (cpos & 4294967295L), cz = (int) ((cpos >> 32) & 4294967295L);
					if (!chunkProvider.isLoading(cx, cz) && chunkProvider.canLoadChunk(cx, cz)) {
						chunkProvider.loadChunk(cx, cz);
					} else {
						needsLoadNext++;
					}
				} else {
					break;
				}
			}
		}
		{
			LongIterator it = chunksToUnload.iterator();
			if (it.hasNext()) {
				long cpos = it.nextLong();
				chunkProvider.unloadChunk(chunkProvider
						.provideChunk((int) (cpos & 4294967295L), (int) ((cpos >> 32) & 4294967295L)), null, true);
			}
		}
	}

	protected abstract boolean checkSpawnAreaLoaded();

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
			FileOutputStream fos = new FileOutputStream(new File(getWorldSaveDir(), "world_info.sav"));
			BufferedDataOutputStream dos = new BufferedDataOutputStream(fos);
			dos.write(1); // version number
			dos.writeLong(seed);
			dos.writeDouble(spawnPoint.x);
			dos.writeDouble(spawnPoint.y);
			dos.writeDouble(spawnPoint.z);
			blockIdRegistry.save(dos);
			// NBTUtil.writeTag(tag, new File(getWorldSaveDir(), "world_info.sav"), false);
			dos.close();
		}
		{
			FileOutputStream fos = new FileOutputStream(new File(getWorldSaveDir(), "world_entities.sav"));
			BufferedDataOutputStream dos = new BufferedDataOutputStream(fos);

			for (Entity entity : getEntities()) {
				if (entity.isPersistent()) {
					dos.writeByte(EntityMap.getEntityId(entity.getClass()));
					entity.writeEntityData(dos);
				}
			}
			dos.writeByte(0);
		}
	}

	public void load() throws IOException {
		File worldInfoFile = new File(getWorldSaveDir(), "world_info.sav");
		if (worldInfoFile.isFile()) {
			entities.clear();
			chunkProvider.reset();
			generationChunkProvider = null;
			chunkProvider.coldUnloadAllChunks();
			TinyCraft.getInstance().player = null;
			{
				FileInputStream fis = new FileInputStream(worldInfoFile);
				BufferedDataInputStream dis = new BufferedDataInputStream(fis);

				if(dis.readByte() != 1) {
					throw new IllegalSerializedDataException("unsupported world info version");
				}

				seed = dis.readLong();
				spawnPoint.set(dis.readDouble(), dis.readDouble(), dis.readDouble());
				blockIdRegistry.load(dis);
				generationChunkProvider = new ChunkGenerator(this, seed);
				dis.close();
			}
			{

				FileInputStream fis = new FileInputStream(new File(getWorldSaveDir(), "world_entities.sav"));
				BufferedDataInputStream dis = new BufferedDataInputStream(fis);
				while(true) {
					byte type = dis.readByte();
					if(type == 0) break;
					Class<? extends Entity> clazz = EntityMap.getEntityById(type);
					try {
						Entity entity = EntityPlayer.class.isAssignableFrom(clazz) ? constructPlayer()
								: clazz.getConstructor(World.class).newInstance(this);
						entity.readEntityData(dis);
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
				dis.close();
			}
		}
	}

	protected abstract EntityPlayer constructPlayer();

	public int getLightLevelAt(int x, int y, int z, int lightLevelType) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? 0x0 : chunk.getLightLevelAt(x & 0xF, y, z & 0xF, lightLevelType);
	}

	public void setLightLevelAt(int x, int y, int z, int lightLevelType, int val, int flags) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setLightLevelAt(x & 0xF, y, z & 0xF, lightLevelType, val, 0);
		}
	}

	public EnumWorldBiome getBiomeAt(int x, int z) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? EnumWorldBiome.plains : chunk.getBiomeAt(x & 0xF, z & 0xF);
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
			chunkProvider.dispose();
			entities.clear();
		} catch (IOException e) {
			throw new CrashException(e);
		}
	}

	public void playSFX(String path, float x, float y, float z, float radius, boolean loop) {
		TinyCraft.getInstance().soundManager
				.play(	"world;" + path,
						new AudioSource(path).setPosition(new Vector3f(x, y, z)).setRadius(radius).setLooping(loop));
	}

	public <T> T getBlockMetadataAt(int x, int y, int z) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		return chunk == null ? null : chunk.getBlockMetadataAt(x & 0xF, y, z & 0xF);
	}

	public void setBlockMetadataAt(int x, int y, int z, Object meta) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setBlockMetadataAt(x & 0xF, y, z & 0xF, meta);
		}
	}

	public void setBlockWithMetadataAt(int x, int y, int z, Block block, Object meta, int flags) {
		Chunk chunk = chunkProvider.provideChunk(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setBlockWithMetadataAt(x & 0xF, y, z & 0xF, block, meta, flags);
		}
	}
}
