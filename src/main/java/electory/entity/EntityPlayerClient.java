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
	public void update() {
		if(TinyCraft.getInstance().currentGui == null) {
			if(GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
				if(onGround || isUnderwater || fly) {
					velocity.y += isUnderwater ? 0.128f : 0.512f; 
				}
			}
		}
		if(fly) {
			velocity.y *= 0.5f;
		}
		super.update();
	}
}
