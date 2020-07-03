package electory.world;

import electory.block.Block;
import electory.utils.io.IllegalSerializedDataException;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public class BlockIDRegistry {
	private Block[] idToBlock = new Block[32768];
	private Object2IntOpenHashMap<Block> blockToId = new Object2IntOpenHashMap<>();
	private int nextId = 1;

	public int getBlockId(Block block) {
		if (block == null)
			return 0;
		if (blockToId.containsKey(block)) {
			return blockToId.getInt(block);
		}
		int id = nextId++;
		idToBlock[id] = block;
		blockToId.put(block, id);
		return id;
	}

	public Block getBlockById(int id) {
		if (id == 0)
			return null;
		if (idToBlock[id] != null) {
			return idToBlock[id];
		}
		throw new IllegalArgumentException("no block id " + id);
	}

	public void save(DataOutput dos) throws IOException {
		dos.write(1); // version number
		dos.writeInt(blockToId.size());
		for (Map.Entry<Block, Integer> entry : blockToId.object2IntEntrySet()) {
			dos.writeUTF(entry.getKey().getRegistryName());
			dos.writeInt(entry.getValue());
		}
	}

	public void load(DataInput dis) throws IOException {
		if(dis.readByte() != 1) {
			throw new IllegalSerializedDataException("unsupported block id registry version");
		}
		Arrays.fill(idToBlock, null);
		blockToId.clear();
		int size = dis.readInt();
		for (int i = 0; i < size; i++) {
			String blockName = dis.readUTF();
			int id = dis.readInt();
			Block block = Block.REGISTRY.get(blockName); // TODO: downgrade
			if (id + 1 > nextId) {
				nextId = id + 1;
			}
			idToBlock[id] = block;
			blockToId.put(block, id);
		}
	}
}
