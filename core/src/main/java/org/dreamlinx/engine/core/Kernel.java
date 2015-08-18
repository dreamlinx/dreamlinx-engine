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

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.fn.MathFn;

/**
 * The main component of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class Kernel {

	private static final Logger logger = Log.getEngineLogger();

	/**
	 * Sleeping for saving CPU when idle.
	 */
	private long sleepTime = 500;

	final void _alive() throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Starting kernel...");

		setup();

		if (logger.isDebugEnabled())
			logger.debug("Kernel is ready!");

		while (true) {

			alive();
			await(sleepTime);
		}
	}

	public abstract void setup() throws Exception;
	public abstract void alive() throws Exception;

	public final void setSleepTime(Long millisec)
	{
		Validate.isTrue(MathFn.notZero(millisec), "Sleep time cannot be zero.");

		this.sleepTime = millisec.longValue();
	}

	protected final void await(long millisec) throws InterruptedException
	{
		Thread.sleep(millisec);
	}
}
