package tinycraft.client.gui;

import tinycraft.client.render.Tessellator;
import tinycraft.client.render.shader.ShaderManager;

public class FontRenderer {
	public static final int FONT_HEIGHT = 8;
	public static final int FONT_WIDTH = 8;

	public void drawText(GuiRenderState rs, String s, int x, int y) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.loadRenderState(rs);
		ShaderManager.defaultProgram.bindTexture("/font.png");
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			float cw = 1.f / 16.f;
			float ch = 1.f / 16.f;
			float cx = cw * (c % 16);
			float cy = ch * (c / 16);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH, y + 0, 0, cx, cy);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH, y + FONT_HEIGHT, 0, cx, cy + ch);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH + FONT_WIDTH, y + FONT_HEIGHT, 0, cx + cw, cy + ch);
			Tessellator.instance.getBuffer().addQuadVertexWithUV(x + i * FONT_WIDTH + FONT_WIDTH, y + 0, 0, cx + cw, cy);
		}
		Tessellator.instance.draw();
	}
}
