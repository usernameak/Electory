package electory.world;

import java.util.Map;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

import electory.block.Block;
import electory.nbt.CompoundTag;
import electory.nbt.IntTag;
import electory.nbt.Tag;

public class BlockIDRegistry {
	private HashIntObjMap<Block> idToBlock = HashIntObjMaps.newMutableMap();
	private HashObjIntMap<Block> blockToId = HashObjIntMaps.newMutableMap();
	private int nextId = 1;

	public int getBlockId(Block block) {
		if (block == null)
			return 0;
		if (blockToId.containsKey(block)) {
			return blockToId.getInt(block);
		}
		int id = nextId++;
		idToBlock.put(id, block);
		blockToId.put(block, id);
		return id;
	}

	public Block getBlockById(int id) {
		if (id == 0)
			return null;
		if (idToBlock.containsKey(id)) {
			return idToBlock.get(id);
		}
		throw new IllegalArgumentException("no block id " + id);
	}

	public void save(CompoundTag tag) {
		for (Map.Entry<Block, Integer> entry : blockToId.entrySet()) {
			tag.putInt(entry.getKey().getRegistryName(), entry.getValue().intValue());
		}
	}

	public void load(CompoundTag compoundTag) {
		idToBlock.clear();
		blockToId.clear();
		for (Map.Entry<String, Tag<?>> entry : compoundTag.entrySet()) {
			String blockName = entry.getKey();
			int id = ((IntTag) entry.getValue()).asInt();
			Block block = Block.REGISTRY.get(blockName); // TODO: downgrade
			if (id + 1 > nextId) {
				nextId = id + 1;
			}
			idToBlock.put(id, block);
			blockToId.put(block, id);
		}
	}
}
