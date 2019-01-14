package electory.client.gui;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;
import electory.utils.Rect2D;

public class FontRenderer {
	public static final int CHAR_HEIGHT = 8;
	public static final int CHAR_WIDTH = 8;

	private CharBuffer cb = CharBuffer.allocate(1);

	public int getTextWidth(String s) {
		return CHAR_WIDTH * s.length();
	}

	public void drawText(GuiRenderState rs, String s, int x, int y) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.loadRenderState(rs);
		ShaderManager.defaultProgram.bindTexture("/img/font/font.png");
		for (int i = 0; i < s.length(); i++) {
			char cc = s.charAt(i);
			int c = 0;
			if (cc >= 'А' && cc <= 'я') {
				c = 256 + (cc - 'А');
			} else {
				cb.clear();
				cb.put(cc);
				cb.flip();
				c = Charset.forName("Cp437").encode(cb).get() & 0xFF;
			}
			float cw = 1.f / 16.f;
			float ch = 1.f / 32.f;
			float cx = cw * (c % 16);
			float cy = ch * (c / 16);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * CHAR_WIDTH, y + 0, 0, cx, cy);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * CHAR_WIDTH, y + CHAR_HEIGHT, 0, cx, cy + ch);
			Tessellator.instance.getBuffer()
					.addQuadVertexWithUV(x + i * CHAR_WIDTH + CHAR_WIDTH, y + CHAR_HEIGHT, 0, cx + cw, cy + ch);
			Tessellator.instance.getBuffer()
					.addQuadVertexWithUV(x + i * CHAR_WIDTH + CHAR_WIDTH, y + 0, 0, cx + cw, cy);
		}
		Tessellator.instance.draw();
	}

	public void drawTextArea(GuiRenderState rs, String s, int x, int y, Rect2D area) {
		if(y > area.getMaxY()) {
			System.out.println("[FontRenderer::drawTextArea] Out of area!"); // TODO: Make debugger function
			return;
		}

		int dx = x < area.getX() ? area.getX() : x; // Fit into left border
		int dy = y < area.getY() ? area.getY() : y; // Fit into upper border
		
		for(int i = 0; i < s.length(); i++) {
			if(dx + CHAR_WIDTH >= area.getMaxX()) {
				dy += CHAR_HEIGHT;
				dx = area.getX();
			}
			drawText(rs, "" + s.charAt(i), dx, dy); // TODO: Send part of string // FIXME: Bad practics?
			dx += CHAR_WIDTH;

			if(dy >= area.getMaxY()) break; // Out of area
		}
	}
}
