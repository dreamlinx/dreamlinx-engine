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

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class Module {

	protected static final Logger logger = Log.getEngineLogger();

	public static void execute(Class<? extends Module> module) throws Exception
	{
		String n = module.getName();
		Module m = (Module) Module.class.getClassLoader()
			.loadClass(n).newInstance();

		execute(m);
	}

	public static void execute(Module instance) throws Exception
	{
		instance.init();
		instance.launch();

		if (logger.isInfoEnabled())
			logger.info("Executed module " + instance.getClass().getName());
	}

	public abstract void init() throws Exception;

	public abstract void launch() throws Exception;
}
