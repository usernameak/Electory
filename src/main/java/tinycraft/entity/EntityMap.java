package tinycraft.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityMap {

	private static Map<Class<? extends Entity>, Integer> entityToIDMap = new HashMap<>();
	private static Map<Integer, Class<? extends Entity>> idToEntityMap = new HashMap<>();

	public static int getEntityId(Class<? extends Entity> entityClass) {
		return entityToIDMap.getOrDefault(entityClass, 0);
	}

	public static Class<? extends Entity> getEntityById(int id) {
		return idToEntityMap.getOrDefault(id, Entity.class);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, int id) {
		entityToIDMap.put(entityClass, id);
		idToEntityMap.put(id, entityClass);
	}

	static {
		registerEntity(Entity.class, 0);
		registerEntity(EntityPlayer.class, 1);
	}
}
