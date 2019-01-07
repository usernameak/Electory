package tinycraft.entity;

import java.io.IOException;

import tinycraft.utils.io.ArrayDataInput;
import tinycraft.utils.io.ArrayDataOutput;
import tinycraft.world.World;

public abstract class EntityLiving extends Entity {
	public float pitch;
	public float yaw;
	
	public EntityLiving(World world) {
		super(world);
	}
	
	@Override
	public void writeEntityData(ArrayDataOutput dos) throws IOException {
		super.writeEntityData(dos);
		dos.writeFloat(pitch);
		dos.writeFloat(yaw);
	}
	
	@Override
	public void readEntityData(ArrayDataInput dis) throws IOException {
		super.readEntityData(dis);
		pitch = dis.readFloat();
		yaw = dis.readFloat();
	}
	
}
