package electory.network.packet;

import java.io.IOException;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

public abstract class Packet {
	private static IntObjMap<Class<? extends Packet>> registeredPackets = HashIntObjMaps.newMutableMap();
	private static ObjIntMap<Class<? extends Packet>> registeredPacketIDs = HashObjIntMaps.newMutableMap();

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
