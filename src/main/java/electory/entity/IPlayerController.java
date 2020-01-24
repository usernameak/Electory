package electory.entity;

import org.joml.Vector3f;

import electory.client.MouseEvent;

public interface IPlayerController {
	public void doMovement(EntityPlayer player, Vector3f movementVector);
	
	public void processMouseEvent(EntityPlayer player, MouseEvent event);
}
