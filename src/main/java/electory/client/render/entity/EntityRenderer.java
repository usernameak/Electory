package electory.client.render.entity;

import java.util.HashMap;
import java.util.Map;

import electory.client.render.entity.particle.RenderBlockParticle;
import electory.client.render.world.WorldRenderState;
import electory.entity.Entity;
import electory.entity.particle.EntityBlockParticle;

public abstract class EntityRenderer<T extends Entity> {
	private static Map<Class<? extends Entity>, EntityRenderer<?>> renderers = new HashMap<>();
	
	private static EntityRenderer<?> empty = new EmptyEntityRenderer();
	
	@SuppressWarnings("unchecked")
	public static <U extends Entity> EntityRenderer<U> getRendererFromEntity(U entity) {
		return (EntityRenderer<U>) renderers.getOrDefault(entity.getClass(), empty);
	}
	
	public abstract void renderEntity(T entity, float renderPartialTicks, WorldRenderState rs);
	
	static {
		renderers.put(EntityBlockParticle.class, new RenderBlockParticle());
	}
}
