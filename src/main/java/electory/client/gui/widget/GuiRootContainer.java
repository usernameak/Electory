package electory.client.gui.widget;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.opengl.GL11;

import electory.client.KeyEvent;
import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.ShaderManager;

public class GuiRootContainer extends GuiWidget implements IRelayoutable {

	private GuiWidget child;

	public Color bgColor = new Color(0xC0000000, true);
	public Position position = Position.TOP_LEFT;
	public int horizontalGap = 0;
	public int verticalGap = 0;

	public static enum Position {
		TOP_LEFT {
			@Override
			public void align(GuiRenderState rs, ResolutionScaler scaler, GuiRootContainer container) {
				rs.modelMatrix.translate(container.horizontalGap, container.verticalGap, 0);
			}

			@Override
			public Point getPosition(ResolutionScaler scaler, GuiRootContainer container) {
				return new Point(container.horizontalGap, container.verticalGap);
			}
		},
		BOTTOM_LEFT {
			@Override
			public void align(GuiRenderState rs, ResolutionScaler scaler, GuiRootContainer container) {
				rs.modelMatrix.translate(	container.horizontalGap,
											scaler.getHeight() - container.child.getHeight() - container.verticalGap,
											0);
			}

			@Override
			public Point getPosition(ResolutionScaler scaler, GuiRootContainer container) {
				return new Point(container.horizontalGap,
						scaler.getHeight() - container.child.getHeight() - container.verticalGap);
			}
		},
		BOTTOM_RIGHT {
			@Override
			public void align(GuiRenderState rs, ResolutionScaler scaler, GuiRootContainer container) {
				rs.modelMatrix.translate(	scaler.getWidth() - container.child.getWidth() - container.horizontalGap,
											scaler.getHeight() - container.child.getHeight() - container.verticalGap,
											0);
			}

			@Override
			public Point getPosition(ResolutionScaler scaler, GuiRootContainer container) {
				return new Point(scaler.getWidth() - container.child.getWidth() - container.horizontalGap,
						scaler.getHeight() - container.child.getHeight() - container.verticalGap);
			}
		},
		CENTER {
			@Override
			public void align(GuiRenderState rs, ResolutionScaler scaler, GuiRootContainer container) {
				rs.modelMatrix.translate(	(scaler.getWidth() - container.child.getWidth()) / 2,
											(scaler.getHeight() - container.child.getHeight()) / 2,
											0);
			}

			@Override
			public Point getPosition(ResolutionScaler scaler, GuiRootContainer container) {
				return new Point((scaler.getWidth() - container.child.getWidth()) / 2,
						(scaler.getHeight() - container.child.getHeight()) / 2);
			}

		};

		public abstract void align(GuiRenderState rs, ResolutionScaler scaler, GuiRootContainer container);

		public abstract Point getPosition(ResolutionScaler scaler, GuiRootContainer container);
	};

	public GuiRootContainer(TinyCraft tc, GuiWidget child) {
		super(tc);
		this.child = child;
	}

	@Override
	public int getWidth() {
		return tc.resolutionScaler.getWidth();
	}

	@Override
	public int getHeight() {
		return tc.resolutionScaler.getHeight();
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		if (bgColor != null) {
			ShaderManager.solidProgram.use();
			ShaderManager.solidProgram.loadRenderState(rs);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);
			TriangleBuffer tb = Tessellator.instance.getBuffer();
			tb.setColor(bgColor.getRGB());
			tb.addQuadVertex(0, 0, 0);
			tb.addQuadVertex(0, getHeight(), 0);
			tb.addQuadVertex(getWidth(), getHeight(), 0);
			tb.addQuadVertex(getWidth(), 0, 0);
			Tessellator.instance.draw();
			GL11.glDisable(GL11.GL_BLEND);
			tb.setColor(0xFFFFFFFF);
		}

		GuiRenderState rs2 = new GuiRenderState(rs);

		position.align(rs2, tc.resolutionScaler, this);

		child.renderGui(rs2);
	}

	@Override
	public void handleKeyEvent(KeyEvent event) {
		super.handleKeyEvent(event);
		child.handleKeyEvent(event);
	}

	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		Point point = position.getPosition(tc.resolutionScaler, this);
		child.handleMouseEvent(event.clipEvent(point.x, point.y, child.getWidth(), child.getHeight()));
	}

	@Override
	public void relayout(int width, int height) {
		// child.relayout();
	}
}
