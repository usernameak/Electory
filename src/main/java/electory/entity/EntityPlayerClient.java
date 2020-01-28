package electory.entity;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import electory.client.TinyCraft;
import electory.world.World;

public class EntityPlayerClient extends EntityPlayer {

	public EntityPlayerClient(World world) {
		super(world);
	}

	@Override
	public Vector3f getAcceleration() {
		return super.getAcceleration()
				.add(	0f,
						TinyCraft.getInstance().currentGui == null
								&& GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS
								&& (onGround || isUnderwater) ? (isUnderwater ? 0.128f : 0.512f) : 0f,
						0f);
	}
}
