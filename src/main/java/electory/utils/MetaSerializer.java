package electory.utils;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

import electory.nbt.CompoundTag;
import electory.obf.Keep;

public abstract class MetaSerializer {
	@Keep
	@FunctionalInterface
	public interface ConstructorSerializer {
		IMetaSerializable get();
	}
	private static final MethodType INVOKED = MethodType.methodType(IMetaSerializable.class);
	private static final MethodType NOARGS_CONSTRUCTOR = MethodType.methodType(void.class);
	private static final ClassValue<Optional<ConstructorSerializer>> SERIALIZABLE_GETTER = new ClassValue<Optional<ConstructorSerializer>>() {
		@Override
	    protected Optional<ConstructorSerializer> computeValue(Class<?> type) {
			try {
				MethodHandle construtor = MethodHandles.publicLookup().findConstructor(type, NOARGS_CONSTRUCTOR);
				return Optional.of(((ConstructorSerializer)LambdaMetafactory.metafactory(MethodHandles.publicLookup(), "get", INVOKED, construtor.type(), construtor, construtor.type()).getTarget().invokeExact()));
			} catch (Throwable e) { }
			return Optional.empty();
		}
	};
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
			Optional<IMetaSerializable> meta = SERIALIZABLE_GETTER.get(clazz).map(e -> e.get());
			meta.ifPresent(e -> e.readFromNBT(tag));
			return meta.orElse(null);
		}
		return null;
	}

}
