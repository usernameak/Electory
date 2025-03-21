package electory.client.audio.decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.openal.AL10;

import electory.client.audio.sound.StreamingSound;
import lombok.Getter;

public class VorbisDecoder extends AudioDecoder {

	@Getter(onMethod = @__(@Override))
	private int sampleRate;

	@Getter(onMethod = @__(@Override))
	private int format;

	private AudioInputStream baseStream;
	private AudioInputStream decodedStream;

	private boolean eof;

	public VorbisDecoder(URL url, boolean looping) {
		this.url = url;
		try {
			this.baseStream = AudioSystem.getAudioInputStream(url);
			AudioFormat baseFormat = baseStream.getFormat();
			AudioFormat decodedFormat = new AudioFormat(baseFormat.getSampleRate(), 16, baseFormat.getChannels(), true,
					ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
			this.decodedStream = AudioSystem.getAudioInputStream(decodedFormat, baseStream);
			this.sampleRate = (int) baseFormat.getSampleRate();

			switch (baseFormat.getChannels()) {
			case 1:
				this.format = AL10.AL_FORMAT_MONO16;
				break;
			case 2:
				this.format = AL10.AL_FORMAT_STEREO16;
				break;
			default:
				throw new UnsupportedOperationException();
			}
		} catch (IOException | UnsupportedAudioFileException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuffer fetchSoundData() {
		byte[] data = new byte[StreamingSound.STREAMING_BUFFER_SIZE];
		try {
			int count = 0;
			while (count == 0) {
				count = this.decodedStream.read(data);
			}
			if (count < 0) {
				count = 0;
				this.eof = true;
				return ByteBuffer.allocateDirect(0);
			}
			ByteBuffer buf = ByteBuffer.allocateDirect(count);
			buf.put(data, 0, count);
			// System.out.println(buf.remaining());
			buf.flip();
			// System.out.println(buf.remaining());
			return buf;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isEOF() {
		return eof;
	}

	@Override
	public ByteBuffer fetchAllSoundData() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while (true) {
				byte[] data = new byte[StreamingSound.STREAMING_BUFFER_SIZE];
				int count = this.decodedStream.read(data);
				if (count < 0) {
					this.eof = true;
					byte[] fullData = baos.toByteArray();
					ByteBuffer buf = ByteBuffer.allocateDirect(fullData.length);
					buf.put(fullData);
					buf.flip();
					return buf;
				}
				baos.write(data, 0, count);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isClosed = false;

	private URL url;

	@Override
	public void close() {
		try {
			if (!isClosed) {
				decodedStream.close();
				baseStream.close();
			}
			isClosed = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public AudioDecoder clone() {
		return new ModPlugDecoder(url, false);
	}
}
