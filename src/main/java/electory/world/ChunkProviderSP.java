package electory.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import electory.profiling.ElectoryProfiler;
import electory.utils.CrashException;
import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;

public class ChunkProviderSP implements IChunkProvider {

	private World world;
	private Map<ChunkPosition, Thread> savingChunks = new ConcurrentHashMap<>();
	private Map<ColumnPosition, Thread> savingColumns = new ConcurrentHashMap<>();
	private Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();
	private Map<ColumnPosition, ChunkColumn> loadedColumns = new HashMap<>();
	private Executor genExecutor = Executors.newSingleThreadExecutor();
	private Queue<Chunk> chunksToLoad = new ConcurrentLinkedQueue<>();
	private Queue<ChunkColumn> chunkColumnsToLoad = new ConcurrentLinkedQueue<>();
	private Set<ChunkPosition> loadingChunks = new HashSet<>();
	private Set<ColumnPosition> loadingColumns = new HashSet<>();

	public ChunkProviderSP(World world) {
		this.world = world;
	}

	@Override
	public boolean isLoading(int cx, int cy, int cz) {
		return loadingChunks.contains(new ChunkPosition(cx, cy, cz));
	}

	@Override
	public void loadChunk(int cx, int cy, int cz) {
		ElectoryProfiler.INSTANCE.begin("chunkload");
		loadingChunks.add(new ChunkPosition(cx, cy, cz));
		genExecutor.execute(new Runnable() {
			@Override
			public void run() {
				File chunkFile = new File(world.getChunkSaveDir(), "c_" + cx + "_" + cy + "_" + cz + ".sav.gz");

				if (chunkFile.isFile()) {
					Chunk chunk = new Chunk(world, cx, cy, cz);
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
					Chunk chunk = world.generationChunkProvider.loadChunkSynchronously(cx, cy, cz);

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
			loadedChunks
					.put(	new ChunkPosition(chunkToLoad.getChunkX(), chunkToLoad.getChunkY(), chunkToLoad.getChunkZ()),
							chunkToLoad);

			chunkToLoad.notifyNeighbourChunks();

			chunkToLoad.tryPopulateWithNeighbours(this);

			loadingChunks.remove(new ChunkPosition(chunkToLoad.getChunkX(), chunkToLoad.getChunkY(),
					chunkToLoad.getChunkZ()));
		}
	}

	@Override
	public boolean canLoadChunk(int cx, int cy, int cz) {
		if (!isColumnLoaded(cx, cz))
			return false;
		if (savingChunks.containsKey(new ChunkPosition(cx, cy, cz))) {
			return false;
		}
		return true;
	}

	@Override
	public void populate(IChunkProvider saveProvider, int cx, int cy, int cz) {
		this.world.generationChunkProvider.populate(this, cx, cy, cz);
		provideChunk(cx, cy, cz).isPopulated = true;
	}

	@Override
	public void save(IChunkSaveStatusHandler statusHandler, Chunk chunk) {
		Thread thread = new Thread() {
			{
				setName("Chunk save thread");
			}

			public void run() {
				File chunkFile = new File(world.getChunkSaveDir(),
						"c_" + chunk.getChunkX() + "_" + chunk.getChunkY() + "_" + chunk.getChunkZ() + ".sav.gz");
				try {
					BufferedDataOutputStream bdos = new BufferedDataOutputStream(
							new GZIPOutputStream(new FileOutputStream(chunkFile)));
					chunk.writeChunkData(bdos);
					bdos.close();
				} catch (IOException e) {
					throw new CrashException(e);
				}
				statusHandler.chunkSaved(chunk);
				savingChunks.remove(new ChunkPosition(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()));
			}
		};
		savingChunks.put(new ChunkPosition(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()), thread);
		thread.start();
	}

	@Override
	public void waitUntilAllSaved() {
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
	public boolean isChunkLoaded(int x, int y, int z) {
		return loadedChunks.containsKey(new ChunkPosition(x, y, z));
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
				loadedChunks.remove(new ChunkPosition(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()));
			}

			save(world, chunk);
		}
	}

	@Override
	public Chunk provideChunk(int cx, int cy, int cz) {
		return loadedChunks.get(new ChunkPosition(cx, cy, cz));
	}

	@Override
	public void coldUnloadAll() {
		loadedChunks.clear();
		loadedColumns.clear();
	}

	@Override
	public Map<ChunkPosition, Chunk> getLoadedChunkMap() {
		return loadedChunks;
	}

	@Override
	public Chunk loadChunkSynchronously(int cx, int cy, int cz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ChunkColumn provideColumn(int cx, int cz) {
		return loadedColumns.get(new ColumnPosition(cx, cz));
	}

	@Override
	public boolean isColumnLoaded(int x, int z) {
		return loadedColumns.containsKey(new ColumnPosition(x, z));
	}

	@Override
	public ChunkColumn loadColumnSynchronously(int cx, int cz) {
		File columnFile = new File(world.getChunkSaveDir(), "col_" + cx + "_" + cz + ".c2d.gz");
		ChunkColumn col;
		if (columnFile.isFile()) {
			col = new ChunkColumn(world, cx, cz);
			try {
				BufferedDataInputStream bdis = new BufferedDataInputStream(
						new GZIPInputStream(new FileInputStream(columnFile)));
				col.readColumnData(bdis);
				bdis.close();
			} catch (IOException e) {

			}
		} else {
			col = world.generationChunkProvider.loadColumnSynchronously(cx, cz);
		}
		return col;
	}

	@Override
	public void save(ChunkColumn chunkColumn) {
		Thread thread = new Thread() {
			{
				setName("Column save thread");
			}

			public void run() {
				File chunkFile = new File(world.getChunkSaveDir(),
						"col_" + chunkColumn.getChunkX() + "_" + chunkColumn.getChunkZ() + ".c2d.gz");
				try {
					BufferedDataOutputStream bdos = new BufferedDataOutputStream(
							new GZIPOutputStream(new FileOutputStream(chunkFile)));
					chunkColumn.writeColumnData(bdos);
					bdos.close();
				} catch (IOException e) {
					throw new CrashException(e);
				}
				// statusHandler.chunkSaved(chunk);
				savingColumns.remove(new ColumnPosition(chunkColumn.getChunkX(), chunkColumn.getChunkZ()));
			}
		};
		savingColumns.put(new ColumnPosition(chunkColumn.getChunkX(), chunkColumn.getChunkZ()), thread);
		thread.start();
	}

	@Override
	public boolean canLoadColumn(int cx, int cz) {
		if (savingColumns.containsKey(new ColumnPosition(cx, cz))) {
			return false;
		}
		return true;
	}

	@Override
	public Collection<ChunkColumn> getAllLoadedColumns() {
		return loadedColumns.values();
	}

	@Override
	public Map<ColumnPosition, ChunkColumn> getLoadedColumnMap() {
		return loadedColumns;
	}

	@Override
	public void unloadColumn(ChunkColumn chunk, Iterator<ChunkColumn> it, boolean doSave) {
		if (chunk != null) { // for the sake of god
			// chunk.unload();

			if (it != null) {
				it.remove();
			} else {
				loadedColumns.remove(new ColumnPosition(chunk.getChunkX(), chunk.getChunkZ()));
			}

			save(chunk);
		}
	}

}
