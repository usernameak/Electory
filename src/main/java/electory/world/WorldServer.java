package electory.world;

import java.util.Collection;

import electory.entity.EntityPlayer;
import electory.entity.EntityPlayerServer;
import electory.server.ElectoryServer;

public class WorldServer extends World {

	@Override
	protected Collection<EntityPlayer> getPlayers() {
		return ElectoryServer.getInstance().getPlayers();
	}

	@Override
	protected boolean checkSpawnAreaLoaded() {
		return true;
	}

	@Override
	protected EntityPlayer constructPlayer() {
		return new EntityPlayerServer(this);
	}

}
