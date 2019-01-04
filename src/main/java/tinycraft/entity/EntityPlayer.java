package tinycraft.entity;

import static tinycraft.math.MathUtils.deg2rad;

import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;

import tinycraft.world.World;

public class EntityPlayer extends EntityLiving {

	public EntityPlayer(World world) {
		super(world);
	}
	
	protected Vector3f movementForce;

	@Override
	public void update() {
		Matrix4x3f movementMatrix = new Matrix4x3f();

		movementMatrix.rotate((float) deg2rad(-yaw), 0.0f, 1.0f, 0.0f);

		Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			movementVector.add(0.0f, 0.0f, 0.3f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			movementVector.add(-0.3f, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			movementVector.add(0.3f, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			movementVector.add(0.0f, 0.0f, -0.3f);
		}
		
		movementMatrix.transformDirection(movementVector);
		
		moveClipped(movementVector.x, movementVector.y, movementVector.z);
		
		super.update();
	}

	@Override
	public Vector3f getAcceleration() {
		return super.getAcceleration().add(0f, Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround ? 0.256f : 0f, 0f);
	}

}
