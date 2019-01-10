package electory.client.render.shader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderManager {
	public static DefaultProgram defaultProgram;
	public static DefaultProgram waterProgram;
	public static TerrainProgram terrainProgram;
	public static CompositeProgram worldCompositeProgram;
	public static DefaultProgram whiteProgram;
	private static Map<String, Integer> compiledShaders = new HashMap<String, Integer>();
	public static DefaultProgram shadowTerrainProgram;

	public static void init() throws IOException, ShaderCompileException {
		defaultProgram = new DefaultProgram(compile("/shaders/default.vp", GL20.GL_VERTEX_SHADER),
				compile("/shaders/default.fp", GL20.GL_FRAGMENT_SHADER));
		shadowTerrainProgram = new DefaultProgram(compile("/shaders/default.vp", GL20.GL_VERTEX_SHADER),
		                    				compile("/shaders/shadow_terrain.fp", GL20.GL_FRAGMENT_SHADER));
		waterProgram = new DefaultProgram(compile("/shaders/default.vp", GL20.GL_VERTEX_SHADER),
				compile("/shaders/fragment_library.fp", GL20.GL_FRAGMENT_SHADER),
				compile("/shaders/default_water.fp", GL20.GL_FRAGMENT_SHADER));
		terrainProgram = new TerrainProgram(compile("/shaders/default_terrain.vp", GL20.GL_VERTEX_SHADER),
				compile("/shaders/default_terrain.fp", GL20.GL_FRAGMENT_SHADER));
		worldCompositeProgram = new CompositeProgram(compile("/shaders/default.vp", GL20.GL_VERTEX_SHADER),
				compile("/shaders/world_composite.fp", GL20.GL_FRAGMENT_SHADER),
				compile("/shaders/fragment_library.fp", GL20.GL_FRAGMENT_SHADER));
		whiteProgram = new DefaultProgram(compile("/shaders/default.vp", GL20.GL_VERTEX_SHADER),
		                    				compile("/shaders/default_white.fp", GL20.GL_FRAGMENT_SHADER));
	}

	public static int compile(String name, int type) throws ShaderCompileException, IOException {
		if (!compiledShaders.containsKey(name)) {
			int shader = GL20.glCreateShader(type);
			GL20.glShaderSource(shader,
								IOUtils.toString(ShaderManager.class.getResource(name), StandardCharsets.UTF_8));
			GL20.glCompileShader(shader);
			if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
				ShaderCompileException ex = new ShaderCompileException(
						GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH)));
				GL20.glDeleteShader(shader);
				throw ex;
			}
			compiledShaders.put(name, shader);
			return shader;
		}
		return compiledShaders.get(name);
	}
}
