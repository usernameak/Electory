package tinycraft.entity.render;

import java.util.HashMap;
import java.util.Map;

import tinycraft.entity.Entity;
import tinycraft.entity.particle.EntityBlockParticle;
import tinycraft.entity.render.particle.RenderBlockParticle;

public abstract class EntityRenderer<T extends Entity> {
	private static Map<Class<? extends Entity>, EntityRenderer<?>> renderers = new HashMap<>();
	
	private static EntityRenderer<?> empty = new EmptyEntityRenderer();
	
	@SuppressWarnings("unchecked")
	public static <U extends Entity> EntityRenderer<U> getRendererFromEntity(U entity) {
		return (EntityRenderer<U>) renderers.getOrDefault(entity.getClass(), empty);
	}
	
	public abstract void renderEntity(T entity, float renderPartialTicks);
	
	static {
		renderers.put(EntityBlockParticle.class, new RenderBlockParticle());
	}
}
