package electory.network.packet;

import java.io.IOException;

import electory.server.ElectoryServer;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

public class S00ServerHandshake extends Packet {

	@Override
	public void writeToPacketBuffer(ArrayDataOutput ado) throws IOException {
		ado.writeUTF("Electory server version " + ElectoryServer.getVersion());
	}

	@Override
	public void readFromPacketBuffer(ArrayDataInput adi) throws IOException {
		adi.readUTF();
	}

}
