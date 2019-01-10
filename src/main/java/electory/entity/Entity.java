package electory.entity;

import java.io.IOException;
import java.util.Set;

import org.joml.Vector3f;

import electory.math.AABB;
import electory.nbt.CompoundTag;
import electory.world.World;

public abstract class Entity {
	protected float oldX;
	protected float oldY;
	protected float oldZ;
	protected float x;
	protected float y;
	protected float z;
	protected float newX;
	protected float newY;
	protected float newZ;

	protected Vector3f velocity = new Vector3f();

	public boolean onGround = false;
	public boolean isUnderwater = false;

	public final World world;

	@SuppressWarnings("unused") // TODO: use
	private float xSize, ySize, zSize, xSizeHalf, ySizeHalf, zSizeHalf;

	private boolean shouldDespawn = false;

	public Entity(World world) {
		this.world = world;
		setSize(0.75f, 1.75f, 0.75f);
	}

	public boolean hasGravity() {
		return true;
	}

	public void scheduleDespawn() {
		shouldDespawn = true;
	}

	public boolean shouldDespawn() {
		return shouldDespawn;
	}

	protected void setSize(float x, float y, float z) {
		this.xSize = x;
		this.xSizeHalf = x / 2;
		this.ySize = y;
		this.ySizeHalf = y / 2;
		this.zSize = z;
		this.zSizeHalf = z / 2;
	}

	public void update() {
		boolean wasUnderwater = isUnderwater;

		isUnderwater = world.isAABBWithinLiquid(getAABB());

		if (onGround) {
			velocity.y = 0f;
		}
		if (!wasUnderwater && isUnderwater) {
			velocity.y *= 0.05f;
		}

		Vector3f affectedVel = getAcceleration();
		velocity.add(affectedVel);
		moveClipped(velocity.x, velocity.y, velocity.z);
	}

	public void postUpdate() {
		oldX = x;
		oldY = y;
		oldZ = z;
		x = newX;
		y = newY;
		z = newZ;
	}

	public void setVelocity(float x, float y, float z) {
		velocity.set(x, y, z);
	}

	public Vector3f getAcceleration() {
		return new Vector3f(0f, (hasGravity() && !onGround) ? (isUnderwater ? -0.003f : -0.0981f) : 0f, 0f);
	}

	public void moveClipped(float xofs, float yofs, float zofs) {
		AABB taabb = getAABB().expand(xofs, yofs, zofs);
		AABB paabb = getAABB();

		Set<AABB> blockAABBs = world.getBlockAABBsWithinAABB(taabb, true);

		for (AABB aabb : blockAABBs) {
			yofs = aabb.clipYCollide(paabb, yofs);

		}

		paabb.move(0, yofs, 0);

		for (AABB aabb : blockAABBs) {
			xofs = aabb.clipXCollide(paabb, xofs);
		}

		paabb.move(xofs, 0, 0);

		for (AABB aabb : blockAABBs) {
			zofs = aabb.clipZCollide(paabb, zofs);
		}

		paabb.move(0, 0, zofs);

		AABB gaabb = paabb.expand(0f, -0.01f, 0f);
		float gofs = -0.005f;
		Set<AABB> gAABBs = world.getBlockAABBsWithinAABB(gaabb, true);

		onGround = false;

		for (AABB aabb : gAABBs) {
			gofs = aabb.clipYCollide(paabb, gofs);
			if (gofs >= -0.0025f) {
				onGround = true;
			}
		}

		newX += xofs;
		newY += yofs;
		newZ += zofs;
	}

	public AABB getAABB() {
		return new AABB(newX - xSizeHalf, newY, newZ - zSizeHalf, newX + xSizeHalf, newY + ySize, newZ + zSizeHalf);
	}

	public void setPosition(float x, float y, float z, boolean interpolate) {
		if (interpolate) {
			newX = x;
			newY = y;
			newZ = z;
		} else {
			this.x = this.newX = this.oldX = x;
			this.y = this.newY = this.oldY = y;
			this.z = this.newZ = this.oldZ = z;
		}
	}

	public Vector3f getInterpolatedPosition(float renderPartialTicks) {
		return new Vector3f(oldX, oldY, oldZ).lerp(new Vector3f(x, y, z), renderPartialTicks);
	}

	public void writeEntityData(CompoundTag tag) throws IOException {
		tag.putFloat("x", newX);
		tag.putFloat("y", newY);
		tag.putFloat("z", newZ);
		tag.putFloat("vx", velocity.x);
		tag.putFloat("vy", velocity.y);
		tag.putFloat("vz", velocity.z);
		tag.putBoolean("onGround", onGround);
		tag.putBoolean("shouldDespawn", shouldDespawn);

	}
	
	public boolean canBlockPlacedInto() {
		return true;
	}

	public boolean isPersistent() {
		return true;
	}

	public void readEntityData(CompoundTag tag) throws IOException {
		newX = x = oldX = tag.getFloat("x");
		newY = y = oldY = tag.getFloat("y");
		newZ = z = oldZ = tag.getFloat("z");
		velocity.x = tag.getFloat("vx");
		velocity.y = tag.getFloat("vy");
		velocity.z = tag.getFloat("vz");
		onGround = tag.getBoolean("onGround");
		shouldDespawn = tag.getBoolean("shouldDespawn");
	}
}
