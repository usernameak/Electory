package electory.block;

import org.luaj.vm2.LuaValue;

public class LuaBlock extends Block {
	public LuaBlock(LuaValue data) {
		super(data.get("sprite_name").get("unit_id").toint(), data.get("unit_subid").toint());
		LuaValue spriteName = data.get("sprite_name");
		if (!spriteName.isnil()) {
			setSpriteName(spriteName.tojstring());
		}
		LuaValue solid = data.get("solid");
		if(!solid.isnil()) {
			setSolid(solid.toboolean());
		}
		LuaValue breakable = data.get("breakable");
		if(!breakable.isnil()) {
			setBreakable(breakable.toboolean());
		}
		LuaValue sound = data.get("sound");
		if(!sound.isnil()) {
			setSound(new BlockSound(sound));
		}
	}
}
