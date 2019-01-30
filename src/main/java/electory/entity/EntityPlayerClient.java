package electory.entity;

import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;

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
								&& Keyboard.isKeyDown(Keyboard.KEY_SPACE)
								&& (onGround || isUnderwater) ? (isUnderwater ? 0.006f : 0.512f) : 0f,
						0f);
	}
}
