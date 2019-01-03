package tinycraft.entity;

import tinycraft.math.AABB;
import tinycraft.world.World;

public abstract class Entity {
	public float x;
	public float y;
	public float z;

	public float velX, velY, velZ;

	public boolean onGround = false;

	public final World world;

	public Entity(World world) {
		this.world = world;
	}

	public boolean hasGravity() {
		return true;
	}

	public void update() {
		if (onGround) {
			velY = 0;
		}
		if (hasGravity() && !onGround) {
			velY -= 0.00981f;
		}
		moveClipped(velZ, velY, velX);
	}

	public void moveClipped(float xofs, float yofs, float zofs) {
		float origYOfs = yofs;

		AABB taabb = getAABB().expand(xofs, yofs, zofs);
		AABB paabb = getAABB();

		for (AABB aabb : world.getBlockAABBsWithinAABB(taabb)) {
			yofs = aabb.clipYCollide(paabb, yofs);

		}

		paabb.move(0, yofs, 0);

		for (AABB aabb : world.getBlockAABBsWithinAABB(taabb)) {
			xofs = aabb.clipXCollide(paabb, xofs);
		}

		paabb.move(xofs, 0, 0);

		for (AABB aabb : world.getBlockAABBsWithinAABB(taabb)) {
			zofs = aabb.clipZCollide(paabb, zofs);
		}

		paabb.move(0, 0, zofs);

		if (origYOfs <= 0f || onGround) {
			onGround = (origYOfs != yofs);
		}

		x += xofs;
		y += yofs;
		z += zofs;
	}

	public AABB getAABB() {
		return new AABB(x - 0.5f, y, z - 0.5f, x + 0.5f, y + 2.0f, z + 0.5f);
	}
}
