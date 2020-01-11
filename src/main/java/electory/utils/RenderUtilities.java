package electory.utils;

import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.KHRDebug;

public class RenderUtilities {
	private static int debugGroupId = 0;
	
	public static void pushDebugGroup(String name) {
		if(GLContext.getCapabilities().GL_KHR_debug) {
			KHRDebug.glPushDebugGroup(KHRDebug.GL_DEBUG_SOURCE_APPLICATION, debugGroupId++, name);
		}
	}
	
	public static void popDebugGroup() {
		if(GLContext.getCapabilities().GL_KHR_debug) {
			KHRDebug.glPopDebugGroup();
		}
	}
}
