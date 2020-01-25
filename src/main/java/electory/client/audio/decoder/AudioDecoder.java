package electory.client.audio.decoder;

import java.nio.ByteBuffer;

public abstract class AudioDecoder {
	public abstract ByteBuffer fetchSoundData();
	public abstract ByteBuffer fetchAllSoundData();
	public abstract int getSampleRate();
	public abstract int getFormat();
	public abstract boolean isEOF();
}
