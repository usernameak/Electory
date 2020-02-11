package electory.client;

import static electory.math.MathUtils.deg2rad;

import org.joml.AxisAngle4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import electory.block.Block;
import electory.entity.EntityPlayer;
import electory.entity.IPlayerController;
import electory.math.Ray;
import electory.math.Ray.AABBIntersectionResult;
import electory.utils.EnumSide;

public class PlayerControllerClient implements IPlayerController {

	@Override
	public void doMovement(EntityPlayer player, Vector3f movementVector) {
		if(TinyCraft.getInstance().currentGui != null) {
			return;
		}
		
		Matrix4x3f movementMatrix = new Matrix4x3f();

		movementMatrix.rotate((float) deg2rad(-player.yaw), 0.0f, 1.0f, 0.0f);

		// float movementSpeed = player.isUnderwater ? 0.25f : (player.onGround ? 0.3f : 0.2f);

		if (GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			movementVector.add(0.0f, 0.0f, 1.0f);
		}

		if (GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			movementVector.add(-1.0f, 0.0f, 0.0f);
		}

		if (GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			movementVector.add(1.0f, 0.0f, 0.0f);
		}

		if (GLFW.glfwGetKey(TinyCraft.getInstance().window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			movementVector.add(0.0f, 0.0f, -1.0f);
		}

		if (movementVector.lengthSquared() != 0f) {
			movementVector.normalize();//.mul(movementSpeed);
		}

		movementMatrix.transformDirection(movementVector);
	}

	@Override
	public void processMouseEvent(EntityPlayer player, MouseEvent event) {
		player.yaw += event.getDX() * 0.1f;
		player.pitch += -event.getDY() * 0.1f;
		if (player.pitch > 90f) {
			player.pitch = 90f;
		} else if (player.pitch < -90f) {
			player.pitch = -90f;
		}
		if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT || event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			if (event.getButtonState()) {
				Vector3d pos = player.getInterpolatedPosition(TinyCraft.getInstance().tickTimer.renderPartialTicks);
				pos.add(0f, player.getEyeHeight(), 0f);

				Ray ray = new Ray(new Vector3d(pos.x, pos.y, pos.z), new Vector3d(0f, 0f, 1f))
						.rotate(new AxisAngle4f((float) deg2rad(-player.pitch), 1f, 0f, 0f))
						.rotate(new AxisAngle4f((float) deg2rad(-player.yaw), 0f, 1f, 0f));

				int x1 = (int) Math.floor(pos.x - 5);
				int y1 = (int) Math.floor(pos.y - 5);
				int z1 = (int) Math.floor(pos.z - 5);
				int x2 = (int) Math.ceil(pos.x + 5);
				int y2 = (int) Math.ceil(pos.y + 5);
				int z2 = (int) Math.ceil(pos.z + 5);

				int tx = 0, ty = 0, tz = 0;
				AABBIntersectionResult tres = new AABBIntersectionResult(false, Float.MAX_VALUE, EnumSide.UNKNOWN);

				for (int x = x1; x <= x2; x++) {
					for (int y = y1; y <= y2; y++) {
						for (int z = z1; z <= z2; z++) {
							Block block = player.world.getBlockAt(x, y, z);
							if (block != null && !block.isLiquid()) {
								AABBIntersectionResult res = ray
										.intersectsAABB(block.getAABB(player.world, x, y, z, false));
								if (res.hasHit && res.distance < tres.distance) {
									tres = res;
									tx = x;
									ty = y;
									tz = z;
								}
							}
						}
					}
				}

				if (tres.hasHit && tres.distance <= 5.0f) {
					if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && player.world.getBlockAt(tx, ty, tz).isBreakable()) {
						player.world.breakBlockByPlayer(player, tx, ty, tz);
					} else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
						player.world.interactWithBlock(player, tx, ty, tz, tres.side);
					}
				}
			}
		}
	}

}
