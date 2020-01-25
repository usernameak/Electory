package electory.client.audio.sound;

import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import electory.client.audio.decoder.AudioDecoder;
import lombok.Getter;
import lombok.Setter;

public class PrefetchSound implements ISound {
	@Setter(onMethod = @__(@Override))
	private int source;

	@Setter(onMethod = @__(@Override))
	private AudioDecoder decoder;

	private int buffer = 0;

	@Getter(onMethod = @__(@Override))
	private boolean endOfStream = false;

	public PrefetchSound() {
		buffer = AL10.alGenBuffers();
	}

	@Override
	public void update() {
		if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
			this.endOfStream = true;
		}
	}

	@Override
	public void destroy() {
		AL10.alDeleteBuffers(buffer);
		AL10.alSourceStop(source);
		AL10.alDeleteSources(source);
	}

	@Override
	public void initialize() {
		ByteBuffer data = decoder.fetchAllSoundData();
		AL10.alBufferData(buffer, decoder.getFormat(), data, decoder.getSampleRate());
		decoder.close();
		decoder = null;
		
		AL10.alSourcei(source, AL10.AL_BUFFER, buffer);

		AL10.alSourcePlay(source);
	}
}
