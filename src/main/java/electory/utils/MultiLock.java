package electory.utils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MultiLock implements Lock {
	private Set<Lock> locks;
	
	public MultiLock(Set<Lock> locks) {
		this.locks = locks;
	}

	@Override
	public void lock() {
		this.locks.forEach(Lock::lock);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unlock() {
		this.locks.forEach(Lock::unlock);
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}

}
