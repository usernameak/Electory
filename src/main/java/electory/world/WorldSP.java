package electory.world;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.joml.Vector3d;

import electory.client.TinyCraft;
import electory.entity.EntityPlayer;
import electory.entity.EntityPlayerClient;

public class WorldSP extends World {
	private String worldName;

	public WorldSP(String worldName) {
		super();
		this.worldName = worldName;
	}

	@Override
	protected Collection<EntityPlayer> getPlayers() {
		return Collections.singleton(TinyCraft.getInstance().player);
	}

	@Override
	protected boolean checkSpawnAreaLoaded() {
		if (TinyCraft.getInstance().player == null) {
			if (playerToSpawn == null) {
				if (chunkProvider.isChunkLoaded((int) Math.floor(spawnPoint.x) >> 4,
												(int) Math.floor(spawnPoint.z) >> 4)) {
					EntityPlayer player = new EntityPlayerClient(this);
					player.setPosition(	spawnPoint.x + 0.5f,
										getHeightAt((int) Math.floor(spawnPoint.x), (int) Math.floor(spawnPoint.z))
												+ 1.0f,
										spawnPoint.z + 0.5f,
										false);
					addEntity(player);
					TinyCraft.getInstance().setPlayer(player);
					return true;
				}
			} else {
				Vector3d spawnPos = playerToSpawn.getInterpolatedPosition(1.0f);
				if (chunkProvider.isChunkLoaded((int) Math.floor(spawnPos.x) >> 4, (int) Math.floor(spawnPos.z) >> 4)) {
					TinyCraft.getInstance().setPlayer(playerToSpawn);
					addEntity(playerToSpawn);
					playerToSpawn = null;
					return true;
				}
			}
		}
		return TinyCraft.getInstance().player != null;
	}
	
	@Override
	public File getWorldSaveDir() {
		File dir = new File(TinyCraft.getInstance().getUserDataDir(), "universes/" + worldName);
		dir.mkdirs();
		return dir;
	}

	@Override
	protected EntityPlayer constructPlayer() {
		return new EntityPlayerClient(this);
	}
}
