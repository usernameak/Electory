package electory.profiling;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public class ElectoryProfiler {
	public static final ElectoryProfiler INSTANCE = new ElectoryProfiler();

	private Object2LongMap<String> times = new Object2LongOpenHashMap<>();
	private Object2LongMap<String> beginTimes = new Object2LongOpenHashMap<>();
	{
		times.defaultReturnValue(0);
		beginTimes.defaultReturnValue(0);
	}

	public void begin(String name) {
		beginTimes.put(name, System.nanoTime());
	}

	public void end(String name) {
		long beginTime = beginTimes.getLong(name);
		if(!times.containsKey(name)) {
			times.put(name, System.nanoTime() - beginTime);
		} else {
			times.put(name, times.getLong(name) + (System.nanoTime() - beginTime));
		}
	}

	public Object2LongMap<String> getTimes() {
		return times;
	}
}
