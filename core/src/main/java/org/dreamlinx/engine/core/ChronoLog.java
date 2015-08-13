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

package org.dreamlinx.engine.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Level;

/**
 * Chronograph logger for timing executions.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ChronoLog extends Log {

	private static Map<Integer, StopWatch> timers;
	static {
		timers = new HashMap<Integer, StopWatch>();
	}

	/**
	 * Threshold level of logging.
	 */
	private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

	/**
	 * Start the timer with the instance of the caller class.
	 * Return false if the logger level is not enabled for
	 * DEFAULT_LOG_LEVEL; true otherwise.
	 * 
	 * @param Object thisInstance
	 * @return Boolean started
	 */
	public static Boolean start(Object thisInstance)
	{
		return start(thisInstance, DEFAULT_LOG_LEVEL);
	}

	/**
	 * Start the timer with the instance of the caller class.
	 * Return false if the logger level in parameter is not
	 * enabled for; true otherwise.
	 * 
	 * @param Object thisInstance
	 * @param Level logLevel
	 * @return Boolean started
	 */
	public static Boolean start(Object thisInstance, Level logLevel)
	{
		Validate.notNull(thisInstance, "thisInstance cannot be null");
		if (! rootLogger.isEnabledFor(logLevel))
			return false;

		StopWatch sw = timers.get(thisInstance.hashCode());
		if (sw == null) {
			sw = new StopWatch();
			timers.put(thisInstance.hashCode(), sw);
		}

		if (sw.isStarted()) {
			rootLogger.warn("Trace timer is already started.");
			return false;
		}

		sw.start();
		return true;
	}

	/**
	 * Stop the timer with the instance of the caller class and the
	 * name of required task. Return -1 if the logger level is not
	 * enabled for DEFAULT_LOG_LEVEL; the millis time otherwise.
	 * 
	 * @param Object thisInstance
	 * @return Long millis
	 */
	public static Long stop(Object thisInstance)
	{
		Validate.notNull(thisInstance, "thisInstance cannot be null");

		return stop(thisInstance, thisInstance.getClass().getSimpleName());
	}

	/**
	 * Stop the timer with the instance of the caller class and the
	 * name of required task. Return -1 if the logger level is not
	 * enabled for DEFAULT_LOG_LEVEL; the millis time otherwise.
	 * 
	 * @param Object thisInstance
	 * @param String taskName
	 * @return Long millis
	 */
	public static Long stop(Object thisInstance, String taskName)
	{
		return stop(thisInstance, taskName, DEFAULT_LOG_LEVEL);
	}

	/**
	 * Stop the timer with the instance of the caller class and the
	 * name of required task. Return -1 if the logger level is not
	 * enabled for logger level in parameter; the millis time otherwise.
	 * 
	 * @param Object thisInstance
	 * @param String taskName
	 * @param Level logLevel
	 * @return Long millis
	 */
	public static Long stop(Object thisInstance, String taskName, Level logLevel)
	{
		Validate.notNull(thisInstance, "thisInstance cannot be null");

		Long msTime = - 1L;
		if (! rootLogger.isEnabledFor(logLevel))
			return msTime;

		StopWatch sw = timers.get(thisInstance.hashCode());
		if (sw == null || ! sw.isStarted()) {
			rootLogger.warn("Trace timer not yet started.");
			return msTime;
		}

		sw.stop();
		msTime = sw.getTime();

		rootLogger.log(logLevel, String.format(
			"Execution time of task '%s': %s (%d millis)",
			taskName, sw.toString(), msTime));

		timers.put(thisInstance.hashCode(), null);
		timers.remove(thisInstance.hashCode());

		return msTime;
	}

	private ChronoLog() {}
}
