package electory.utils;

import electory.client.render.item.IItemRenderer;

public interface IRenderable {
	default public IItemRenderer getRenderer() {
		return null;
	}
}
