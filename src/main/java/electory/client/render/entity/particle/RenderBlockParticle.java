package electory.client.render.entity.particle;

import org.joml.Vector3f;

import electory.client.render.IAtlasSprite;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.entity.EntityRenderer;
import electory.client.render.shader.ShaderManager;
import electory.client.render.world.WorldRenderState;
import electory.entity.particle.EntityBlockParticle;

public class RenderBlockParticle extends EntityRenderer<EntityBlockParticle> {

	@Override
	public void renderEntity(EntityBlockParticle entity, float renderPartialTicks, WorldRenderState rs) {
		Tessellator tess = Tessellator.instance;
		TriangleBuffer buffer = tess.getBuffer();
		IAtlasSprite sprite = entity.sprite;
		Vector3f pos = entity.getInterpolatedPosition(renderPartialTicks);
		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.bindTexture("/terrain.png");
		
		WorldRenderState rs2 = new WorldRenderState(rs);
		rs2.modelMatrix.translate(x - 0.0625f, y, z - 0.0625f);
		rs2.modelMatrix.scale(0.125f, 0.125f, 0.125f);
		
		ShaderManager.defaultProgram.loadRenderState(rs2);
		
		buffer.setColor(0xFFFFFFFF);
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
	}

}
