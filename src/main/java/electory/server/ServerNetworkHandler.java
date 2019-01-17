package electory.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import electory.network.packet.Packet;
import electory.network.packet.S00ServerHandshake;
import electory.utils.io.BufferedDataInputStream;
import electory.utils.io.BufferedDataOutputStream;

public class ServerNetworkHandler {
	private Socket socket;
	private LinkedBlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Packet> recvQueue = new LinkedBlockingQueue<>();
	private BufferedDataOutputStream outputStream;
	private BufferedDataInputStream inputStream;
	
	private class PacketSendThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					Packet packet = sendQueue.take();
					outputStream.write(Packet.getPacketId(packet.getClass()));
					packet.writeToPacketBuffer(outputStream);
					outputStream.flush();
				} catch (InterruptedException e) {
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}	
	
	private class PacketReceiveThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					int packetId = inputStream.read();
					if(packetId < 0) {
						terminateConnection();
						return;
					}
					Packet packet = Packet.getPacketById(packetId).newInstance();
					packet.readFromPacketBuffer(inputStream);
					recvQueue.add(packet);
				} catch (IOException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	
	private PacketSendThread sendThread;
	private PacketReceiveThread recvThread;

	public ServerNetworkHandler(Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = new BufferedDataOutputStream(socket.getOutputStream());
		this.inputStream = new BufferedDataInputStream(socket.getInputStream());
	}
	
	public void terminateConnection() throws IOException {
		System.out.println("Connection from " + socket.getInetAddress() + ":" + socket.getPort() + " closed");
		socket.close();
		recvThread.interrupt();
		sendThread.interrupt();
	}

	public void initialize() {
		sendThread = new PacketSendThread();
		sendThread.start();
		recvThread = new PacketReceiveThread();
		recvThread.start();
		addPacketToSendQueue(new S00ServerHandshake());
	}
	
	public void addPacketToSendQueue(Packet packet) {
		sendQueue.add(packet);
	}
}
