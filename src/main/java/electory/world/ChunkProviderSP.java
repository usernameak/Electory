package electory.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import electory.utils.CrashException;
import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;

public class ChunkProviderSP implements IChunkProvider {

	private World world;
	private Map<ChunkPosition, Thread> savingChunks = new ConcurrentHashMap<>();
	private LongObjMap<Chunk> loadedChunks = HashLongObjMaps.newMutableMap();

	public ChunkProviderSP(World world) {
		this.world = world;
	}

	@Override
	public Chunk loadChunk(int cx, int cy) {
		File chunkFile = new File(world.getChunkSaveDir(), "c_" + cx + "_" + cy + ".sav.gz");
		Chunk chunk = null;
		if (chunkFile.isFile()) {
			chunk = new Chunk(world, cx, cy);
			try {
				BufferedDataInputStream bdis = new BufferedDataInputStream(
						new GZIPInputStream(new FileInputStream(chunkFile)));
				chunk.readChunkData(bdis);
				chunk.isPopulated = true;
				bdis.close();
			} catch (IOException e) {
				
			}
		}
		if (chunk == null) {
			chunk = world.generationChunkProvider.loadChunk(cx, cy);
		}

		Lock lock = chunk.getNeighbourWriteLock();
		
		lock.lock();
		
		loadedChunks.put(ChunkPosition.createLong(chunk.getChunkX(), chunk.getChunkZ()), chunk);

		chunk.notifyNeighbourChunks();
		
		chunk.tryPopulateWithNeighbours(this);
		
		lock.unlock();
		
		return chunk;
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
	public LongObjMap<Chunk> getLoadedChunkMap() {
		return loadedChunks;
	}
}
