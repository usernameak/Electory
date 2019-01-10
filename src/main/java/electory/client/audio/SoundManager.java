package electory.client.audio;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecIBXM;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
	
	private SoundSystem soundSystem;

	public void init() {
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("xm", CodecIBXM.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			
			soundSystem = new SoundSystem();
			
			//soundSystem.backgroundMusic("music", getClass().getResource("/music/cassette_5_darling.xm"), "/music/cassette_5_darling.xm", true);
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		soundSystem.cleanup();
	}

}
