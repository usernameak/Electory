package electory.client.audio.decoder;

import java.nio.ByteBuffer;

public abstract class AudioDecoder implements Cloneable {
	public abstract ByteBuffer fetchSoundData();

	public abstract ByteBuffer fetchAllSoundData();

	public abstract int getSampleRate();

	public abstract int getFormat();

	public abstract boolean isEOF();

	public abstract void close();

	public abstract AudioDecoder clone();

	public void advancedReset() {
		throw new UnsupportedOperationException();
	}
}
