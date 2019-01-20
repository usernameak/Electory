package electory.utils;

import electory.block.IBlockMetaSerializable;
import electory.nbt.CompoundTag;

public abstract class MetaSerializer {

	private MetaSerializer() {

	}

	public static CompoundTag serializeObject(Object object) {
		String type = object.getClass().getName();
		CompoundTag tag = new CompoundTag();
		tag.putString("_Class", type);
		if (object instanceof IBlockMetaSerializable) {
			IBlockMetaSerializable serializable = (IBlockMetaSerializable) object;
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
		} else if (IBlockMetaSerializable.class.isAssignableFrom(clazz)) {
			IBlockMetaSerializable meta;
			try {
				meta = ((IBlockMetaSerializable)clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			meta.readFromNBT(tag);
			return meta;
		}
		return null;
	}

}
