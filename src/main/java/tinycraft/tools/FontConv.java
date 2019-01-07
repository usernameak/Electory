package tinycraft.tools;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class FontConv {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		BufferedImage img = loadFontTexture(new FileInputStream("font.bin"));
		ImageIO.write(img, "PNG", new FileOutputStream("font.png"));
	}

	public static BufferedImage loadFontTexture(InputStream is) throws IOException {
		final int HEIGHT = 128;
		final int WIDTH = 128;

		int[] buffer = new int[HEIGHT * WIDTH];

		for (int y = 0; y < 256; y++) {
			for (int i = 0; i < 8; i++) {
				byte b = (byte) is.read();
				for (int x = 0; x < 8; x++) {
					boolean bb = ((b >> (7 - x)) & 1) != 0;
					int xpos = y % 16 * 8 + x;
					int ypos = y / 16 * 8 + i;
					if (bb) {
						buffer[(xpos + ypos * 128)] = 0xFFFFFFFF;
					} else {
						buffer[(xpos + ypos * 128)] = 0x0;
					}
				}
			}
		}
		
		BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		img.setRGB(0, 0, WIDTH, HEIGHT, buffer, 0, WIDTH);
		
		return img;
	}

}
