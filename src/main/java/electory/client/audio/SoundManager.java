package electory.client.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.joml.Vector3d;
import org.joml.Vector3f;

import electory.entity.EntityPlayer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {

	private SoundSystem soundSystem;

	private Map<String, List<String>> randomSoundRegistry = new HashMap<>();
	
	private Random rand = new Random();

	public void init() {
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("xm", CodecModPlug.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);

			soundSystem = new SoundSystem();

			soundSystem.changeDopplerFactor(10.0f);
			
			registerRandomSounds();

			// soundSystem.backgroundMusic("music",
			// getClass().getResource("/music/cassette_5_darling.xm"),
			// "/music/cassette_5_darling.xm", true);
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
	}

	private void registerRandomSounds() {
		Scanner sc = new Scanner(getClass().getResourceAsStream("/audio/sound_random.def"));
		while (sc.hasNextLine()) {
			String s = sc.nextLine().trim();
			if (!s.isEmpty()) {
				String[] spl = s.split("\\s+");
				if (spl.length < 2 || spl[1].trim().isEmpty()) {
					sc.close();
					throw new IllegalArgumentException("invalid sound_random.def");
				}
				List<String> values = new ArrayList<>();
				randomSoundRegistry.put(spl[0], values);
				for (int i = 1; i < spl.length; i++) {
					values.add(spl[i]);
				}
			}
		}
		sc.close();
	}

	public void destroy() {
		soundSystem.cleanup();
	}

	public void updateListener(EntityPlayer player) {
		Vector3d vec = player.getInterpolatedPosition(0.0f);
		Vector3f vel = player.getVelocity();
		float var3 = player.pitch;
		float var4 = player.yaw;
		float var13 = (float) -Math.sin(-var4 * 0.017453292F - (float) Math.PI);
		float var14 = (float) -Math.sin(-var3 * 0.017453292F - (float) Math.PI);
		float var15 = (float) -Math.cos(-var4 * 0.017453292F - (float) Math.PI);
		soundSystem.setListenerPosition((float) vec.x, (float) vec.y, (float) vec.z);
		soundSystem.setListenerVelocity(vel.x, vel.y, vel.z);
		soundSystem.setListenerOrientation(var13, var14, var15, 0f, 1f, 0f);
	}
	
	public String getRealSoundPath(String pathIn) {
		String path = pathIn;
		if(randomSoundRegistry.containsKey(path)) {
			List<String> l = randomSoundRegistry.get(path);
			int r = rand.nextInt(l.size() + 1);
			path = r > 0 ? l.get(r - 1) : path;
		}
		
		return "/audio/" + path;
	}
	
	public void playMusic(String path, String name, boolean loop) {
		String realPath = getRealSoundPath(path);
		soundSystem.backgroundMusic(name, getClass().getResource(realPath), realPath, loop);
	}
	
	public void stopMusic(String name) {
		soundSystem.stop(name);
		soundSystem.removeSource(name);
	}

	public void playSFX(String path, String name, float x, float y, float z, float radius) {
		String realPath = getRealSoundPath(path);
		
		soundSystem.newSource(	false,
								name,
								getClass().getResource(realPath),
								realPath,
								false,
								x,
								y,
								z,
								SoundSystemConfig.ATTENUATION_LINEAR,
								radius);
		soundSystem.setVolume(name, 1.0f);
		soundSystem.play(name);
	}
}
