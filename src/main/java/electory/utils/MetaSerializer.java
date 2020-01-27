package electory.utils;

import java.lang.reflect.InvocationTargetException;

import electory.nbt.CompoundTag;

public abstract class MetaSerializer {
	private MetaSerializer() {
	}

	public static CompoundTag serializeObject(Object object) {
		String type = object.getClass().getName();
		CompoundTag tag = new CompoundTag();
		tag.putString("_Class", type);
		if (object instanceof IMetaSerializable) {
			IMetaSerializable serializable = (IMetaSerializable) object;
			serializable.writeToNBT(tag);
		} else if (object instanceof Enum) {
			tag.putString("EnumValue", ((Enum<?>) object).name());
		} else {
			throw new IllegalArgumentException();
		}
		return tag;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object deserializeObject(CompoundTag tag) {
		
		if (!tag.containsKey("_Class")) {
			return null;
		}

		String clazzName = tag.getString("_Class");
		Class<?> clazz;
		try {
			clazz = Class.forName(clazzName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		if (Enum.class.isAssignableFrom(clazz)) {
			return Enum.valueOf((Class<? extends Enum>) clazz, tag.getString("EnumValue"));
		} else if (IMetaSerializable.class.isAssignableFrom(clazz)) {
			IMetaSerializable meta;
			try {
				// TODO to method handles!!!ы
				meta = ((IMetaSerializable)clazz.getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
			meta.readFromNBT(tag);
			return meta;
		}
		return null;
	}

}
