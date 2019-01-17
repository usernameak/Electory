package electory.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import electory.client.TinyCraft;
import electory.entity.EntityPlayer;
import electory.world.WorldServer;

public class ElectoryServer {
	private static ElectoryServer instance;

	private static String version = "version unknown";
	
	private Map<ServerNetworkHandler, EntityPlayer> players = new HashMap<>();

	static {
		BufferedReader isr = new BufferedReader(
				new InputStreamReader(TinyCraft.class.getResourceAsStream("/version.def")));
		try {
			version = isr.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean shutdown = false;
	public WorldServer world = new WorldServer();
	
	private ServerSocket serverSocket;
	private ServerSocketWaitThread serverSocketWaitThread;

	public static ElectoryServer getInstance() {
		return instance == null ? (instance = new ElectoryServer()) : instance;
	}
	
	public Collection<EntityPlayer> getPlayers() {
		return players.values();
	}

	public void shutdown() {
		shutdown = true;
	}

	public static void main(String[] args) throws IOException {
		getInstance().start();
	}
	
	public void acceptConnection(Socket socket) throws IOException {
		System.out.println("Accepting connection from " + socket.getInetAddress() + ":" + socket.getPort());
		ServerNetworkHandler netHandler = new ServerNetworkHandler(socket);
		netHandler.initialize();
		// TODO:
	}

	public void start() throws IOException {
		System.out.println("Electory server version " + version + " is starting...");
		
		
		initGame();
		
		serverSocket = new ServerSocket(7034);
		serverSocketWaitThread = new ServerSocketWaitThread(serverSocket);
		serverSocketWaitThread.start();

		while (!shutdown) {
			update();
		}
		
		serverSocket.close();

		world.unload();
	}
	
	public static String getVersion() {
		return version;
	}

	public void update() {
		world.update();
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void initGame() {
		try {
			world.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
