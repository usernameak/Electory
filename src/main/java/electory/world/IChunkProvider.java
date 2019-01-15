package electory.world;

import java.util.Collection;
import java.util.Iterator;

import com.koloboke.collect.map.LongObjMap;

public interface IChunkProvider {
	Chunk loadChunk(int cx, int cy);

	Chunk provideChunk(int cx, int cy);

	void populate(IChunkProvider par1iChunkProvider, int par2, int par3);

	void save(IChunkSaveStatusHandler statusHandler, Chunk chunk);

	boolean canLoadChunk(int cx, int cy);

	void waitUntilAllChunksSaved();

	void reset();

	Collection<Chunk> getAllLoadedChunks();

	LongObjMap<Chunk> getLoadedChunkMap();

	boolean isChunkLoaded(int x, int z);

	void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave);

	void coldUnloadAllChunks();
}
