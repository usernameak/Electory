package electory.world;

import java.util.Collection;
import java.util.Iterator;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public interface IChunkProvider {
	default void loadChunk(int cx, int cy) {
		loadChunkSynchronously(cx, cy);
	}
	
	default void update() {
	}
	
	default boolean isLoading(int cx, int cy) {
		return false;
	}

	Chunk provideChunk(int cx, int cy);

	void populate(IChunkProvider par1iChunkProvider, int par2, int par3);

	void save(IChunkSaveStatusHandler statusHandler, Chunk chunk);

	boolean canLoadChunk(int cx, int cy);

	void waitUntilAllChunksSaved();

	void reset();

	Collection<Chunk> getAllLoadedChunks();

	Long2ObjectOpenHashMap<Chunk> getLoadedChunkMap();

	boolean isChunkLoaded(int x, int z);

	void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave);

	void coldUnloadAllChunks();

	Chunk loadChunkSynchronously(int cx, int cy);
	
	default void dispose() {}
}
