package electory.client.render.world;

import static electory.math.MathUtils.deg2rad;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBCopyImage;
import org.lwjgl.opengl.ARBDrawBuffers;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.NVCopyImage;

import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.render.Tessellator;
import electory.client.render.entity.EntityRenderer;
import electory.client.render.shader.DefaultProgram;
import electory.client.render.shader.ShaderManager;
import electory.utils.RenderUtilities;
import electory.world.Chunk;
import electory.world.World;

public class WorldRenderer {
	private World world;

	private WorldRenderState renderState = new WorldRenderState();

	private int framebuffer = 0;
	private int framebuffer2 = 0;

	public boolean debugShadows = false;
	public boolean wireframeEnabled = false;

	public static final int RENDERPASS_WIREFRAME = -1;
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
		if (!chunkUpdateExecutor.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void preRenderPass(int pass) {
		int[] width_ = new int[1];
		int[] height_ = new int[1];

		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, width_, height_);

		int width = width_[0];
		int height = height_[0];

		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			if (GL.getCapabilities().GL_NV_copy_image) {
				NVCopyImage
						.glCopyImageSubDataNV(	TinyCraft.getInstance().textureManager
								.getTextureUnit("/dynamic/framebuffer_world_position.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_pos.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												width,
												height,
												1);
			} else if (GL.getCapabilities().GL_ARB_copy_image) {
				ARBCopyImage.glCopyImageSubData(
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_position.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_pos.png"),
												GL11.GL_TEXTURE_2D,
												0,
												0,
												0,
												0,
												width,
												height,
												1);
			} else {
				EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, framebuffer2);
				EXTFramebufferBlit.glBlitFramebufferEXT(0,
														0,
														width,
														height,
														0,
														0,
														width,
														height,
														GL11.GL_COLOR_BUFFER_BIT,
														GL11.GL_NEAREST);
				EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, framebuffer);
			}
			if (TinyCraft.getInstance().player.isHeadUnderwater()) {
				GL11.glCullFace(GL11.GL_FRONT);
			}
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			IntBuffer drawbuffers = BufferUtils.createIntBuffer(3);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT);
			drawbuffers.flip();
			ARBDrawBuffers.glDrawBuffersARB(drawbuffers);
			ShaderManager.waterProgram.use();
			ShaderManager.waterProgram.setTimer(TinyCraft.getInstance().tickTimer.totalTicks
					+ TinyCraft.getInstance().tickTimer.renderPartialTicks);
			Vector3d vec = TinyCraft.getInstance().player
					.getInterpolatedPosition(TinyCraft.getInstance().tickTimer.renderPartialTicks);
			ShaderManager.waterProgram.setWaterPositionOffset((float) vec.x, 0, (float) vec.z);
		} else if (pass == RENDERPASS_BASE) {
			IntBuffer drawbuffers = BufferUtils.createIntBuffer(2);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
			drawbuffers.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT);
			drawbuffers.flip();
			ARBDrawBuffers.glDrawBuffersARB(drawbuffers);
		} else if (pass == RENDERPASS_WIREFRAME) {
			ShaderManager.solidProgram.setColor(0.0f, 1.0f, 1.0f, 1.0f);
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
		} else if (pass == WorldRenderer.RENDERPASS_WIREFRAME) {
			return ShaderManager.solidProgram;
		}
		return ShaderManager.terrainProgram;
	}

	public int getVBOForPass(int pass) {
		if (pass == WorldRenderer.RENDERPASS_LIQUID1) {
			return VBO_LIQUID1;
		}
		return VBO_BASE;
	}

	public boolean doesPassAllowConditionalRendering(int pass) {
		return true;
	}

	public WorldRenderState processRenderStateForPass(int pass, WorldRenderState stateIn) {
		return stateIn;
	}

	public void render(float renderPartialTicks) {
		int[] width_ = new int[1];
		int[] height_ = new int[1];

		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, width_, height_);

		int width = width_[0];
		int height = height_[0];

		RenderUtilities.pushDebugGroup("World render");

		{
			RenderUtilities.pushDebugGroup("Prepare framebuffer");
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
			RenderUtilities.popDebugGroup();
		}

		renderState.projectionMatrix.identity();
		renderState.projectionMatrix.perspective(1.22173f, (float) width / (float) height, 0.01f, 1000f);
		renderState.viewMatrix.identity();
		renderState.viewMatrix.lookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
		renderState.viewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.pitch), 1.0f, 0.0f, 0.0f);
		renderState.viewMatrix.rotate((float) deg2rad(TinyCraft.getInstance().player.yaw), 0.0f, 1.0f, 0.0f);
		Vector3d pos = TinyCraft.getInstance().player.getInterpolatedPosition(renderPartialTicks);
		pos.add(0f, TinyCraft.getInstance().player.getEyeHeight(), 0f);
		renderState.viewMatrix.translate(0f, -pos.y, 0f);
		GL11.glDepthRange(0.01f, 1000f);

		{
			RenderUtilities.pushDebugGroup("Query prepass");
			world.chunkProvider.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
				WorldRenderState rs2 = new WorldRenderState(renderState);
				rs2.modelMatrix
						.translate(cr.chunk.getChunkBlockCoordX() - pos.x, 0f, cr.chunk.getChunkBlockCoordZ() - pos.z);
				cr.doChunkQuery(rs2);
			});
			RenderUtilities.popDebugGroup();
		}

		{
			RenderUtilities.pushDebugGroup("Entity rendering");
			world.getEntities().stream().forEach(e -> {
				WorldRenderState rs2 = new WorldRenderState(renderState);
				Vector3d epos = e.getInterpolatedPosition(renderPartialTicks);
				rs2.modelMatrix.translate(epos.x - pos.x, epos.y, epos.z - pos.z);
				EntityRenderer.getRendererFromEntity(e).renderEntity(e, renderPartialTicks, rs2);
			});
			RenderUtilities.popDebugGroup();
		}

		for (int i = 0; i < RENDERPASS_LAST; i++) {
			RenderUtilities.pushDebugGroup("World render pass " + i);
			preRenderPass(i);
			final int pass = i;

			final boolean c = doesPassAllowConditionalRendering(pass);
			world.chunkProvider.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
				if (c)
					cr.beginConditionalRender();
				WorldRenderState rs2 = new WorldRenderState(renderState);
				rs2.modelMatrix
						.translate(cr.chunk.getChunkBlockCoordX() - pos.x, 0f, cr.chunk.getChunkBlockCoordZ() - pos.z);
				cr.render(	processRenderStateForPass(pass, rs2),
							chunkUpdateExecutor,
							getRenderPassShader(pass),
							pass,
							getVBOForPass(pass));
				if (c)
					cr.endConditionalRender();
			});
			postRenderPass(i);
			RenderUtilities.popDebugGroup();
		}

		if (wireframeEnabled) {
			for (int i = 0; i < RENDERPASS_LAST; i++) {
				RenderUtilities.pushDebugGroup("World render wireframe pass " + i);
				preRenderPass(RENDERPASS_WIREFRAME);
				final int pass = RENDERPASS_WIREFRAME;
				final int realPass = i;

				final boolean c = doesPassAllowConditionalRendering(pass);
				world.chunkProvider.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(cr -> {
					if (c)
						cr.beginConditionalRender();
					WorldRenderState rs2 = new WorldRenderState(renderState);
					rs2.modelMatrix.translate(	cr.chunk.getChunkBlockCoordX() - pos.x,
												0f,
												cr.chunk.getChunkBlockCoordZ() - pos.z);
					cr.render(	processRenderStateForPass(pass, rs2),
								chunkUpdateExecutor,
								getRenderPassShader(pass),
								RENDERPASS_WIREFRAME,
								getVBOForPass(realPass));
					if (c)
						cr.endConditionalRender();
				});
				postRenderPass(RENDERPASS_WIREFRAME);
				RenderUtilities.popDebugGroup();
			}
		}

		{
			RenderUtilities.pushDebugGroup("Compositing");

			GL11.glDisable(GL11.GL_DEPTH_TEST);
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

			ResolutionScaler scaler = TinyCraft.getInstance().resolutionScaler;
			GuiRenderState grs = new GuiRenderState();
			scaler.setupOrtho(grs);

			ShaderManager.worldCompositeProgram.use();
			ShaderManager.worldCompositeProgram.bindTexture("/dynamic/framebuffer_world.png");
			ShaderManager.worldCompositeProgram.bindTextureWaterMask("/dynamic/framebuffer_world_watermask.png");
			ShaderManager.worldCompositeProgram.bindTextureDepth("/dynamic/framebuffer_world_depth.png");
			ShaderManager.worldCompositeProgram.bindTextureOpaquePos("/dynamic/framebuffer_world_opaque_pos.png");
			ShaderManager.worldCompositeProgram.bindTexturePosition("/dynamic/framebuffer_world_position.png");
			ShaderManager.worldCompositeProgram.setSubmergedInWater(TinyCraft.getInstance().player.isHeadUnderwater());
			ShaderManager.worldCompositeProgram.setZNear(0.01f);
			ShaderManager.worldCompositeProgram.setZFar(1000.f);
			ShaderManager.worldCompositeProgram.setCameraPos(new Vector3f(0, (float) pos.y, 0));
			ShaderManager.worldCompositeProgram.loadRenderState(grs);
			ShaderManager.worldCompositeProgram.setTimer(TinyCraft.getInstance().tickTimer.totalTicks
					+ TinyCraft.getInstance().tickTimer.renderPartialTicks);

			Tessellator tess = Tessellator.instance;
			tess.getBuffer().addQuadVertexWithUV(0, 0, 0, 0, 1);
			tess.getBuffer().addQuadVertexWithUV(0, scaler.getHeight(), 0, 0, 0);
			tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), scaler.getHeight(), 0, 1, 0);
			tess.getBuffer().addQuadVertexWithUV(scaler.getWidth(), 0, 0, 1, 1);
			tess.draw();

			RenderUtilities.popDebugGroup();
		}
		RenderUtilities.popDebugGroup();
	}

	public void updateScreenSize() {
		int[] width_ = new int[1];
		int[] height_ = new int[1];

		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, width_, height_);

		int width = width_[0];
		int height = height_[0];

		// Base buffer

		if (framebuffer != 0) {
			EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
		}
		framebuffer = EXTFramebufferObject.glGenFramebuffersEXT();

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);
		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world.png",
																	width,
																	height,
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
																	width,
																	height,
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

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_position.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_position.png",
																	width,
																	height,
																	GL30.GL_RGBA32F,
																	GL11.GL_RGBA,
																	GL11.GL_FLOAT);

		EXTFramebufferObject
				.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
											EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT,
											GL11.GL_TEXTURE_2D,
											TinyCraft.getInstance().textureManager
													.getTextureUnit("/dynamic/framebuffer_world_position.png"),
											0);

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_depth.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_depth.png",
																	width,
																	height,
																	EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT,
																	EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
																	EXTPackedDepthStencil.GL_UNSIGNED_INT_24_8_EXT);
		EXTFramebufferObject.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
														EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
														GL11.GL_TEXTURE_2D,
														TinyCraft.getInstance().textureManager
																.getTextureUnit("/dynamic/framebuffer_world_depth.png"),
														0);

		TinyCraft.getInstance().textureManager.disposeTexture("/dynamic/framebuffer_world_opaque_pos.png");
		TinyCraft.getInstance().textureManager.createVirtualTexture("/dynamic/framebuffer_world_opaque_pos.png",
																	width,
																	height,
																	GL11.GL_RGBA,
																	GL11.GL_RGBA,
																	GL11.GL_UNSIGNED_BYTE);

		int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
		if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
			throw new IllegalArgumentException("Framebuffer incomplete, error " + status);
		}

		// Framebuffer blit buffer

		if (!GL.getCapabilities().GL_NV_copy_image && !GL.getCapabilities().GL_ARB_copy_image) {
			if (framebuffer2 != 0) {
				EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer2);
			}
			framebuffer2 = EXTFramebufferObject.glGenFramebuffersEXT();
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer2);
			EXTFramebufferObject
					.glFramebufferTexture2DEXT(	EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
												EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
												GL11.GL_TEXTURE_2D,
												TinyCraft.getInstance().textureManager
														.getTextureUnit("/dynamic/framebuffer_world_opaque_pos.png"),
												0);
			status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
			if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
				throw new IllegalArgumentException("Framebuffer incomplete, error " + status);
			}
		}

		// Unbind

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
}
