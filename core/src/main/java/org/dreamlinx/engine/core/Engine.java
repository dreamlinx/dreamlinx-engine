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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.Version;
import org.dreamlinx.engine.conf.Configuration;
import org.dreamlinx.engine.conf.ConfigurationValidator;
import org.dreamlinx.engine.error.ConfigurationException;

/**
 * Main class of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class Engine {

	private static Logger logger;
	private static Configuration configuration;

	private Memory memory;
	private Bootstrap bootstrap;
	private Initialize initialize;
	private Kernel kernel;
	private Shutdown shutdown;

	public Engine(Configuration configuration) throws ConfigurationException {

		ConfigurationValidator.validate(configuration);
		Engine.configuration = configuration;
	}

	public static Configuration getConfiguration()
	{
		return configuration;
	}

	public void start() throws Exception
	{
		boot();

		if (initialize != null)
			initialize.init();

		if (kernel == null)
			kernel = new DummyKernel();

		kernel._alive();
	}

	public void setBootstrap(Bootstrap bootstrap)
	{
		this.bootstrap = bootstrap;
	}

	public void setInitialize(Initialize initialize)
	{
		this.initialize = initialize;
	}

	public void setMemory(Memory memory)
	{
		this.memory = memory;
	}

	public void setKernel(Kernel kernel)
	{
		this.kernel = kernel;
	}

	public void setShutdown(Shutdown shutdown)
	{
		this.shutdown = shutdown;
	}

	//
	// Internal
	//

	private void boot() throws Exception
	{
		// Log
		Log.init(Level.INFO,
			configuration.getLogPatternLayout(), configuration.getLogFile(),
			configuration.getLogRollPolicy(), configuration.getLogConsole());

		logger = Log.getLogger();

		if (logger.isInfoEnabled())
			logger.info("DreamLinx engine : version " + Version.get());

		Log.change(configuration.getLogLevel());

		if (logger.isDebugEnabled())
			logger.debug("Bootstrapping...");

		// Memory
		if (memory == null)
			memory = new Memory();

		memory.init(configuration);

		// Shutdown
		if (shutdown != null) {
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run()
				{
					try {
						shutdown.shut();
					}
					catch (Exception e) {
						logger.error("", e);
					}
				}
			});
		}

		// Defined bootstrap
		if (bootstrap != null)
			bootstrap.boot(configuration);

		if (logger.isDebugEnabled())
			logger.debug("Bootstrap completed.");
	}

	private class DummyKernel extends Kernel {

		@Override
		public void setup() throws Exception
		{}

		@Override
		public void alive() throws Exception
		{}
	}
}
