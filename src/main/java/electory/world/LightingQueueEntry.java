package electory.world;

import lombok.Data;

@Data
public class LightingQueueEntry {
    public final int x, y, z;
    public final boolean isDefinitelyIncreasing;
}
