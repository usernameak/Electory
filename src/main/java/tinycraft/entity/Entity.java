package tinycraft.entity;

import java.util.Set;

import org.joml.Vector3f;

import tinycraft.math.AABB;
import tinycraft.world.World;

public abstract class Entity {
	public float x;
	public float y;
	public float z;

	protected Vector3f velocity = new Vector3f();

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
			velocity.y = 0f;
		}
		//System.out.println("1: " + onGround);
		Vector3f affectedVel = getAcceleration();
		velocity.add(affectedVel);
		moveClipped(velocity.x, velocity.y, velocity.z);

		//System.out.println("2: " + onGround);
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
			if(gofs >= -0.0025f) {
				onGround = true;
			}
		}

		x += xofs;
		y += yofs;
		z += zofs;
	}

	public AABB getAABB() {
		return new AABB(x - 0.5f, y, z - 0.5f, x + 0.5f, y + 2.0f, z + 0.5f);
	}
}
