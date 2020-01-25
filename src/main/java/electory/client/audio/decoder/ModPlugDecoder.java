package electory.client.audio.decoder;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.lwjgl.openal.AL10;

import com.sun.jna.Memory;

import electory.client.audio.modplug.ModPlugLibrary;
import electory.client.audio.modplug.ModPlugLibrary.ModPlugFile;
import electory.client.audio.modplug.ModPlug_Settings;
import electory.client.audio.sound.StreamingSound;

public class ModPlugDecoder extends AudioDecoder {
	private ModPlugFile modPlugFile;
	private boolean endOfStream;

	public ModPlugDecoder(URL url) {
		byte[] data;
		try {
			data = IOUtils.toByteArray(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Memory mem = new Memory(data.length);
		mem.write(0, data, 0, data.length);
		ModPlug_Settings settings = new ModPlug_Settings();
		settings.mFlags = ModPlugLibrary._ModPlug_Flags.MODPLUG_ENABLE_OVERSAMPLING;
		settings.mChannels = 2;
		settings.mBits = 16;
		settings.mFrequency = 44100;
		settings.mResamplingMode = ModPlugLibrary._ModPlug_ResamplingMode.MODPLUG_RESAMPLE_LINEAR;
		settings.mStereoSeparation = 32;
		settings.mMaxMixChannels = 256;
		settings.mLoopCount = -1;
		ModPlugLibrary.INSTANCE.ModPlug_SetSettings(settings);
		this.modPlugFile = ModPlugLibrary.INSTANCE.ModPlug_Load(mem, (int) mem.size());
		endOfStream = false;
	}

	@Override
	public ByteBuffer fetchSoundData() {
		Memory mem = new Memory(StreamingSound.STREAMING_BUFFER_SIZE);
		int readSize = ModPlugLibrary.INSTANCE.ModPlug_Read(modPlugFile, mem, (int) mem.size());
		if (readSize == 0)
			endOfStream = true;
		return mem.getByteBuffer(0L, readSize);
	}

	@Override
	public int getSampleRate() {
		return 44100;
	}

	@Override
	public int getFormat() {
		return AL10.AL_FORMAT_STEREO16;
	}

	@Override
	public boolean isEOF() {
		return endOfStream;
	}

	@Override
	public ByteBuffer fetchAllSoundData() {
		throw new UnsupportedOperationException();
	}

	private boolean isClosed = false;

	@Override
	public void close() {
		if (!isClosed)
			ModPlugLibrary.INSTANCE.ModPlug_Unload(modPlugFile);
		isClosed = true;
	}

}
