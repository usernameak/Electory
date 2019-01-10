package electory.client.render;

public class AtlasSpriteManagerImpl implements IAtlasSpriteManager {
	private final float invSpriteCountX, invSpriteCountY;
	@SuppressWarnings("unused") // TODO: use
	private final int spriteCountX, spriteCountY;

	public AtlasSpriteManagerImpl(int spriteCountX, int spriteCountY) {
		super();
		this.spriteCountX = spriteCountX;
		this.spriteCountY = spriteCountY;
		this.invSpriteCountX = 1.f / spriteCountX;
		this.invSpriteCountY = 1.f / spriteCountY;
	}

	@Override
	public IAtlasSprite registerSpriteByID(int id) {
		final int x = id % spriteCountX;
		final int y = id / spriteCountX;

		return new IAtlasSprite() {
			@Override
			public float getMinU() {
				return invSpriteCountX * x;
			}

			@Override
			public float getMinV() {
				return invSpriteCountY * y;
			}

			@Override
			public float getMaxU() {
				return invSpriteCountX * (x + 1);
			}

			@Override
			public float getMaxV() {
				return invSpriteCountY * (y + 1);
			}
		};
	}

}
