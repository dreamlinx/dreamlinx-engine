/**
 *  Copyright (C) 2015 DreamLinx <dreamlinx@dreamlinx.org>
 *  All Rights Reserved.
 *
 *  This file is part of DreamLinx.
 *
 *  DreamLinx is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DreamLinx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with DreamLinx. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dreamlinx.engine.sys;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.Validate;

/**
 * Thread pool with await synchronization when a slot is available.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @author Giuseppe Avola
 * @author Amir Kirsh
 * @since 1.0
 */
public class ThreadPoolNotifier extends ThreadPoolExecutor {

	private AtomicInteger runningTask = new AtomicInteger();
	private Synchronizer synchronizer = new Synchronizer();

	public ThreadPoolNotifier(
		int corePoolSize, int maximumPoolSize, long keepAliveTime,
		TimeUnit timeUnit, BlockingQueue<Runnable> workQueue,
		RejectedExecutionHandler handler) {

		super(corePoolSize, maximumPoolSize, keepAliveTime,
			timeUnit, workQueue, handler);
	}

	/**
	 * Await until the first slot is available.
	 * 
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException
	{
		awaitFor(1);
	}

	/**
	 * Await until the number of slots are available.
	 * 
	 * @param Number freeSlots
	 * @throws InterruptedException
	 */
	public void awaitFor(Number freeSlots) throws InterruptedException
	{
		Validate.notNull(freeSlots, "freeSlots cannot be null");
		Validate.isTrue((freeSlots.intValue() > 0), "freeSlots cannot be zero or negative");

		while (getAvailableSlots() <= (freeSlots.intValue() - 1))
			synchronizer.await();
	}

	/**
	 * The current number of running tasks.
	 * 
	 * @return int
	 */
	public int getRunningTasks()
	{
		return runningTask.get();
	}

	/**
	 * The current number of available slots.
	 * 
	 * @return int
	 */
	public int getAvailableSlots()
	{
		return (getMaximumPoolSize() - getRunningTasks());
	}

	/**
	 * The load factor is between 0.0 (empty) and 1.0 (full) or more (overfull).
	 * 
	 * @return float
	 */
	public float getLoadFactor()
	{
		return ((float) getRunningTasks() / getMaximumPoolSize());
	}

	//
	// Internal
	//

	@Override
	protected void beforeExecute(Thread t, Runnable r)
	{
		runningTask.incrementAndGet();
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t)
	{
		synchronized (this) {
			runningTask.decrementAndGet();
			synchronizer.signalAll();
		}
	}

	private class Synchronizer {

		private final Lock lock = new ReentrantLock();
		private final Condition done = lock.newCondition();
		private boolean isDone = false;

		protected void signalAll()
		{
			lock.lock(); // MUST lock!
			try {
				done.signalAll();
				isDone = true;
			}
			finally {
				lock.unlock(); // unlock even in case of an exception
			}
		}

		protected void await() throws InterruptedException
		{
			lock.lock(); // MUST lock!
			try {
				while (! isDone) { // avoid signaling on 'spuriously' wake-up
					done.await();
				}
			}
			finally {
				lock.unlock(); // unlock even in case of an exception
				isDone = false; // for next call to await
			}
		}
	}
}
