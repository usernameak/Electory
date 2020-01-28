package electory.world;

import java.io.IOException;

import electory.nbt.CompoundTag;
import electory.nbt.IntArrayTag;
import electory.nbt.Tag;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;
import lombok.Getter;

public class ChunkColumn {
	private int heightMap[] = new int[World.CHUNK_SIZE * World.CHUNK_SIZE];
	private int approxHeightMap[] = new int[World.CHUNK_SIZE * World.CHUNK_SIZE];
	
	@Getter
	private World world;
	
	@Getter
	private int chunkX, chunkZ;
	
	public ChunkColumn(World world, int chunkX, int chunkZ) {
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public int getHeightAt(int x, int z) {
		if (x < 0 || z < 0 || x >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return 0;
		}
		return heightMap[x * World.CHUNK_SIZE + z];
	}

	public void writeColumnData(ArrayDataOutput dos) throws IOException {
		CompoundTag tag = new CompoundTag();
		tag.put("heightMap", new IntArrayTag(heightMap));
		tag.put("approxHeightMap", new IntArrayTag(approxHeightMap));
		tag.serialize(dos, 0);
	}

	public void readColumnData(ArrayDataInput dis) throws IOException {
		CompoundTag tag = (CompoundTag) Tag.deserialize(dis, 0);
		heightMap = ((IntArrayTag) tag.get("heightMap")).getValue();
		approxHeightMap = ((IntArrayTag) tag.get("approxHeightMap")).getValue();
	}

	public void setHeightAt(int x, int z, int y) {
		if (x < 0 || z < 0 || x >= World.CHUNK_SIZE || z >= World.CHUNK_SIZE) {
			return;
		}
		heightMap[x * World.CHUNK_SIZE + z] = y;
	}
}
