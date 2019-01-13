package electory.entity;

import org.joml.Vector3f;

public interface IPlayerController {
	public void doMovement(EntityPlayer player, Vector3f movementVector);
	
	public void processMouseEvent(EntityPlayer player);
}
