package electory.client.render.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBTextureMultisample;
import org.lwjgl.opengl.GL11;

import electory.utils.CrashException;

public class TextureManager {
	public static final String TERRAIN_TEXTURE = "/img/generated/block_atlas.png";

	private Map<String, Integer> loadedTextures = new HashMap<>();

	public void bindTexture(String texture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureUnit(texture));
	}

	public void bindTextureMS(String texture) {
		GL11.glBindTexture(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, getTextureUnit(texture));
	}

	public void createVirtualTexture(String texture, int width, int height, int internalformat, int format, int type) {
		int textureUnit = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureUnit);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, type, (ByteBuffer) null);

		loadedTextures.put(texture, textureUnit);
	}

	public void createVirtualTextureMS(String texture, int width, int height, int internalformat, 
			int samples) {
		int textureUnit = GL11.glGenTextures();

		GL11.glBindTexture(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, textureUnit);

		GL11.glTexParameteri(	ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE,
								GL11.GL_TEXTURE_MAG_FILTER,
								GL11.GL_NEAREST);
		GL11.glTexParameteri(	ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE,
								GL11.GL_TEXTURE_MIN_FILTER,
								GL11.GL_NEAREST);
		GL11.glTexParameteri(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		ARBTextureMultisample.glTexImage2DMultisample(	ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE,
														samples,
														internalformat,
														width,
														height,
														false);

		loadedTextures.put(texture, textureUnit);
	}

	public void setTextureFilter(int minFilter, int magFilter) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, magFilter);
	}

	public void setTextureWrap(int s, int t) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, s);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, t);
	}
	
	public int loadTexture(String texture, BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		int textureUnit = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureUnit);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glTexImage2D(	GL11.GL_TEXTURE_2D,
							0,
							GL11.GL_RGBA,
							image.getWidth(),
							image.getHeight(),
							0,
							GL11.GL_RGBA,
							GL11.GL_UNSIGNED_BYTE,
							buffer);

		loadedTextures.put(texture, textureUnit);
		return textureUnit;
	}

	public int getTextureUnit(String texture) {
		if (loadedTextures.containsKey(texture)) {
			return loadedTextures.get(texture);
		}
		URL resourceURL = getClass().getResource(texture);
		try {
			BufferedImage image = ImageIO.read(resourceURL);
			return loadTexture(texture, image);
		} catch (IOException e) {
			throw new CrashException(e);
		}
	}

	public void disposeTexture(String texture) {
		if (loadedTextures.containsKey(texture)) {
			GL11.glDeleteTextures(loadedTextures.get(texture));
			loadedTextures.remove(texture);
			return;
		}
	}
}
