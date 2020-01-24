package electory.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

import electory.client.TinyCraft;

public class ScriptingEngine {
	private Globals globals;

	public ScriptingEngine() {
		TinyCraft.getInstance().logger.info(Lua._VERSION);
		this.globals = JsePlatform.standardGlobals();
		this.globals.set("electory", new ElectoryScriptingFunctions());
		LuaJC.install(this.globals);
	}
	
	public void runScript(String path) throws IOException {
		InputStream is = getClass().getResourceAsStream(path);
		InputStreamReader isr = new InputStreamReader(is);
		LuaValue chunk = globals.load(isr, path);
		chunk.call();
		isr.close();
	}
}
