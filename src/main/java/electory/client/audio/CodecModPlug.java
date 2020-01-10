package electory.client.audio;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Memory;

import electory.client.audio.modplug.ModPlugLibrary;
import electory.client.audio.modplug.ModPlugLibrary.ModPlugFile;
import electory.client.audio.modplug.ModPlug_Settings;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;

public class CodecModPlug implements ICodec {
	private boolean reverseBytes = false;
	private ModPlugFile modPlugFile;
	private boolean initialized = false;
	private boolean endOfStream = false;
	private AudioFormat format = new AudioFormat(44100, 16, 2, true, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);

	@Override
	public void reverseByteOrder(boolean b) {
		reverseBytes = b;
	}

	@Override
	public boolean initialize(URL url) {
		byte[] data;
		try {
			data = IOUtils.toByteArray(url);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Memory mem = new Memory(data.length);
		mem.write(0, data, 0, data.length);
		this.modPlugFile = ModPlugLibrary.INSTANCE.ModPlug_Load(mem, (int) mem.size());
		initialized = true;
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
		endOfStream = false;
		
		return true;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public SoundBuffer read() {
		Memory mem = new Memory(SoundSystemConfig.getStreamingBufferSize());
		int readSize = ModPlugLibrary.INSTANCE.ModPlug_Read(modPlugFile, mem, (int) mem.size());
		byte[] data = mem.getByteArray(0, readSize);
		if(data.length == 0) endOfStream = true;
		return new SoundBuffer(data, format);
	}

	@Override
	public SoundBuffer readAll() {
		// FIXME: streaming only
		return null;
	}

	@Override
	public boolean endOfStream() {
		return endOfStream;
	}

	@Override
	public void cleanup() {
		ModPlugLibrary.INSTANCE.ModPlug_Unload(modPlugFile);
	}

	@Override
	public AudioFormat getAudioFormat() {
		return format;
	}

}
