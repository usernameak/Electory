package electory.client.render.world;

import static electory.math.MathUtils.deg2rad;

import java.nio.IntBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joml.Vector3d;
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

import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.render.Tessellator;
import electory.client.render.entity.EntityRenderer;
import electory.client.render.shader.DefaultProgram;
import electory.client.render.shader.ShaderManager;
import electory.world.Chunk;
import electory.world.World;

public class WorldRenderer {
	private World world;

	private WorldRenderState renderState = new WorldRenderState();

	private int framebuffer = 0;
	private int framebuffer2 = 0;

	public boolean debugShadows = false;

	public static final int RENDERPASS_BASE = 0;
	public static final int RENDERPASS_LIQUID1 = 1;
	public static final int RENDERPASS_LAST = 2;
	public static final int VBO_BASE = 0;
	public static final int VBO_LIQUID1 = 1;
	public static final int VBO_COUNT = 2;

	public static final int LIGHTMAP_SIZE = 1024;
	
	public static ExecutorService chunkUpdateExecutor = Executors.newSingleThreadExecutor();

	public WorldRenderer() {
	}
	
	public void terminate() throws InterruptedException {
		chunkUpdateExecutor.shutdown();
		if(!chunkUpdateExecutor.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}
	}

	public void setWorld(World world) {
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
		} /*
			 * else if (pass == RENDERPASS_BASE_SHADOW) {
			 * EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.
			 * GL_FRAMEBUFFER_EXT, framebuffer3); GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			 * GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			 * GL11.glViewport(0, 0, LIGHTMAP_SIZE, LIGHTMAP_SIZE); /* IntBuffer drawbuffers
			 * = BufferUtils.createIntBuffer(2);
			 * drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			 * drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT);
			 * drawbuffers.flip(); ARBDrawBuffers.glDrawBuffersARB(drawbuffers); Matrix4d
			 * lightMatrix = new Matrix4d(); lightMatrix.ortho(-10, 10, -10, 10, -10, 20);
			 * lightMatrix.lookAt(128f, 128f, 128f, 0f, 0f, 0f, 0f, 1f, 0f);
			 * ShaderManager.terrainProgram.setLightMatrix(lightMatrix);
			 */
		/* } */else if (pass == RENDERPASS_BASE) {
			/*
			 * ShaderManager.terrainProgram.bindTextureDepthShadow(
			 * "/dynamic/framebuffer_world_sunspace_depth.png"); Matrix4d lightMatrix = new
			 * Matrix4d(); lightMatrix.ortho(-25, 25, -25, 25, 1.0f, 512.0f); Vector3f pos =
			 * TinyCraft.getInstance().player
			 * .getInterpolatedPosition(TinyCraft.getInstance().tickTimer.renderPartialTicks
			 * ); lightMatrix.lookAt(pos.x + (256f - pos.y), 256f, pos.z, pos.x, pos.y,
			 * pos.z, 0f, 1f, 0f); ShaderManager.terrainProgram.setLightMatrix(lightMatrix);
			 */
		}
	}

	public void postRenderPass(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			GL11.glDrawBuffer(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glCullFace(GL11.GL_BACK);
		} /*
			 * else if (pass == RENDERPASS_BASE_SHADOW) {
			 * EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.
			 * GL_FRAMEBUFFER_EXT, framebuffer); GL11.glViewport(0, 0, Display.getWidth(),
			 * Display.getHeight()); GL11.glDepthRange(0.01f, 1000f); }
			 */
	}

	public DefaultProgram getRenderPassShader(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			return ShaderManager.waterProgram;
		} /*
			 * else if (pass == RENDERPASS_BASE_SHADOW) { return
			 * ShaderManager.shadowTerrainProgram; }
			 */
		return ShaderManager.terrainProgram;
	}

	public int getVBOForPass(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			return VBO_LIQUID1;
		}
		return VBO_BASE;
	}

	public boolean doesPassAllowConditionalRendering(int pass) {
		return true;// pass != RENDERPASS_BASE_SHADOW;
	}

	public WorldRenderState processRenderStateForPass(int pass, WorldRenderState stateIn) {
		/*
		 * if (pass == RENDERPASS_BASE_SHADOW) { WorldRenderState state = new
		 * WorldRenderState(); state.projectionMatrix.ortho(-25, 25, -25, 25, 1.0f,
		 * 512.0f); GL11.glDepthRange(1.0f, 512.0f); Vector3f pos =
		 * TinyCraft.getInstance().player
		 * .getInterpolatedPosition(TinyCraft.getInstance().tickTimer.renderPartialTicks
		 * ); state.viewMatrix.lookAt(pos.x + (256f - pos.y), 256f, pos.z, pos.x, pos.y,
		 * pos.z, 0f, 1f, 0f); state.modelMatrix = stateIn.modelMatrix; return state; }
		 */
		return stateIn;
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
		renderState.viewMatrix.identity();
		renderState.viewMatrix.lookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
		renderState.viewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.pitch), 1.0f, 0.0f, 0.0f);
		renderState.viewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.yaw), 0.0f, 1.0f, 0.0f);
		Vector3d pos = TinyCraft.getInstance().player.getInterpolatedPosition(renderPartialTicks);
		pos.add(0f, TinyCraft.getInstance().player.getEyeHeight(), 0f);
		renderState.viewMatrix.translate(0f, -pos.y, 0f);
		GL11.glDepthRange(0.01f, 1000f);

		world.chunkProvider.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
			WorldRenderState rs2 = new WorldRenderState(renderState);
			rs2.modelMatrix
					.translate(cr.chunk.getChunkBlockCoordX() - pos.x, 0f, cr.chunk.getChunkBlockCoordZ() - pos.z);
			cr.doChunkQuery(rs2);
		});

		world.getEntities().stream().forEach(e -> {
			WorldRenderState rs2 = new WorldRenderState(renderState);
			Vector3d epos = e.getInterpolatedPosition(renderPartialTicks);
			rs2.modelMatrix.translate(epos.x - pos.x, epos.y, epos.z - pos.z);
			EntityRenderer.getRendererFromEntity(e).renderEntity(e, renderPartialTicks, rs2);
		});

		for (int i = 0; i < RENDERPASS_LAST; i++) {
			preRenderPass(i);
			final int pass = i;

			final boolean c = doesPassAllowConditionalRendering(pass);
			world.chunkProvider.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
				if (c)
					cr.beginConditionalRender();
				WorldRenderState rs2 = new WorldRenderState(renderState);
				rs2.modelMatrix
						.translate(cr.chunk.getChunkBlockCoordX() - pos.x, 0f, cr.chunk.getChunkBlockCoordZ() - pos.z);
				cr.render(processRenderStateForPass(pass, rs2), chunkUpdateExecutor, getRenderPassShader(pass), pass, getVBOForPass(pass));
				if (c)
					cr.endConditionalRender();
			});
			postRenderPass(i);
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

		ResolutionScaler scaler = TinyCraft.getInstance().resolutionScaler;
		GuiRenderState grs = new GuiRenderState();
		scaler.setupOrtho(grs);

		if (!debugShadows) {
			ShaderManager.worldCompositeProgram.use();
			ShaderManager.worldCompositeProgram.bindTexture("/dynamic/framebuffer_world.png");
			ShaderManager.worldCompositeProgram.bindTextureWaterMask("/dynamic/framebuffer_world_watermask.png");
			ShaderManager.worldCompositeProgram.bindTextureDepth("/dynamic/framebuffer_world_depth.png");
			ShaderManager.worldCompositeProgram.bindTextureOpaqueDepth("/dynamic/framebuffer_world_opaque_depth.png");
			// ShaderManager.worldCompositeProgram.bindTextureDepthShadow("/dynamic/framebuffer_world_sunspace_depth.png");
			ShaderManager.worldCompositeProgram.setSubmergedInWater(TinyCraft.getInstance().player.isHeadUnderwater());
			ShaderManager.worldCompositeProgram.loadRenderState(grs);
			ShaderManager.worldCompositeProgram.setTimer(TinyCraft.getInstance().tickTimer.totalTicks
					+ TinyCraft.getInstance().tickTimer.renderPartialTicks);
		} else {
			/*
			 * ShaderManager.defaultProgram.use(); ShaderManager.defaultProgram.bindTexture(
			 * "/dynamic/framebuffer_world_sunspace_depth.png");
			 * ShaderManager.worldCompositeProgram.loadRenderState(grs);
			 */
		}
		Tessellator tess = Tessellator.instance;
		tess.getBuffer().addQuadVertexWithUV(0, 0, 0, 0, 1);
		tess.getBuffer().addQuadVertexWithUV(0, scaler.getHeight(), 0, 0, 0);
		tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), scaler.getHeight(), 0, 1, 0);
		tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), 0, 0, 1, 1);
		tess.draw();
	}

	public void updateScreenSize() {
		// Base buffer

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

		/*
		 * TinyCraft.getInstance().textureManager.disposeTexture(
		 * "/dynamic/framebuffer_world_depth_shadow.png");
		 * TinyCraft.getInstance().textureManager.createVirtualTexture(
		 * "/dynamic/framebuffer_world_depth_shadow.png", Display.getWidth(),
		 * Display.getHeight(), GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
		 * 
		 * EXTFramebufferObject .glFramebufferTexture2DEXT(
		 * EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
		 * EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT, GL11.GL_TEXTURE_2D,
		 * TinyCraft.getInstance().textureManager
		 * .getTextureUnit("/dynamic/framebuffer_world_depth_shadow.png"), 0);
		 */

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

		// Framebuffer blit buffer

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

		/*
		 * // Shadow buffer
		 * 
		 * if (framebuffer3 != 0) {
		 * EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer3); } framebuffer3 =
		 * EXTFramebufferObject.glGenFramebuffersEXT();
		 * EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.
		 * GL_FRAMEBUFFER_EXT, framebuffer3); /*
		 * TinyCraft.getInstance().textureManager.disposeTexture(
		 * "/dynamic/framebuffer_world_sunspace_debug.png");
		 * TinyCraft.getInstance().textureManager.createVirtualTexture(
		 * "/dynamic/framebuffer_world_sunspace_debug.png", 1024, 1024, GL11.GL_RGBA,
		 * GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
		 * TinyCraft.getInstance().textureManager.setTextureFilter(GL11.GL_LINEAR,
		 * GL11.GL_LINEAR);
		 * 
		 * EXTFramebufferObject.glFramebufferTexture2DEXT(
		 * EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
		 * EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D,
		 * TinyCraft.getInstance().textureManager
		 * .getTextureUnit("/dynamic/framebuffer_world_sunspace_debug.png"), 0);
		 */
		/*
		 * TinyCraft.getInstance().textureManager.disposeTexture(
		 * "/dynamic/framebuffer_world_sunspace_depth.png");
		 * TinyCraft.getInstance().textureManager.createVirtualTexture(
		 * "/dynamic/framebuffer_world_sunspace_depth.png", LIGHTMAP_SIZE,
		 * LIGHTMAP_SIZE, EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT,
		 * EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
		 * EXTPackedDepthStencil.GL_UNSIGNED_INT_24_8_EXT);
		 * TinyCraft.getInstance().textureManager.setTextureFilter(GL11.GL_LINEAR,
		 * GL11.GL_LINEAR);
		 * TinyCraft.getInstance().textureManager.setTextureWrap(GL12.GL_CLAMP_TO_EDGE,
		 * GL12.GL_CLAMP_TO_EDGE); EXTFramebufferObject .glFramebufferTexture2DEXT(
		 * EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
		 * EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, GL11.GL_TEXTURE_2D,
		 * TinyCraft.getInstance().textureManager
		 * .getTextureUnit("/dynamic/framebuffer_world_sunspace_depth.png"), 0); status
		 * = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.
		 * GL_FRAMEBUFFER_EXT); if (status !=
		 * EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) { throw new
		 * IllegalArgumentException("Framebuffer incomplete, error " + status); }
		 */

		// Unbind

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
}
