package electory.client.audio;

import org.joml.Vector3f;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AudioSource {
	private boolean streaming = false;
	
	private boolean ambient = false;
	
	private boolean looping = false;

	private String path;
	
	private Vector3f position;
	
	private float radius;
	
	public AudioSource(String path) {
		this.path = path;
	}
}
