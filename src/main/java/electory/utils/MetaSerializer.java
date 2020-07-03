package electory.utils;

import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

import java.io.IOException;

public abstract class MetaSerializer {

	private MetaSerializer() {

	}

	public static void serializeObject(ArrayDataOutput tag, Object object) throws IOException {
		if(object == null) return;
		if (object instanceof IMetaSerializable) {
			IMetaSerializable serializable = (IMetaSerializable) object;
			serializable.writeToNBT(tag);
		} else if (object instanceof Enum) {
			tag.writeInt(((Enum<?>) object).ordinal());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object deserializeObject(ArrayDataInput tag, Class<?> clazz) throws IOException {
		if(clazz == null) return null;

		if (Enum.class.isAssignableFrom(clazz)) {
			return clazz.getEnumConstants()[tag.readInt()]; // TODO: optimize me
		} else if (IMetaSerializable.class.isAssignableFrom(clazz)) {
			IMetaSerializable meta;
			try {
				meta = ((IMetaSerializable)clazz.newInstance());
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
