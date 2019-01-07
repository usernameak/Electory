package tinycraft.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;

import tinycraft.block.Block;
import tinycraft.client.TinyCraft;
import tinycraft.entity.Entity;
import tinycraft.entity.EntityPlayer;
import tinycraft.entity.particle.EntityBlockParticle;
import tinycraft.math.AABB;
import tinycraft.utils.EnumSide;
import tinycraft.utils.io.BufferedDataOutputStream;

public class World {
	private Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();
	private Set<Entity> entities = new HashSet<>();

	public long seed = ThreadLocalRandom.current().nextLong();

	public Random random = new Random(seed);

	private Vector3f spawnPoint = new Vector3f(0f, 128f, 0f);

	public Set<Entity> getEntities() {
		return entities;
	}

	public World() {
	}

	public Block getBlockAt(int x, int y, int z) {
		try {
			return loadedChunks.get(new ChunkPosition(x >> 4, z >> 4)).getBlockAt(x & 0xF, y, z & 0xF);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void setBlockAt(int x, int y, int z, Block block) {
		try {
			loadedChunks.get(new ChunkPosition(x >> 4, z >> 4)).setBlockAt(x & 0xF, y, z & 0xF, block);
		} catch (NullPointerException e) {
		}
	}

	public Chunk getChunkFromChunkCoord(int x, int z) {
		try {
			return loadedChunks.get(new ChunkPosition(x >> 4, z >> 4));
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
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
		if(entities.stream().noneMatch(entity -> !entity.canBlockPlacedInto() && entity.getAABB().intersects(blockAABB))) {
			setBlockAt(x + side.offsetX, y + side.offsetY, z + side.offsetZ, player.selectedBlock);
		}
	}

	public Set<AABB> getBlockAABBsWithinAABB(AABB aabbIn, boolean ignoreLiquids) {
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
					if (block != null && (!ignoreLiquids || !block.isLiquid())) {
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
		Chunk chunk = new Chunk(this, cpos.x, cpos.z);
		chunk.generate();
		return chunk;
	}

	public void unloadChunk(Chunk chunk) {
		chunk.unload();
		ChunkPosition cpos = new ChunkPosition(chunk.getChunkX(), chunk.getChunkZ());

		loadedChunks.remove(cpos);
	}

	public boolean isChunkLoaded(int x, int z) {
		return loadedChunks.containsKey(new ChunkPosition(x, z));
	}

	public void update() {
		getEntities().removeIf(Entity::shouldDespawn);
		getEntities().stream().forEach(Entity::update);
		getEntities().stream().forEach(Entity::postUpdate);
		EntityPlayer player = TinyCraft.getInstance().player;
		{
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
				do {
					if (it.hasNext()) {
						ChunkPosition cpos = it.next();
						Chunk chunk = prepareChunk(cpos);
						loadChunk(chunk);
					} else {
						break;
					}
				} while (!checkSpawnAreaLoaded());
			}
			{
				Iterator<ChunkPosition> it = chunksToUnload.iterator();
				if (it.hasNext()) {
					ChunkPosition cpos = it.next();
					unloadChunk(loadedChunks.get(cpos));
				}
			}
			// chunkUnloadQueue.addAll(chunksToUnload);
			// chunksToLoad.removeAll(chunkLoadQueue);
			/*
			 * chunksToLoad.removeIf(v -> chunkLoadQueue.stream() .anyMatch(c ->
			 * v.equals(new ChunkPosition(c.getChunkX(), c.getChunkZ()))));
			 */

			/*
			 * TinyCraft.getInstance().chunkLoadThread.world = this;
			 * TinyCraft.getInstance().chunkLoadThread.queue.addAll(chunksToLoad);
			 * synchronized (TinyCraft.getInstance().chunkLoadThread.lock) {
			 * TinyCraft.getInstance().chunkLoadThread.lock.notifyAll(); }
			 */

		}
		/*
		 * do { Iterator<Chunk> it = chunkLoadQueue.iterator(); if (it.hasNext()) {
		 * Chunk chunk = it.next(); //loadChunk(chunk); } else { break; } } while
		 * (!checkSpawnAreaLoaded()); { Iterator<ChunkPosition> it =
		 * chunkUnloadQueue.iterator(); if (it.hasNext()) { ChunkPosition cpos =
		 * it.next(); it.remove(); if (cpos != null) { Chunk chunk = new Chunk(this,
		 * cpos.x, cpos.z); chunk.generate(); //unloadChunk(chunk); } } }
		 */
	}

	private boolean checkSpawnAreaLoaded() {
		if (TinyCraft.getInstance().player == null
				&& isChunkLoaded((int) Math.floor(spawnPoint.x) >> 4, (int) Math.floor(spawnPoint.z) >> 4)) {
			EntityPlayer player = new EntityPlayer(this);
			player.setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z, false);
			addEntity(player);
			TinyCraft.getInstance().player = player;
			return true;
		}
		return TinyCraft.getInstance().player != null;
	}

	public void saveChunk(Chunk chunk) throws IOException {
		File worldDir = new File(TinyCraft.getInstance().getUserDataDir(), "world");
		worldDir.mkdirs();
		File saveFile = new File(worldDir, "chunk_" + chunk.getChunkX() + "_" + chunk.getChunkZ() + ".dat");
		BufferedDataOutputStream dos = new BufferedDataOutputStream(
				new GZIPOutputStream(new FileOutputStream(saveFile)));
		chunk.writeChunkData(dos);
		dos.close();
	}

	public void save() throws IOException {
		/*
		 * File worldDir = new File(TinyCraft.getInstance().getUserDataDir(), "world");
		 * worldDir.mkdirs(); for (int x = 0; x < chunks.length; x++) { for (int y = 0;
		 * y < chunks[x].length; y++) { File saveFile = new File(worldDir, "chunk_" + x
		 * + "_" + y + ".dat"); BufferedDataOutputStream dos = new
		 * BufferedDataOutputStream( new GZIPOutputStream(new
		 * FileOutputStream(saveFile))); chunks[x][y].writeChunkData(dos); dos.close();
		 * } }
		 * 
		 * { File worldInfoFile = new File(worldDir, "world_info.dat");
		 * BufferedDataOutputStream dos = new BufferedDataOutputStream(new
		 * FileOutputStream(worldInfoFile)); dos.writeLong(seed); dos.close(); }
		 * 
		 * { File worldEntitiesFile = new File(worldDir, "world_entities.dat");
		 * BufferedDataOutputStream dos = new BufferedDataOutputStream(new
		 * FileOutputStream(worldEntitiesFile)); for (Entity entity : entities) { if
		 * (entity.isPersistent()) {
		 * dos.writeInt(EntityMap.getEntityId(entity.getClass()));
		 * entity.writeEntityData(dos); } } dos.writeInt(0); dos.close(); }
		 */
	}

	public void load() throws IOException {
		/*
		 * File worldDir = new File(TinyCraft.getInstance().getUserDataDir(), "world");
		 * 
		 * if (worldDir.isDirectory()) { for (int x = 0; x < chunks.length; x++) { for
		 * (int y = 0; y < chunks[x].length; y++) { File saveFile = new File(worldDir,
		 * "chunk_" + x + "_" + y + ".dat"); BufferedDataInputStream dis = new
		 * BufferedDataInputStream( new GZIPInputStream(new FileInputStream(saveFile)));
		 * chunks[x][y].readChunkData(dis); dis.close(); } } }
		 * 
		 * { File worldInfoFile = new File(worldDir, "world_info.dat");
		 * BufferedDataInputStream dis = new BufferedDataInputStream(new
		 * FileInputStream(worldInfoFile)); seed = dis.readLong(); dis.close(); }
		 * 
		 * { File worldEntitiesFile = new File(worldDir, "world_entities.dat");
		 * BufferedDataInputStream dis = new BufferedDataInputStream(new
		 * FileInputStream(worldEntitiesFile)); entities.clear(); while (true) { int
		 * entityId = dis.readInt(); if (entityId == 0) { // EOF break; } Class<?
		 * extends Entity> entityClass = EntityMap.getEntityById(entityId);
		 * System.out.println(entityClass); try { Constructor<? extends Entity> cons =
		 * entityClass.getConstructor(World.class); Entity entity =
		 * cons.newInstance(this); entity.readEntityData(dis); if (entity instanceof
		 * EntityPlayer) { TinyCraft.getInstance().player = (EntityPlayer) entity; }
		 * addEntity(entity); } catch (NoSuchMethodException | SecurityException |
		 * InstantiationException | IllegalAccessException | IllegalArgumentException |
		 * InvocationTargetException e) { e.printStackTrace(); } } dis.close(); }
		 */
	}
	/*
	 * public void makeChunkPendingToLoad(Chunk chunk) { chunkLoadQueue.add(chunk);
	 * }
	 */
}
