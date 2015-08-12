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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Log;
import org.dreamlinx.engine.error.InitializationException;

/**
 * Tool for handling asynchronous task execution module by Daemon.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DaemonPool {

	private static final Logger logger = Log.getLogger();

	private static ThreadPoolNotifier pool;

	/**
	 * Initialization of the pool with values in XML configuration.
	 */
	public static void init(Number threadNumber)
	{
		Validate.notNull(threadNumber, "threadNumber cannot be null.");
		int poolSize = threadNumber.intValue();

		pool = new ThreadPoolNotifier(
			poolSize,
			poolSize,
			10, TimeUnit.SECONDS,
			new ArrayBlockingQueue<Runnable>(poolSize, true),
			new ThreadPoolExecutor.CallerRunsPolicy());

		if (logger.isDebugEnabled())
			logger.debug("Daemon pool is now active.");
	}

	/**
	 * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new
	 * tasks will be accepted. Invocation has no additional effect if already shut down.
	 * This method does not wait for previously submitted tasks to complete execution. Use
	 * awaitTermination to do that.
	 * 
	 * @param Number timeout
	 * @param TimeUnit unit
	 * @return List<Runnable>
	 * @throws Exception
	 */
	public static List<Runnable> shutdown(Number timeout, TimeUnit unit) throws Exception
	{
		if (pool == null)
			return null; // Already shut down

		List<Runnable> stillRun = null;
		if (! (pool.isShutdown() && pool.isTerminated())) {

			pool.shutdown();
			if (logger.isDebugEnabled())
				logger.debug("Daemons still running: #" + getRunningCount());

			logger.debug("Waiting for all deamons to terminated..");
			if (! pool.awaitTermination(timeout.longValue(), unit)) {

				if (logger.isInfoEnabled()) {
					String m = "The await timeout of %d %s is reached: force to shutdown immediatly!";
					logger.info(String.format(m, timeout, unit));
				}

				stillRun = pool.shutdownNow();
				if (logger.isDebugEnabled())
					logger.debug("Killed daemons: " + stillRun);
			}

			if (logger.isDebugEnabled())
				logger.debug("Daemon pool is terminated.");
		}
		else
			logger.warn("Daemon pool is already terminated.");

		return stillRun;
	}

	/**
	 * Attempts to stop all actively executing daemons, halts the processing of waiting
	 * and returns the raw list of the running tasks. This method does not wait for actively
	 * executing tasks to terminate - use `shutdown` to do that.
	 * 
	 * @return List<Runnable>
	 */
	public static List<Runnable> halt()
	{
		if (pool == null)
			throw new InitializationException(DaemonPool.class);

		if (pool.isShutdown() || pool.isTerminated()) {
			logger.warn("Daemon pool is already terminated.");
			return null;
		}

		List<Runnable> stillRun = pool.shutdownNow();
		if (logger.isInfoEnabled())
			logger.info("Daemon pool is halted.");

		return stillRun;
	}

	/**
	 * Start a Daemon class and returns its Future. The caller execution will
	 * continue. The call of the 'get()' method of the Future cause the
	 * awaiting in the caller for the completion of the task; it returns the
	 * result of the Daemon when it will be completed.
	 * 
	 * @param Daemon<T>
	 * @return Future<T>
	 */
	public static <T> Future<T> startForFuture(Daemon<T> daemon)
	{
		Validate.notNull(daemon, "daemon cannot be null");
		if (pool == null || pool.isShutdown())
			throw new InitializationException(DaemonPool.class);

		return pool.submit(daemon);
	}

	/**
	 * Start a Daemon class and waiting for the completion of the task. The
	 * result T will be returned when the task will be completed.
	 * 
	 * @param Daemon<T>
	 * @return T
	 * @throws Exception
	 */
	public static <T> T startForResult(Daemon<T> daemon) throws Exception
	{
		return startForFuture(daemon).get();
	}

	/**
	 * Await until the first slot is available.
	 * 
	 * @throws InterruptedException
	 */
	public static void await() throws InterruptedException
	{
		pool.await();
	}

	/**
	 * Await until the number of slots are available.
	 * 
	 * @param Number freeSlots
	 * @throws InterruptedException
	 */
	public static void await(Number freeSlots) throws InterruptedException
	{
		pool.awaitFor(freeSlots);
	}

	/**
	 * The load factor is between 0.0 (empty) and 1.0 (full).
	 * 
	 * @return float
	 */
	public static float getLoadFactor()
	{
		return Math.min(pool.getLoadFactor(), 1);
	}

	/**
	 * Returns the number of maximum running daemons.
	 * 
	 * @return int
	 */
	public static int getMaximumCount()
	{
		return pool.getMaximumPoolSize();
	}

	/**
	 * Returns the number of daemons that are running.
	 * 
	 * @return int
	 */
	public static int getRunningCount()
	{
		return pool.getRunningTasks();
	}

	/**
	 * Returns the number of available slots for new daemons.
	 * 
	 * @return int
	 */
	public static int getAvailableCount()
	{
		return pool.getAvailableSlots();
	}

	/**
	 * Check if all available slots are used by active daemon in execution.
	 * 
	 * @return boolean
	 */
	public static boolean isFull()
	{
		return (getAvailableCount() <= 0);
	}
}
