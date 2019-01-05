package tinycraft.entity;

import java.util.Set;

import org.joml.Vector3f;

import tinycraft.math.AABB;
import tinycraft.world.World;

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
		if (onGround) {
			velocity.y = 0f;
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
		return new Vector3f(0f, (hasGravity() && !onGround) ? -0.00981f : 0f, 0f);
	}

	public void moveClipped(float xofs, float yofs, float zofs) {
		AABB taabb = getAABB().expand(xofs, yofs, zofs);
		AABB paabb = getAABB();

		Set<AABB> blockAABBs = world.getBlockAABBsWithinAABB(taabb);

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
		Set<AABB> gAABBs = world.getBlockAABBsWithinAABB(gaabb);

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
		return new AABB(x - xSizeHalf, y, z - zSizeHalf, x + xSizeHalf, y + ySize, z + zSizeHalf);
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
}
