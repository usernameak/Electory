package electory.utils;

import java.awt.Color;

import electory.client.TinyCraft;
import electory.client.render.IRenderState;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;
import lombok.AllArgsConstructor;

public class DrawUtil {
	public static TinyCraft electory = TinyCraft.getInstance();
	
	public static void bindTextureDefault(String par1) {
		ShaderManager.defaultProgram.bindTexture(par1);
	}
	
	public static void bindTextureWater(String par1) {
		ShaderManager.waterProgram.bindTexture(par1);
	}
	
	public static void bindTextureWorldComposite(WorldCompositeBindType par1, String par2) {
		switch(par1) {
			case DEPTH:
				ShaderManager.worldCompositeProgram.bindTextureDepth(par2);
				break;
			case OPAQUEDEPTH:
				ShaderManager.worldCompositeProgram.bindTextureOpaqueDepth(par2);
				break;
			case TEXTURE:
				ShaderManager.worldCompositeProgram.bindTexture(par2);
				break;
			case WATERMASK:
				ShaderManager.worldCompositeProgram.bindTextureWaterMask(par2);
				break;
		}
	}
	
	public static void bindTextureTerrain(String par1) {
		ShaderManager.terrainProgram.bindTexture(par1);
	}
	
	public static void bindTextureSolid(String par1) {
		ShaderManager.solidProgram.bindTexture(par1);
	}
	
	/**
	 * <br>Using reversed-rgb decimal color<br>
	 * Example: 0x(ALPHA)(XX XX XX)
	 */
	public static void bindColor(int par1) {
		Tessellator t = Tessellator.instance;
		t.getBuffer().setColor(par1);
	}
	
	public static void bindColor(int r, int g, int b) {
		Tessellator t = Tessellator.instance;
		t.getBuffer().setColor((b << 16) | (g << 8) | r | 0xFF000000);
	}
	
	public static void bindColor(int r, int g, int b, int a) {
		Tessellator t = Tessellator.instance;
		t.getBuffer().setColor((b << 16) | (g << 8) | r | (a << 24));
	}
	
	public static void drawBox(IRenderState state, float x, float y, float x1, float y1) {
		ShaderManager.solidProgram.loadRenderState(state);
		ShaderManager.solidProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertex(x, y, 0.0F);
		t.getBuffer().addQuadVertex(x, y1, 0.0F);
		t.getBuffer().addQuadVertex(x1, y1, 0.0F);
		t.getBuffer().addQuadVertex(x1, y, 0.0F);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static void drawBox(IRenderState state, float x, float y, float x1, float y1, float z) {
		ShaderManager.solidProgram.loadRenderState(state);
		ShaderManager.solidProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertex(x, y, z);
		t.getBuffer().addQuadVertex(x, y1, z);
		t.getBuffer().addQuadVertex(x1, y1, z);
		t.getBuffer().addQuadVertex(x1, y, z);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static void drawBoxGradient(IRenderState state, float x, float y, float x1, float y1, DColor dc) {
		ShaderManager.solidProgram.loadRenderState(state);
		ShaderManager.solidProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertex(x, y, 0.0F);
		t.getBuffer().addQuadVertex(x, y1, 0.0F);
		bindColor(dc.getDecimal());
		t.getBuffer().addQuadVertex(x1, y1, 0.0F);
		t.getBuffer().addQuadVertex(x1, y, 0.0F);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static void drawBoxGradient(IRenderState state, float x, float y, float x1, float y1, float z, DColor dc) {
		ShaderManager.solidProgram.loadRenderState(state);
		ShaderManager.solidProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertex(x, y, z);
		t.getBuffer().addQuadVertex(x, y1, z);
		bindColor(dc.getDecimal());
		t.getBuffer().addQuadVertex(x1, y1, z);
		t.getBuffer().addQuadVertex(x1, y, z);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static void drawBoxUV(IRenderState state, float x, float y, float x1, float y1) {
		ShaderManager.defaultProgram.loadRenderState(state);
		ShaderManager.defaultProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertexWithUV(x, y, 0.0F, 0, 0);
		t.getBuffer().addQuadVertexWithUV(x, y1, 0.0F, 0, 1);
		t.getBuffer().addQuadVertexWithUV(x1, y1, 0.0F, 1, 1);
		t.getBuffer().addQuadVertexWithUV(x1, y, 0.0F, 1, 0);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static void drawBoxUV(IRenderState state, float x, float y, float x1, float y1, float z) {
		ShaderManager.defaultProgram.loadRenderState(state);
		ShaderManager.defaultProgram.use();
		Tessellator t = Tessellator.instance;
		t.getBuffer().addQuadVertexWithUV(x, y, z, 0, 0);
		t.getBuffer().addQuadVertexWithUV(x, y1, z, 0, 1);
		t.getBuffer().addQuadVertexWithUV(x1, y1, z, 1, 1);
		t.getBuffer().addQuadVertexWithUV(x1, y, z, 1, 0);
		t.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
	}
	
	public static enum WorldCompositeBindType {
		TEXTURE, DEPTH, OPAQUEDEPTH, WATERMASK
	}
	
	@AllArgsConstructor
	public static class DColor {
		public float r, g, b, a;
		public static DColor get(Color par1) {
			return new DColor(((float)par1.getRed()) / 255.0F, ((float)par1.getGreen()) / 255.0F, ((float)par1.getBlue()) / 255.0F, ((float)par1.getAlpha()) / 255.0F);
		}
		
		public static DColor get(int r, int g, int b) {
			return new DColor(((float)r) / 255.0F, ((float)g) / 255.0F, ((float)b) / 255.0F, 1.0F);
		}
		
		public int getDecimal() {
			return (new Color(r, g, b, a).getRGB());
		}
	}
}
