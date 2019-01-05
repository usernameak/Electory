package tinycraft.entity.render.particle;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import tinycraft.TinyCraft;
import tinycraft.entity.particle.EntityBlockParticle;
import tinycraft.entity.render.EntityRenderer;
import tinycraft.render.IAtlasSprite;
import tinycraft.render.Tessellator;
import tinycraft.render.TriangleBuffer;

public class RenderBlockParticle extends EntityRenderer<EntityBlockParticle> {

	@Override
	public void renderEntity(EntityBlockParticle entity, float renderPartialTicks) {
		Tessellator tess = Tessellator.instance;
		TriangleBuffer buffer = tess.getBuffer();
		IAtlasSprite sprite = entity.sprite;
		Vector3f pos = entity.getInterpolatedPosition(renderPartialTicks);
		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		
		TinyCraft.getInstance().textureManager.bindTexture("/terrain.png");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x - 0.0625f, y, z - 0.0625f);
		GL11.glScalef(0.125f, 0.125f, 0.125f);
		
		buffer.addQuadVertexWithUV(0, 1, 0, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 1, 1, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 0, 0, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 0, 1, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 0, 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 0, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 0, 1, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 0, 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 1, 1, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 1, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 1, 0, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 0, 0, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 0, 0, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 1, 0, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 1, 1, sprite.getMinU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(1, 0, 1, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(1, 0, 0, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 0, 0, sprite.getMinU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 0, 1, sprite.getMaxU(), sprite.getMaxV());
		buffer.addQuadVertexWithUV(0, 1, 1, sprite.getMaxU(), sprite.getMinV());
		buffer.addQuadVertexWithUV(0, 1, 0, sprite.getMinU(), sprite.getMinV());
		tess.draw();
		
		GL11.glPopMatrix();
	}

}
