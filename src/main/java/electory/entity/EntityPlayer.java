package electory.entity;

import java.io.IOException;

import org.joml.Vector3f;

import electory.block.Block;
import electory.inventory.IContainerProvider;
import electory.inventory.InventoryPlayer;
import electory.nbt.CompoundTag;
import electory.world.World;

public abstract class EntityPlayer extends EntityLiving {

	public EntityPlayer(World world) {
		super(world);
	}
	
	// public Block selectedBlock = Block.blockCobblestone;
	
	public InventoryPlayer inventory = new InventoryPlayer(this);
	
	public IContainerProvider inventoryContainer = inventory.createSPProvider();
	
	public transient IPlayerController playerController = null;

	@Override
	public void update() {
		Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);
		
		if(playerController != null) {
			playerController.doMovement(this, movementVector);
		}
		
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
		CompoundTag invTag = new CompoundTag();
		inventory.writeToNBT(invTag);
		tag.put("inventory", invTag);
	}
	
	@Override
	public void readEntityData(CompoundTag tag) throws IOException {
		super.readEntityData(tag);
		inventory.readFromNBT(tag.getCompoundTag("inventory"));
	}

}
