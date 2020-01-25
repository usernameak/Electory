package electory.utils;

import java.util.Collection;
import java.util.HashMap;

public class NamedRegistry<T extends IRegistriable> {
	private HashMap<String, T> items = new HashMap<>();
	
	public void register(String name, T block) {
		if(items.containsKey(name)) {
			throw new IllegalStateException("registry id conflict for `" + name + "`");
		}
		items.put(name, block);
		block.setRegistryName(name);
	}
	
	public T get(String name) {
		return items.get(name);
	}

	public Collection<T> getAllBlocks() {
		return items.values();
	}
}
