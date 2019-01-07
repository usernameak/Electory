package tinycraft.client.render.world;

import static tinycraft.math.MathUtils.deg2rad;

import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBCopyImage;
import org.lwjgl.opengl.ARBDrawBuffers;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVCopyImage;

import tinycraft.client.TinyCraft;
import tinycraft.client.gui.GuiRenderState;
import tinycraft.client.gui.ResolutionScaler;
import tinycraft.client.render.Tessellator;
import tinycraft.client.render.entity.EntityRenderer;
import tinycraft.client.render.shader.DefaultProgram;
import tinycraft.client.render.shader.ShaderManager;
import tinycraft.world.Chunk;
import tinycraft.world.World;

public class WorldRenderer {
	private World world;

	private WorldRenderState renderState = new WorldRenderState();

	private int framebuffer = 0;
	private int framebuffer2 = 0;

	public static final int RENDERPASS_BASE = 0;
	public static final int RENDERPASS_LIQUID1 = 1;
	public static final int RENDERPASS_LAST = 2;

	public WorldRenderer(World world) {
		this.world = world;
	}

	public void preRenderPass(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			if (GLContext.getCapabilities().GL_NV_copy_image) {
				NVCopyImage
						.glCopyImageSubDataNV(	TinyCraft.getInstance().textureManager
								.getTextureUnit("/dynamic/framebuffer_world_depth.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_depth.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												Display.getWidth(),
												Display.getHeight(),
												1);
			} else if (GLContext.getCapabilities().GL_ARB_copy_image) {
				ARBCopyImage.glCopyImageSubData(
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_depth.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_depth.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												Display.getWidth(),
												Display.getHeight(),
												1);
			} else {
				EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, framebuffer2);
				EXTFramebufferBlit.glBlitFramebufferEXT(0,
														0,
														Display.getWidth(),
														Display.getHeight(),
														0,
														0,
														Display.getWidth(),
														Display.getHeight(),
														GL11.GL_DEPTH_BUFFER_BIT,
														GL11.GL_NEAREST);
				EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, framebuffer);
			}
			if (TinyCraft.getInstance().player.isHeadUnderwater()) {
				GL11.glCullFace(GL11.GL_FRONT);
			}
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			IntBuffer drawbuffers = BufferUtils.createIntBuffer(2);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT);
			drawbuffers.flip();
			ARBDrawBuffers.glDrawBuffersARB(drawbuffers);
			ShaderManager.waterProgram.use();
			ShaderManager.waterProgram.setTimer(TinyCraft.getInstance().tickTimer.totalTicks
					+ TinyCraft.getInstance().tickTimer.renderPartialTicks);
		}
	}

	public void postRenderPass(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glCullFace(GL11.GL_BACK);
		}
	}

	public DefaultProgram getRenderPassShader(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			return ShaderManager.waterProgram;
		}
		return ShaderManager.terrainProgram;
	}

	public void render(float renderPartialTicks) {
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
		GL11.glClearColor(0.52f, 0.8f, 0.92f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT);
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);

		renderState.projectionMatrix.identity();
		renderState.projectionMatrix
				.perspective(1.22173f, (float) Display.getWidth() / (float) Display.getHeight(), 0.01f, 1000f);
		renderState.modelViewMatrix.identity();
		renderState.modelViewMatrix.lookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
		renderState.modelViewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.pitch), 1.0f, 0.0f, 0.0f);
		renderState.modelViewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.yaw), 0.0f, 1.0f, 0.0f);
		Vector3f pos = TinyCraft.getInstance().player.getInterpolatedPosition(renderPartialTicks);
		pos.add(0f, TinyCraft.getInstance().player.getEyeHeight(), 0f);
		renderState.modelViewMatrix.translate(-pos.x, -pos.y, -pos.z);
		GL11.glDepthRange(0.01f, 1000f);

		world.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
			cr.doChunkQuery(renderState);
		});
		
		for (int i = 0; i < RENDERPASS_LAST; i++) {
			
			preRenderPass(i);
			final int pass = i;
			world.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
				cr.beginConditionalRender();
				cr.render(renderState, getRenderPassShader(pass), pass);
				cr.endConditionalRender();
			});
			postRenderPass(i);
		}

		world.getEntities()
				.stream()
				.forEach(e -> EntityRenderer.getRendererFromEntity(e).renderEntity(e, renderPartialTicks, renderState));

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

		ResolutionScaler scaler = TinyCraft.getInstance().resolutionScaler;
		GuiRenderState grs = new GuiRenderState();
		scaler.setupOrtho(grs);

		ShaderManager.worldCompositeProgram.use();
		ShaderManager.worldCompositeProgram.bindTexture("/dynamic/framebuffer_world.png");
		ShaderManager.worldCompositeProgram.bindTextureWaterMask("/dynamic/framebuffer_world_watermask.png");
		ShaderManager.worldCompositeProgram.bindTextureDepth("/dynamic/framebuffer_world_depth.png");
		ShaderManager.worldCompositeProgram.bindTextureOpaqueDepth("/dynamic/framebuffer_world_opaque_depth.png");
		ShaderManager.worldCompositeProgram.setSubmergedInWater(TinyCraft.getInstance().player.isHeadUnderwater());
		ShaderManager.worldCompositeProgram.loadRenderState(grs);
		ShaderManager.worldCompositeProgram.setTimer(TinyCraft.getInstance().tickTimer.totalTicks
				+ TinyCraft.getInstance().tickTimer.renderPartialTicks);
		Tessellator tess = Tessellator.instance;
		tess.getBuffer().addQuadVertexWithUV(0, 0, 0, 0, 1);
		tess.getBuffer().addQuadVertexWithUV(0, scaler.getHeight(), 0, 0, 0);
		tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), scaler.getHeight(), 0, 1, 0);
		tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), 0, 0, 1, 1);
		tess.draw();
	}

	public void updateScreenSize() {
		if (framebuffer != 0) {
			EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
		}
		framebuffer = EXTFramebufferObject.glGenFramebuffersEXT();

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);
		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world.png",
																	Display.getWidth(),
																	Display.getHeight(),
																	GL11.GL_RGBA,
																	GL11.GL_RGBA,
																	GL11.GL_UNSIGNED_BYTE);
		TinyCraft.getInstance().textureManager.setTextureFilter(GL11.GL_LINEAR, GL11.GL_LINEAR);

		EXTFramebufferObject.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
														EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
														GL11.GL_TEXTURE_2D,
														TinyCraft.getInstance().textureManager
																.getTextureUnit("/dynamic/framebuffer_world.png"),
														0);

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_watermask.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_watermask.png",
																	Display.getWidth(),
																	Display.getHeight(),
																	GL11.GL_RGBA,
																	GL11.GL_RGBA,
																	GL11.GL_UNSIGNED_BYTE);

		EXTFramebufferObject
				.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
											EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT,
											GL11.GL_TEXTURE_2D,
											TinyCraft.getInstance().textureManager
													.getTextureUnit("/dynamic/framebuffer_world_watermask.png"),
											0);

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_depth.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_depth.png",
																	Display.getWidth(),
																	Display.getHeight(),
																	EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT,
																	EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
																	EXTPackedDepthStencil.GL_UNSIGNED_INT_24_8_EXT);
		EXTFramebufferObject.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
														EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
														GL11.GL_TEXTURE_2D,
														TinyCraft.getInstance().textureManager
																.getTextureUnit("/dynamic/framebuffer_world_depth.png"),
														0);

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_opaque_depth.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_opaque_depth.png",
																	Display.getWidth(),
																	Display.getHeight(),
																	EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT,
																	EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
																	EXTPackedDepthStencil.GL_UNSIGNED_INT_24_8_EXT);

		int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
		if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
			throw new IllegalArgumentException("Framebuffer incomplete, error " + status);
		}

		if (!GLContext.getCapabilities().GL_NV_copy_image && !GLContext.getCapabilities().GL_ARB_copy_image) {
			if (framebuffer2 != 0) {
				EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer2);
			}
			framebuffer2 = EXTFramebufferObject.glGenFramebuffersEXT();
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer2);
			EXTFramebufferObject
					.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
												EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
												GL11.GL_TEXTURE_2D,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_depth.png"),
												0);
			status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
			if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
				throw new IllegalArgumentException("Framebuffer incomplete, error " + status);
			}
		}
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
}
