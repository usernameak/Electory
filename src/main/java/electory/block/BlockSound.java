package electory.block;

import org.luaj.vm2.LuaValue;

import electory.world.World;

public class BlockSound {
	private String path;

	public BlockSound(String path) {
		this.path = path;
	}
	
	public BlockSound(LuaValue luaValue) {
		if(luaValue.isstring()) {
			this.path = luaValue.checkjstring();
		}
	}
	
	public void play(World world, int x, int y, int z) {
		world.playSFX(this.path, x + 0.5f, y + 0.5f, z + 0.5f, 10.0f, false);
	}
}
