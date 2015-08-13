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

import org.apache.log4j.Logger;
import org.dreamlinx.engine.conf.Configuration;
import org.dreamlinx.engine.error.InitializationException;

/**
 * The container of instances in memory of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Memory {

	private static final Logger logger = Log.getEngineLogger();

	private Configuration configuration;

	final void init(Configuration configuration)
	{
		if (this.configuration != null) {
			logger.warn("Memory already initialized.");
			return;
		}

		this.configuration = configuration;

		if (logger.isDebugEnabled())
			logger.debug("Memory initialized.");
	}

	public Configuration getConfiguration()
	{
		if (configuration == null)
			throw new InitializationException(Memory.class);

		return configuration;
	}
}
