package electory.world;

public interface IChunkProvider {
	Chunk provideChunk(int cx, int cy);

	void populate(IChunkProvider par1iChunkProvider, int par2, int par3);
	
	void save(IChunkSaveStatusHandler statusHandler, Chunk chunk);
	
	boolean canProvideChunk(int cx, int cy);
	
	void waitUntilAllChunksSaved();
	
	void reset();
}
