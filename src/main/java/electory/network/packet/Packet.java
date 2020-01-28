package electory.network.packet;

import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Supplier;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

import electory.obf.Keep;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

public abstract class Packet {
	private static IntObjMap<Class<? extends Packet>> registeredPackets = HashIntObjMaps.newMutableMap();
	private static ObjIntMap<Class<? extends Packet>> registeredPacketIDs = HashObjIntMaps.newMutableMap();

	public static int getPacketId(Class<? extends Packet> packet) {
		return registeredPacketIDs.getInt(packet);
	}
	@Keep
	@FunctionalInterface
	public interface ConstructorSerializer extends Supplier<Packet> {
		Packet get();
	}
	private static final MethodType INVOKED = MethodType.methodType(Packet.class);
	private static final MethodType NOARGS_CONSTRUCTOR = MethodType.methodType(void.class);
	private static final ClassValue<Optional<ConstructorSerializer>> PACKET_GETTER = new ClassValue<Optional<ConstructorSerializer>>() {
		@Override
	    protected Optional<ConstructorSerializer> computeValue(Class<?> type) {
			try {
				MethodHandle construtor = MethodHandles.publicLookup().findConstructor(type, NOARGS_CONSTRUCTOR);
				return Optional.of(((ConstructorSerializer)LambdaMetafactory.metafactory(MethodHandles.publicLookup(), "get", INVOKED, construtor.type(), construtor, construtor.type()).getTarget().invokeExact()));
			} catch (Throwable e) { }
			return Optional.empty();
		}
	};

	public static Supplier<Packet> getPacketSupplierById(int id) {
		return PACKET_GETTER.get(registeredPackets.get(id)).orElse(null);
	}

	public static Class<? extends Packet> getPacketById(int id) {
		return registeredPackets.get(id);
	}

	protected static void registerPacket(int packetId, Class<? extends Packet> packet) {
		registeredPackets.put(packetId, packet);
		registeredPacketIDs.put(packet, packetId);
	}

	public abstract void writeToPacketBuffer(ArrayDataOutput ado) throws IOException;
	public abstract void readFromPacketBuffer(ArrayDataInput adi) throws IOException;

	static {
		registerPacket(0, S00ServerHandshake.class);
	}
}
