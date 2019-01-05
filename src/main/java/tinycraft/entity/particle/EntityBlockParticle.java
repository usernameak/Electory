package tinycraft.entity.particle;

import java.util.Random;

import tinycraft.entity.Entity;
import tinycraft.render.IAtlasSprite;
import tinycraft.world.World;

public class EntityBlockParticle extends Entity {
	public IAtlasSprite sprite;

	private int despawnCounter;

	private static final Random random = new Random();

	public EntityBlockParticle(World world, IAtlasSprite sprite) {
		super(world);

		this.sprite = generateRandomPartialAtlasSprite(sprite);
		this.despawnCounter = random.nextInt(40) + 20;
		
		setSize(0.0625f, 0.0625f, 0.0625f);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(despawnCounter-- == 0) {
			scheduleDespawn();
		}
	}
	
	private static IAtlasSprite generateRandomPartialAtlasSprite(final IAtlasSprite spriteIn) {
		final float minU = spriteIn.getMinU() + (spriteIn.getMaxU() - spriteIn.getMinU()) * random.nextFloat() * 0.875f;
		final float maxU = minU + (spriteIn.getMaxU() - spriteIn.getMinU()) * 0.125f;
		final float minV = spriteIn.getMinV() + (spriteIn.getMaxV() - spriteIn.getMinV()) * random.nextFloat() * 0.875f;
		final float maxV = minV + (spriteIn.getMaxV() - spriteIn.getMinV()) * 0.125f;

		return new IAtlasSprite() {
			@Override
			public float getMinV() {
				return minV;
			}

			@Override
			public float getMinU() {
				return minU;
			}

			@Override
			public float getMaxV() {
				return maxV;
			}

			@Override
			public float getMaxU() {
				return maxU;
			}
		};
	}

}
