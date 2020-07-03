package electory.entity;

import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;
import electory.world.World;

import java.io.IOException;

public abstract class EntityLiving extends Entity {
	public float pitch;
	public float yaw;
	
	public EntityLiving(World world) {
		super(world);
	}
	
	@Override
	public void writeEntityData(ArrayDataOutput tag) throws IOException {
		super.writeEntityData(tag);
		tag.writeFloat(pitch);
		tag.writeFloat(yaw);
	}
	
	@Override
	public void readEntityData(ArrayDataInput tag) throws IOException {
		super.readEntityData(tag);

		pitch = tag.readFloat();
		yaw = tag.readFloat();
	}
	
}
