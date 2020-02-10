package electory.client.stitcher;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import electory.client.TinyCraft;
import electory.client.render.IAtlasSprite;
import electory.client.render.IAtlasSpriteManager;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

public class AtlasSpriteManagerImplStitch implements IAtlasSpriteManager {
	@AllArgsConstructor
	public class StitcherTile {
		public BufferedImage image;
		public StitchedAtlasSprite sprite;
		public int x, y;
		public int side;
		public String name;
	}

	public class StitchedAtlasSprite implements IAtlasSprite {

		private int x, y, side, atlasWidth, atlasHeight;

		@Override
		public float getMinU() {
			return x / (float) atlasWidth;
		}

		@Override
		public float getMinV() {
			return y / (float) atlasHeight;
		}

		@Override
		public float getMaxU() {
			return (x + side) / (float) atlasWidth;
		}

		@Override
		public float getMaxV() {
			return (y + side) / (float) atlasHeight;
		}

	}

	public String textureName;

	private Map<String, StitchedAtlasSprite> sprites = new HashMap<>();

	public AtlasSpriteManagerImplStitch(String textureName) {
		super();
		this.textureName = textureName;
	}

	@Override
	public IAtlasSprite registerSprite(String name) {
		if (sprites.containsKey(name)) {
			return sprites.get(name);
		}
		val sprite = new StitchedAtlasSprite();
		sprites.put(name, sprite);
		return sprite;
	}

	@SneakyThrows(IOException.class)
	public void buildAtlas() {
		int minPotSide = 9999;
		int totalArea = 0;
		List<List<StitcherTile>> tileGroups = new ArrayList<>();
		for (Map.Entry<String, StitchedAtlasSprite> entry : sprites.entrySet()) {
			String name = entry.getKey();
			StitchedAtlasSprite sprite = entry.getValue();

			URL url = getClass().getResource(name);
			if (url == null) {
				url = getClass().getResource("/img/missing_texture.png");
			}
			BufferedImage img = ImageIO.read(url);

			if (img.getWidth() != img.getHeight()) {
				throw new RuntimeException("Could not stitch image " + name + " because its width is not equal to height");
			}

			if (img.getWidth() == 0 || (img.getWidth() & (img.getWidth() - 1)) != 0) {
				throw new RuntimeException("Could not stitch image " + name + " because its side is NPOT");
			}

			int potSide = 0;

			while ((1 << potSide) < img.getWidth()) {
				potSide++;
			}

			while (potSide >= tileGroups.size()) {
				tileGroups.add(new ArrayList<>());
			}

			totalArea += img.getWidth() * img.getHeight();

			tileGroups.get(potSide).add(new StitcherTile(img, sprite, 0, 0, img.getWidth(), name));

			if (potSide < minPotSide)
				minPotSide = potSide;
		}

		int atlasSide = (int) pow(2, ceil(log(sqrt((double) totalArea)) / log(2)));

		final int largestTileSide = 1 << (tileGroups.size() - 1);

		int nn = atlasSide / largestTileSide;

		boolean[] slots = new boolean[nn * nn];

		int firstFreeSlot = 0;

		for (int g = tileGroups.size() - 1; g >= 0; g--) {
			if (g < minPotSide)
				break;

			List<StitcherTile> tiles = tileGroups.get(g);

			for (StitcherTile tile : tiles) {
				int tileSlot = firstFreeSlot;
				tile.x = tileSlot % nn * tile.side;
				tile.y = tileSlot / nn * tile.side;
				/*
				 * System.out.println("x " + tile.x); System.out.println("y " + tile.y);
				 * System.out.println("side " + tile.side); System.out.println("slot " +
				 * tileSlot); System.out.println("=================");
				 */
				slots[tileSlot] = true;
				for (int i = tileSlot + 1; i < slots.length; i++) {
					if (!slots[i]) {
						firstFreeSlot = (int) (i);
						break;
					}
				}
			}

			boolean[] newSlots = new boolean[slots.length * 4];

			for (int i = 0; i < slots.length; i++) {
				boolean oldSlot = slots[i];

				int slotX = (int) (i % nn * 2);
				int slotY = (int) (i / nn * 2);

				int newNN = nn * 2;

				newSlots[slotX + slotY * newNN] = oldSlot;
				newSlots[slotX + 1 + slotY * newNN] = oldSlot;
				newSlots[slotX + (slotY + 1) * newNN] = oldSlot;
				newSlots[slotX + 1 + (slotY + 1) * newNN] = oldSlot;
			}

			int ffslotX = (int) (firstFreeSlot % nn * 2);
			int ffslotY = (int) (firstFreeSlot / nn * 2);

			nn *= 2;

			firstFreeSlot = ffslotX + ffslotY * nn;

			slots = newSlots;
		}

		int atlasHeight = totalArea <= (atlasSide * atlasSide / 2) ? atlasSide / 2 : atlasSide;

		BufferedImage outImg = new BufferedImage(atlasSide, atlasHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = outImg.createGraphics();

		for (int g = tileGroups.size() - 1; g >= 0; g--) {
			if (g < minPotSide)
				break;

			List<StitcherTile> tiles = tileGroups.get(g);

			for (StitcherTile tile : tiles) {
				gr.drawImage(tile.image, tile.x, tile.y, null);
				tile.sprite.x = tile.x;
				tile.sprite.y = tile.y;
				tile.sprite.side = tile.side;
				tile.sprite.atlasWidth = atlasSide;
				tile.sprite.atlasHeight = atlasHeight;
			}
		}

		ImageIO.write(outImg, "png", new File("test1902.png"));
		TinyCraft.getInstance().textureManager.loadTexture(textureName, outImg);
		TinyCraft.getInstance().logger.info("Stitched " + outImg.getWidth() + "x" + outImg.getHeight() + " atlas '" + textureName + "'");
	}

}
