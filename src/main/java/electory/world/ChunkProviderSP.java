package electory.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import electory.profiling.ElectoryProfiler;
import electory.utils.CrashException;
import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class ChunkProviderSP implements IChunkProvider {

	private World world;
	private Map<ChunkPosition, Thread> savingChunks = new ConcurrentHashMap<>();
	private Long2ObjectOpenHashMap<Chunk> loadedChunks = new Long2ObjectOpenHashMap<>();
	private ExecutorService genExecutor = Executors.newSingleThreadExecutor();
	private Queue<Chunk> chunksToLoad = new ConcurrentLinkedQueue<>();
	private Set<ChunkPosition> loadingChunks = new HashSet<>();

	public ChunkProviderSP(World world) {
		this.world = world;
	}

	@Override
	public boolean isLoading(int cx, int cy) {
		return loadingChunks.contains(new ChunkPosition(cx, cy));
	}

	@Override
	public void loadChunk(int cx, int cy) {
		ElectoryProfiler.INSTANCE.begin("chunkload");
		loadingChunks.add(new ChunkPosition(cx, cy));
		genExecutor.execute(new Runnable() {
			@Override
			public void run() {
				File chunkFile = new File(world.getChunkSaveDir(), "c_" + cx + "_" + cy + ".sav.gz");

				if (chunkFile.isFile()) {
					Chunk chunk = new Chunk(world, cx, cy);
					try {
						BufferedDataInputStream bdis = new BufferedDataInputStream(
								new GZIPInputStream(new FileInputStream(chunkFile)));
						chunk.readChunkData(bdis);
						chunk.isPopulated = true;
						bdis.close();
					} catch (IOException e) {

					}
					chunksToLoad.add(chunk);
				} else {
					Chunk chunk = world.generationChunkProvider.loadChunkSynchronously(cx, cy);

					chunksToLoad.add(chunk);
				}
			}
		});
		/*
		 * Lock lock = chunk.getNeighbourWriteLock();
		 * 
		 * lock.lock();
		 */

		// lock.unlock();

		ElectoryProfiler.INSTANCE.end("chunkload");

		// return chunk;
	}

	@Override
	public void update() {
		flush();
	}

	private void flush() {
		Chunk chunkToLoad = null;
		while ((chunkToLoad = chunksToLoad.poll()) != null) {
			loadedChunks.put(ChunkPosition.createLong(chunkToLoad.getChunkX(), chunkToLoad.getChunkZ()), chunkToLoad);

			chunkToLoad.notifyNeighbourChunks();

			chunkToLoad.tryPopulateWithNeighbours(this);

			loadingChunks.remove(new ChunkPosition(chunkToLoad.getChunkX(), chunkToLoad.getChunkZ()));
		}
	}

	@Override
	public boolean canLoadChunk(int cx, int cy) {
		if (savingChunks.containsKey(new ChunkPosition(cx, cy))) {
			return false;
		}
		return true;
	}

	@Override
	public void populate(IChunkProvider saveProvider, int cx, int cy) {
		this.world.generationChunkProvider.populate(this, cx, cy);
		provideChunk(cx, cy).isPopulated = true;
	}

	@Override
	public void save(IChunkSaveStatusHandler statusHandler, Chunk chunk) {
		Thread thread = new Thread() {
			{
				setName("Chunk save thread");
			}

			public void run() {
				File chunkFile = new File(world.getChunkSaveDir(),
						"c_" + chunk.getChunkX() + "_" + chunk.getChunkZ() + ".sav.gz");
				try {
					BufferedDataOutputStream bdos = new BufferedDataOutputStream(
							new GZIPOutputStream(new FileOutputStream(chunkFile)));
					chunk.writeChunkData(bdos);
					bdos.close();
				} catch (IOException e) {
					throw new CrashException(e);
				}
				statusHandler.chunkSaved(chunk);
				savingChunks.remove(new ChunkPosition(chunk.getChunkX(), chunk.getChunkZ()));
			}
		};
		savingChunks.put(new ChunkPosition(chunk.getChunkX(), chunk.getChunkZ()), thread);
		thread.start();
	}

	@Override
	public void waitUntilAllChunksSaved() {
		for (Thread thread : savingChunks.values()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void reset() {
		savingChunks.clear();
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return loadedChunks.containsKey(ChunkPosition.createLong(x, z));
	}

	@Override
	public Collection<Chunk> getAllLoadedChunks() {
		return loadedChunks.values();
	}

	@Override
	public void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave) {
		if (chunk != null) { // for the sake of god
			chunk.unload();

			if (it != null) {
				it.remove();
			} else {
				loadedChunks.remove(ChunkPosition.createLong(chunk.getChunkX(), chunk.getChunkZ()));
			}

			save(world, chunk);
		}
	}

	@Override
	public Chunk provideChunk(int cx, int cy) {
		return loadedChunks.get(ChunkPosition.createLong(cx, cy));
	}

	@Override
	public void coldUnloadAllChunks() {
		loadedChunks.clear();
	}

	@Override
	public Long2ObjectOpenHashMap<Chunk> getLoadedChunkMap() {
		return loadedChunks;
	}

	@Override
	public Chunk loadChunkSynchronously(int cx, int cy) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void dispose() {
		genExecutor.shutdown();
		try {
			if (!genExecutor.awaitTermination(1L, TimeUnit.SECONDS)) {
				return;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
