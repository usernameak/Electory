package electory.entity;

import java.io.IOException;

import electory.nbt.CompoundTag;
import electory.world.World;

public abstract class EntityLiving extends Entity {
	public float pitch;
	public float yaw;
	
	public EntityLiving(World world) {
		super(world);
	}
	
	@Override
	public void writeEntityData(CompoundTag tag) throws IOException {
		super.writeEntityData(tag);
		tag.putFloat("pitch", pitch);
		tag.putFloat("yaw", yaw);
	}
	
	@Override
	public void readEntityData(CompoundTag tag) throws IOException {
		super.readEntityData(tag);
		pitch = tag.getFloat("pitch");
		yaw = tag.getFloat("yaw");
	}
	
}
