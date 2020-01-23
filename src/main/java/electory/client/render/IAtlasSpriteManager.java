package electory.client.render;

public interface IAtlasSpriteManager {
	IAtlasSprite registerSprite(String sprite);
	
	default void buildAtlas() {}
}
