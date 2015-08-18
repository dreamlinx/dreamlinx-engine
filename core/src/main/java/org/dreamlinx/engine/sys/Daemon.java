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

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.ChronoLog;
import org.dreamlinx.engine.core.Log;

/**
 * Prototype of asynchronous task execution module. The generic T type
 * represents the return type of the task.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class Daemon<T> implements Callable<T> {

	private static final Logger logger = Log.getEngineLogger();

	private boolean breakOnError = false;
	private transient boolean running = false;

	@Override
	public final T call() throws Exception
	{
		T result = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("Start of '" + getName() + "' daemon");

			ChronoLog.start(this);
			running = true;

			result = execute();
		}
		catch (Exception e) {

			if (breakOnError)
				throw e;
			else
				logger.error("Error on '" + getName() + "' daemon", e);
		}
		finally {
			running = false;
			ChronoLog.stop(this, getName());

			if (logger.isDebugEnabled())
				logger.debug("End of '" + getName() + "' daemon");
		}

		return result;
	}

	/**
	 * Task execution of the daemon.
	 * 
	 * @return T
	 * @throws Exception
	 */
	public abstract T execute() throws Exception;

	/**
	 * Return the name of this Daemon. It return the name of the class.
	 * The concrete Daemon can override this method, suppling a desired name.
	 * 
	 * @return String
	 */
	protected String getName()
	{
		return getClass().getSimpleName();
	}

	/**
	 * If true, the daemon will throw exception when an error is occurred. The
	 * default value of this feature is false.
	 * 
	 * @param Boolean
	 */
	protected final void setBreakOnError(Boolean breakOnError)
	{
		this.breakOnError = breakOnError;
	}

	/**
	 * Return if the daemon is running or not.
	 * 
	 * @return Boolean
	 */
	public final Boolean isRunning()
	{
		return running;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
