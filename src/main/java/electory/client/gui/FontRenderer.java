package electory.client.gui;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;

public class FontRenderer {
	public static final int FONT_HEIGHT = 8;
	public static final int FONT_WIDTH = 8;

	private CharBuffer cb = CharBuffer.allocate(1);

	public int getTextWidth(String s) {
		return FONT_WIDTH * s.length();
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
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH, y + 0, 0, cx, cy);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH, y + FONT_HEIGHT, 0, cx, cy + ch);
			Tessellator.instance.getBuffer()
					.addQuadVertexWithUV(x + i * FONT_WIDTH + FONT_WIDTH, y + FONT_HEIGHT, 0, cx + cw, cy + ch);
			Tessellator.instance.getBuffer()
					.addQuadVertexWithUV(x + i * FONT_WIDTH + FONT_WIDTH, y + 0, 0, cx + cw, cy);
		}
		Tessellator.instance.draw();
	}
}
