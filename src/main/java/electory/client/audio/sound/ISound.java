package electory.client.audio.sound;

import electory.client.audio.decoder.AudioDecoder;

public interface ISound {
	void initialize();
	void update();
	void destroy();
	void setDecoder(AudioDecoder decoder);
	void setSource(int source);
	void setLooping(boolean looping);
	boolean isEndOfStream();
}
