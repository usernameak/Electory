package electory.client.audio.sound;

import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import electory.client.audio.decoder.AudioDecoder;
import lombok.Getter;
import lombok.Setter;

public class StreamingSound implements ISound {
	@Setter(onMethod = @__(@Override))
	private int source;

	@Setter(onMethod = @__(@Override))
	private AudioDecoder decoder;

	public static final int STREAMING_BUFFER_SIZE = 131072;
	public static final int NUM_STREAMING_BUFFERS = 4;

	private int[] buffers = new int[NUM_STREAMING_BUFFERS];

	@Getter(onMethod = @__(@Override))
	private boolean endOfStream = false;

	public StreamingSound() {
		AL10.alGenBuffers(buffers);
	}

	@Override
	public void update() {
		if (decoder.isEOF()) {
			decoder.close();
			// no more buffers to feed
			int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
			for (int i = 0; i < processed; i++) {
				AL10.alSourceUnqueueBuffers(source);
			}
			if (AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED) == 0) {
				endOfStream = true;
			}
			return;
		}


		int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
		// if(processed > 0) System.out.println("processed " + processed);
		for (int i = 0; i < processed; i++) {
			int buffer = AL10.alSourceUnqueueBuffers(source);
			ByteBuffer data = decoder.fetchSoundData();
			// System.out.println(data.remaining());
			AL10.alBufferData(buffer, decoder.getFormat(), data, decoder.getSampleRate());
			AL10.alSourceQueueBuffers(source, buffer);
			if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
				AL10.alSourcePlay(source);
			}
		}
	}

	@Override
	public void destroy() {
		AL10.alDeleteBuffers(buffers);
		AL10.alSourceStop(source);
		AL10.alDeleteSources(source);
	}

	@Override
	public void initialize() {
		for (int i = 0; i < buffers.length; i++) {
			ByteBuffer data = decoder.fetchSoundData();
			AL10.alBufferData(buffers[i], decoder.getFormat(), data, decoder.getSampleRate());
		}
		AL10.alSourceQueueBuffers(source, buffers);

		AL10.alSourcePlay(source);
	}
}
