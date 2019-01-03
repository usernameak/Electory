package tinycraft.entity;

import tinycraft.world.World;

public abstract class EntityLiving extends Entity {
	public float pitch;
	public float yaw;
	
	public EntityLiving(World world) {
		super(world);
	}
}
