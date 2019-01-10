package electory.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import electory.utils.CrashException;
import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;

public class ChunkProviderSP implements IChunkProvider {

	private World world;
	private Map<ChunkPosition, Thread> savingChunks = new ConcurrentHashMap<>(); 

	public ChunkProviderSP(World world) {
		this.world = world;
	}

	@Override
	public Chunk provideChunk(int cx, int cy) {
		File chunkFile = new File(world.getChunkSaveDir(), "c_" + cx + "_" + cy + ".sav.gz");
		if (chunkFile.isFile()) {
			Chunk chunk = new Chunk(world, cx, cy);
			try {
				BufferedDataInputStream bdis = new BufferedDataInputStream(new GZIPInputStream(new FileInputStream(chunkFile)));
				chunk.readChunkData(bdis);
				chunk.isPopulated = true;
				bdis.close();
			} catch (IOException e) {
				throw new CrashException(e);
			}
			return chunk;
		}
		return world.generationChunkProvider.provideChunk(cx, cy);
	}
	
	@Override
	public boolean canProvideChunk(int cx, int cy) {
		if(savingChunks.containsKey(new ChunkPosition(cx, cy))) {
			return false;
		}
		return true;
	}

	@Override
	public void populate(IChunkProvider saveProvider, int cx, int cy) {

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
					BufferedDataOutputStream bdos = new BufferedDataOutputStream(new GZIPOutputStream(new FileOutputStream(chunkFile)));
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
		for(Thread thread : savingChunks.values()) {
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

}
