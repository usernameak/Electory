package tinycraft.utils;

import tinycraft.TinyCraft;

public class TickTimer {
	private long time = System.currentTimeMillis();
	private long lastTickTime = System.currentTimeMillis();
	
	public TickTimer() {
	}
	
	public void update() {
		time = System.currentTimeMillis();
		int iters = 0;
		while(time - lastTickTime >= 50 && iters < 10) {
			lastTickTime += 50;
			TinyCraft.getInstance().tick();
			iters++;
		}
		if(lastTickTime > time) {
			lastTickTime = time;
		}
		if(iters >= 10) {
			System.out.println("Can't keep up. Skipping " + (iters - 9) + " ticks.");
			lastTickTime = time;
		}
	}
}
