package electory.world;

import java.util.Map;

import electory.block.Block;
import electory.nbt.CompoundTag;
import electory.nbt.IntTag;
import electory.nbt.Tag;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

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

	public void save(CompoundTag tag) {
		for (Map.Entry<Block, Integer> entry : blockToId.entrySet()) {
			tag.putInt(entry.getKey().getRegistryName(), entry.getValue().intValue());
		}
	}

	public void load(CompoundTag compoundTag) {
		for(int i = 0; i < idToBlock.length; i++) {
			idToBlock[i] = null;
		}
		blockToId.clear();
		for (Map.Entry<String, Tag<?>> entry : compoundTag.entrySet()) {
			String blockName = entry.getKey();
			int id = ((IntTag) entry.getValue()).asInt();
			Block block = Block.REGISTRY.get(blockName); // TODO: downgrade
			if (id + 1 > nextId) {
				nextId = id + 1;
			}
			idToBlock[id] = block;
			blockToId.put(block, id);
		}
	}
}
