package electory.item;

import electory.client.render.item.IItemRenderer;
import electory.utils.IRegistriable;
import electory.utils.NamedRegistry;

public class Item implements IRegistriable {
	public static final NamedRegistry<Item> REGISTRY = new NamedRegistry<>();
	private String registryName;	

	public Item() {
	}
	
	public IItemRenderer getRenderer() {
		return null;
	}

	@Override
	public void setRegistryName(String name) {
		this.registryName = name;
	}

	@Override
	public String getRegistryName() {
		return this.registryName;
	}

	public Class<?> getMetadataClass() {
		return null;
	}
}
