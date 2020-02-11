package electory.scripting;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import electory.block.Block;
import electory.block.LuaBlock;
import electory.client.TinyCraft;
import electory.event.IEvent;
import electory.event.IEventHandler;

public class ElectoryScriptingFunctions extends LuaTable {
	public class get_world extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return CoerceJavaToLua.coerce(TinyCraft.getInstance().world);
		}
	}

	public class register_event_handler extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue name_, LuaValue callback) {
			String name = name_.checkjstring();
			LuaFunction func = callback.checkfunction();
			TinyCraft.getInstance().eventRegistry.getEventByName(name).registerHandler(new IEventHandler() {
				@Override
				public void invoke(IEvent event) {
					func.call(CoerceJavaToLua.coerce(event));
				}
			});
			return NIL;
		}
	}
	
	public class register_block extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue name_, LuaValue arg) {
			if(!arg.istable()) {
				throw new RuntimeException("electory.regiser_block() argument is not table");
			}
			Block.REGISTRY.register(name_.tojstring(), new LuaBlock(arg));
			return NIL;
		}
	}

	public ElectoryScriptingFunctions() {
		set("register_event_handler", new register_event_handler());
		set("register_block", new register_block());
		set("get_world", new get_world());
	}
}
