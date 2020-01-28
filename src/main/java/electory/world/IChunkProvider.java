package electory.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public interface IChunkProvider {
	default void loadChunk(int cx, int cy, int cz) {
		loadChunkSynchronously(cx, cy, cz);
	}

	default void loadColumn(int cx, int cz) {
		loadColumnSynchronously(cx, cz);
	}

	default void update() {
	}

	default boolean isLoading(int cx, int cy, int cz) {
		return false;
	}

	default boolean isColumnLoading(int cx, int cz) {
		return false;
	}

	Chunk provideChunk(int cx, int cy, int cz);

	ChunkColumn provideColumn(int cx, int cz);

	void populate(IChunkProvider par1iChunkProvider, int par2, int par3, int par4);

	void save(IChunkSaveStatusHandler statusHandler, Chunk chunk);
	
	void save(ChunkColumn chunkColumn);

	boolean canLoadChunk(int cx, int cy, int cz);
	
	boolean canLoadColumn(int cx, int cz);

	void waitUntilAllSaved();

	void reset();

	Collection<Chunk> getAllLoadedChunks();
	
	Collection<ChunkColumn> getAllLoadedColumns();

	Map<ChunkPosition, Chunk> getLoadedChunkMap();
	
	Map<ColumnPosition, ChunkColumn> getLoadedColumnMap();

	boolean isChunkLoaded(int x, int y, int z);

	boolean isColumnLoaded(int x, int z);

	void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave);

	void unloadColumn(ChunkColumn chunk, Iterator<ChunkColumn> it, boolean doSave);

	void coldUnloadAll();

	Chunk loadChunkSynchronously(int cx, int cy, int cz);

	ChunkColumn loadColumnSynchronously(int cx, int cz);
}
