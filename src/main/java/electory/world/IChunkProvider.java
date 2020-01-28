package electory.world;

import java.util.Collection;
import java.util.Iterator;

import com.koloboke.collect.map.LongObjMap;

public interface IChunkProvider {
	default void loadChunk(int cx, int cy, int cz) {
		loadChunkSynchronously(cx, cy, cz);
	}
	
	default void update() {
	}
	
	default boolean isLoading(int cx, int cy, int cz) {
		return false;
	}

	Chunk provideChunk(int cx, int cy, int cz);

	void populate(IChunkProvider par1iChunkProvider, int par2, int par3, int par4);

	void save(IChunkSaveStatusHandler statusHandler, Chunk chunk);

	boolean canLoadChunk(int cx, int cy, int cz);

	void waitUntilAllChunksSaved();

	void reset();

	Collection<Chunk> getAllLoadedChunks();

	LongObjMap<Chunk> getLoadedChunkMap();

	boolean isChunkLoaded(int x, int y, int z);

	void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave);

	void coldUnloadAllChunks();

	Chunk loadChunkSynchronously(int cx, int cy, int cz);
}
