package electory.client.audio;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import electory.client.TinyCraft;
import electory.client.audio.decoder.AudioDecoder;
import electory.client.audio.decoder.ModPlugDecoder;
import electory.client.audio.decoder.VorbisDecoder;
import electory.client.audio.sound.ISound;
import electory.client.audio.sound.PrefetchSound;
import electory.client.audio.sound.StreamingSound;
import electory.entity.EntityPlayer;

public class SoundManager {
	private Map<String, List<String>> randomSoundRegistry = new HashMap<>();

	private Random rand = new Random();

	private Map<String, ISound> streaming = new HashMap<>();

	private Map<String, Class<? extends AudioDecoder>> decoders = new HashMap<>();

	private long alContext;
	private long device;

	public int numPlayingSounds() {
		return streaming.size();
	}

	public void init() {
		device = ALC10.alcOpenDevice((CharSequence) null);
		ALCCapabilities alcCaps = ALC.createCapabilities(device);
		alContext = ALC10.alcCreateContext(device, (IntBuffer) null);
		ALC10.alcMakeContextCurrent(alContext);
		AL.createCapabilities(alcCaps);

		decoders.put("xm", ModPlugDecoder.class);
		decoders.put("s3m", ModPlugDecoder.class);
		decoders.put("it", ModPlugDecoder.class);
		decoders.put("abc", ModPlugDecoder.class);
		decoders.put("pat", ModPlugDecoder.class);
		decoders.put("stm", ModPlugDecoder.class);
		decoders.put("med", ModPlugDecoder.class);
		decoders.put("mtm", ModPlugDecoder.class);
		decoders.put("mdl", ModPlugDecoder.class);
		decoders.put("dbm", ModPlugDecoder.class);
		decoders.put("669", ModPlugDecoder.class);
		decoders.put("far", ModPlugDecoder.class);
		decoders.put("ams", ModPlugDecoder.class);
		decoders.put("okt", ModPlugDecoder.class);
		decoders.put("ptm", ModPlugDecoder.class);
		decoders.put("ult", ModPlugDecoder.class);
		decoders.put("dmf", ModPlugDecoder.class);
		decoders.put("dsm", ModPlugDecoder.class);
		decoders.put("umx", ModPlugDecoder.class);
		decoders.put("amf", ModPlugDecoder.class);
		decoders.put("psm", ModPlugDecoder.class);
		decoders.put("mt2", ModPlugDecoder.class);
		decoders.put("mod", ModPlugDecoder.class);

		decoders.put("ogg", VorbisDecoder.class);

		// new VorbisDecoder(getClass().getResource("/audio/sfx/door1.ogg"));

		registerRandomSounds();
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

	public String getRealSoundPath(String pathIn) {
		String path = pathIn;
		if (randomSoundRegistry.containsKey(path)) {
			List<String> l = randomSoundRegistry.get(path);
			int r = rand.nextInt(l.size() + 1);
			path = r > 0 ? l.get(r - 1) : path;
		}

		return "/audio/" + path;
	}

	public void update() {
		streaming.values().forEach(ISound::update);
		streaming.values().stream().filter(ISound::isEndOfStream).forEach(ISound::destroy);
		streaming.values().removeIf(ISound::isEndOfStream);
	}

	public void play(String name, AudioSource data) {
		String realPath = getRealSoundPath(data.getPath());
		
		URL url = getClass().getResource(realPath);

		AudioDecoder decoder = null;
		String upath = url.getPath();
		String ext = upath.substring(upath.lastIndexOf('.') + 1);
		try {
			decoder = decoders.get(ext).getConstructor(URL.class, boolean.class).newInstance(url, data.isLooping());
		} catch (Exception e) {
			// failed to find decoder. fail
			TinyCraft.getInstance().logger.severe("failed to initialize audio decoder for format ." + ext);
			return;
		}

		int source = AL10.alGenSources();

		if (data.isAmbient()) {
			AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
			AL10.alSource3f(source, AL10.AL_POSITION, 0, 0, 0);
		} else {
			AL10.alSource3f(source, AL10.AL_POSITION, data.getPosition().x, data.getPosition().y, data.getPosition().z);
			AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 0);
			AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, data.getRadius());
		}

		ISound sound;

		if (data.isStreaming()) {
			sound = new StreamingSound();
		} else {
			sound = new PrefetchSound();
		}

		sound.setLooping(data.isLooping());
		
		sound.setDecoder(decoder);
		sound.setSource(source);

		sound.initialize();

		streaming.put(name, sound);
	}

	public void stopMusic(String name) {
		if (streaming.containsKey(name)) {
			streaming.get(name).destroy();
			streaming.remove(name);
		}
	}

	public void destroy() {
		for (ISound sound : streaming.values()) {
			sound.destroy();
		}
		ALC10.alcDestroyContext(this.alContext);
		ALC10.alcCloseDevice(this.device);
		ALC.destroy();
	}

	public void updateListener(EntityPlayer player) {
		Vector3d vec = player.getInterpolatedPosition(0.0f);
		Vector3f vel = player.getVelocity();
		float var3 = player.pitch;
		float var4 = player.yaw;
		float var13 = (float) -Math.sin(-var4 * 0.017453292F - (float) Math.PI);
		float var14 = (float) -Math.sin(-var3 * 0.017453292F - (float) Math.PI);
		float var15 = (float) -Math.cos(-var4 * 0.017453292F - (float) Math.PI);
		AL10.alListener3f(AL10.AL_POSITION, (float) vec.x, (float) vec.y, (float) vec.z);
		AL10.alListener3f(AL10.AL_VELOCITY, (float) vel.x, (float) vel.y, (float) vel.z);
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] { var13, var14, var15, 0f, 1f, 0f });
		// soundSystem.setListenerOrientation(var13, var14, var15, 0f, 1f, 0f);
	}
}
