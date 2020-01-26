package electory.profiling;

import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class ElectoryProfiler {
	public static final ElectoryProfiler INSTANCE = new ElectoryProfiler();

	private ObjLongMap<String> times = HashObjLongMaps.getDefaultFactory().withDefaultValue(0L).newMutableMap();
	private ObjLongMap<String> beginTimes = HashObjLongMaps.getDefaultFactory().withDefaultValue(0L).newMutableMap();

	public void begin(String name) {
		beginTimes.put(name, System.nanoTime());
	}

	public void end(String name) {
		long beginTime = beginTimes.getLong(name);
		times.compute(name, (String k, long v) -> v + (System.nanoTime() - beginTime));
	}
	
	public ObjLongMap<String> getTimes() {
		return times;
	}
}
