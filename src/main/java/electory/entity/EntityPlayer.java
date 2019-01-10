package electory.entity;

import static electory.math.MathUtils.deg2rad;

import java.io.IOException;

import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;

import electory.block.Block;
import electory.nbt.CompoundTag;
import electory.world.World;

public class EntityPlayer extends EntityLiving {

	public EntityPlayer(World world) {
		super(world);
	}
	
	public Block selectedBlock = Block.blockCobblestone;

	@Override
	public void update() {
		Matrix4x3f movementMatrix = new Matrix4x3f();

		movementMatrix.rotate((float) deg2rad(-yaw), 0.0f, 1.0f, 0.0f);

		Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);
		
		float movementSpeed = isUnderwater ? 0.25f : (onGround ? 0.3f : 0.2f);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			movementVector.add(0.0f, 0.0f, movementSpeed);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			movementVector.add(-movementSpeed, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			movementVector.add(movementSpeed, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			movementVector.add(0.0f, 0.0f, -movementSpeed);
		}
		
		movementMatrix.transformDirection(movementVector);
		
		moveClipped(movementVector.x, movementVector.y, movementVector.z);
		
		super.update();
	}
	
	public float getEyeHeight() {
		return 1.5f;
	}
	
	public boolean canBlockPlacedInto() {
		return false;
	}
	
	public boolean isHeadUnderwater() {
		Block block = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + getEyeHeight()), (int) Math.floor(z));
		return block == null ? false : block.isLiquid();
	}
	
	@Override
	public void writeEntityData(CompoundTag tag) throws IOException {
		super.writeEntityData(tag);
		tag.putInt("selectedBlock", selectedBlock.blockID);
	}
	
	@Override
	public void readEntityData(CompoundTag tag) throws IOException {
		super.readEntityData(tag);
		selectedBlock = Block.blockList[tag.getInt("selectedBlock")];
	}

	@Override
	public Vector3f getAcceleration() {
		return super.getAcceleration().add(0f, Keyboard.isKeyDown(Keyboard.KEY_SPACE) && (onGround || isUnderwater) ? (isUnderwater ? 0.006f : 0.512f) : 0f, 0f);
	}

}
