package electory.entity;

import electory.math.AABB;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;
import electory.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Set;

public abstract class Entity {
	protected double oldX;
	protected double oldY;
	protected double oldZ;
	protected double x;
	protected double y;
	protected double z;
	protected double newX;
	protected double newY;
	protected double newZ;

	protected Vector3f velocity = new Vector3f();

	public boolean onGround = false;
	public boolean onCeiling = false;
	public boolean isUnderwater = false;

	public boolean noclip = false;

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

		if(hasGravity()) {
			velocity.y -= 0.0981f;
		}
/*
		if (onGround && velocity.y < 0) {
			velocity.y = 0f;
		}
		if (onCeiling && velocity.y > 0) {
			velocity.y = 0f;
		}*/
		if (isUnderwater) {
			velocity.y *= 0.6f;
		}
		if (isUnderwater) {
			velocity.x *= 0.27f;
			velocity.z *= 0.27f;
		} else if (onGround) {
			velocity.x *= 0.37f;
			velocity.z *= 0.37f;
		} else {
			velocity.x *= 0.43f;
			velocity.z *= 0.43f;
		}
		
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

	public void moveClipped(double xofs, double yofs, double zofs) {
		if (!world.chunkProvider.isChunkLoaded(((int) newX) >> 4, ((int) newZ) >> 4)) {
			return;
		}

		double origXOfs = xofs;
		double origYOfs = yofs;
		double origZOfs = zofs;

		AABB paabb = getAABB();

		if (!noclip) {
			AABB taabb = getAABB().expand(xofs, yofs, zofs);
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
		} else {
			paabb.move(xofs, yofs, zofs);
		}
		
		if(origYOfs != 0) {
			// System.out.println("gravitating");
			boolean collidedVertically = false;
			onGround = false;
			onCeiling = false;
			if(yofs != origYOfs) {
				collidedVertically = true;
				if(origYOfs < 0) {
					onGround = true;
				} else {
					onCeiling = true;
				}
			}
			if(collidedVertically) {
				velocity.y = 0;
			}
		}
		
		if(origXOfs != 0 || origZOfs != 0) {
			boolean collidedHorizontally = false;
			if(xofs != origXOfs || zofs != origZOfs) {
				collidedHorizontally = true;
			}
			if(collidedHorizontally) {
				velocity.x = 0;
				velocity.z = 0;
			}
		}

		/*
		 * { AABB gaabb = paabb.expand(0f, 0.01f, 0f); float gofs = 0.005f; Set<AABB>
		 * gAABBs = world.getBlockAABBsWithinAABB(gaabb, true);
		 * 
		 * onCeiling = false;
		 * 
		 * for (AABB aabb : gAABBs) { gofs = aabb.clipYCollide(paabb, gofs); if (gofs >=
		 * 0.0025f) { onCeiling = true; } } }
		 */

		newX += xofs;
		newY += yofs;
		newZ += zofs;
	}

	public AABB getAABB() {
		return new AABB(newX - xSizeHalf, newY, newZ - zSizeHalf, newX + xSizeHalf, newY + ySize, newZ + zSizeHalf);
	}

	public void setPosition(double x, double y, double z, boolean interpolate) {
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

	public Vector3d getInterpolatedPosition(float renderPartialTicks) {
		return new Vector3d(oldX, oldY, oldZ).lerp(new Vector3d(x, y, z), renderPartialTicks);
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void writeEntityData(ArrayDataOutput tag) throws IOException {
		tag.writeDouble(newX);
		tag.writeDouble(newY);
		tag.writeDouble(newZ);
		tag.writeFloat(velocity.x);
		tag.writeFloat(velocity.y);
		tag.writeFloat(velocity.z);
		tag.writeBoolean(onGround);
		tag.writeBoolean(shouldDespawn);
		tag.writeBoolean(onCeiling);
		tag.writeBoolean(noclip);
	}

	public boolean canBlockPlacedInto() {
		return true;
	}

	public boolean isPersistent() {
		return true;
	}

	public void readEntityData(ArrayDataInput tag) throws IOException {
		newX = x = oldX = tag.readDouble();
		newY = y = oldY = tag.readDouble();
		newZ = z = oldZ = tag.readDouble();
		velocity.x = tag.readFloat();
		velocity.y = tag.readFloat();
		velocity.z = tag.readFloat();
		onGround = tag.readBoolean();
		shouldDespawn = tag.readBoolean();
		onCeiling = tag.readBoolean();
		noclip = tag.readBoolean();
	}
}
