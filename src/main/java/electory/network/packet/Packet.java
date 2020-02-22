package electory.network.packet;

import java.io.IOException;

import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public abstract class Packet {
	private static Int2ObjectMap<Class<? extends Packet>> registeredPackets = new Int2ObjectOpenHashMap<>();
	private static Object2IntMap<Class<? extends Packet>> registeredPacketIDs = new Object2IntOpenHashMap<>();

	public static int getPacketId(Class<? extends Packet> packet) {
		return registeredPacketIDs.getInt(packet);
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
