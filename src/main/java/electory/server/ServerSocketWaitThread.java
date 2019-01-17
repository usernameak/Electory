package electory.server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketWaitThread extends Thread {
	private ServerSocket socket;

	public ServerSocketWaitThread(ServerSocket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			while (true) {
				ElectoryServer.getInstance().acceptConnection(socket.accept());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
