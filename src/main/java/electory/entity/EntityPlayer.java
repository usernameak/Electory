package electory.entity;

import electory.block.Block;
import electory.inventory.IContainerProvider;
import electory.inventory.InventoryPlayer;
import electory.item.ItemStack;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;
import electory.world.World;
import org.joml.Vector3f;

import java.io.IOException;

public abstract class EntityPlayer extends EntityLiving {

	public EntityPlayer(World world) {
		super(world);
	}
	
	// public Block selectedBlock = Block.blockCobblestone;
	
	public InventoryPlayer inventory = new InventoryPlayer(this);
	
	public IContainerProvider inventoryContainer = inventory.createSPProvider();
	
	public transient IPlayerController playerController = null;

	public ItemStack stackOnCursor = new ItemStack();

	public boolean fly = false;
	
	@Override
	public void update() {
		
		Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);
		
		if(playerController != null) {
			playerController.doMovement(this, movementVector);
		}
		
		movementVector.mul(onGround ? 0.5f : 0.3f);
		
		// moveClipped(movementVector.x, movementVector.y, movementVector.z);
		this.velocity.add(movementVector);
		
		super.update();
	}
	
	@Override
	public boolean hasGravity() {
		return !fly;
	}
	
	public float getEyeHeight() {
		return 1.5f;
	}
	
	public boolean canBlockPlacedInto() {
		return false;
	}
	
	public boolean isHeadUnderwater() {
		Block block = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + getEyeHeight()), (int) Math.floor(z));
		return block != null && block.isLiquid();
	}
	
	@Override
	public void writeEntityData(ArrayDataOutput tag) throws IOException {
		super.writeEntityData(tag);
		inventory.writeToNBT(tag);
	}
	
	@Override
	public void readEntityData(ArrayDataInput tag) throws IOException {
		super.readEntityData(tag);
		inventory.readFromNBT(tag);
	}

}
