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

	private static Configuration configuration;

	private Memory memory;
	private Kernel kernel;

	private Class<? extends Memory> memoryClass;
	private Class<? extends Bootstrap> bootstrapClass;
	private Class<? extends Initialize> initializeClass;
	private Class<? extends Shutdown> shutdownClass;

	public Engine(Configuration configuration) throws ConfigurationException {

		this(configuration, null);
	}

	public Engine(Configuration configuration, Class<? extends Memory> memoryClass)
		throws ConfigurationException {

		ConfigurationValidator.validate(configuration);

		Engine.configuration = configuration;
		this.memoryClass = memoryClass;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Configuration> T getConfiguration()
	{
		return (T) configuration;
	}

	public void start() throws Exception
	{
		boot();

		if (initializeClass != null)
			initializeClass.newInstance().init();

		if (kernel == null)
			kernel = new DefaultKernel();

		kernel._alive();
	}

	@SuppressWarnings("unchecked")
	public <T extends Memory> T getMemory()
	{
		return (T) memory;
	}

	public void setKernel(Kernel kernel)
	{
		this.kernel = kernel;
	}

	public void setBootstrapClass(Class<? extends Bootstrap> bootstrap)
	{
		this.bootstrapClass = bootstrap;
	}

	public void setInitializeClass(Class<? extends Initialize> initialize)
	{
		this.initializeClass = initialize;
	}

	public void setShutdownClass(Class<? extends Shutdown> shutdown)
	{
		this.shutdownClass = shutdown;
	}

	//
	// Internal
	//

	private void boot() throws Exception
	{
		// Log
		Log.init(null,
			configuration.getLogPatternLayout(), configuration.getLogFile(),
			configuration.getLogRollPolicy(), configuration.getLogConsole());

		Logger logger = Log.getEngineLogger();
		if (logger.isInfoEnabled())
			logger.info("DreamLinx Engine : version " + Version.get());

		// Log level
		Level level;
		if ((level = configuration.getLogLevel()) == null) {
			for (String name : configuration.getLogLevels().keySet())
				Log.change(configuration.getLogLevels().get(name), name);
		}
		else
			Log.change(level);

		if (logger.isDebugEnabled())
			logger.debug("Bootstrapping...");

		// Memory
		if (memoryClass != null)
			memory = memoryClass.newInstance();
		else
			memory = new DefaultMemory();

		memory.init(configuration);

		// Defined bootstrap
		if (bootstrapClass != null)
			bootstrapClass.newInstance().boot(configuration);

		// Shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run()
			{
				try {
					if (shutdownClass != null)
						shutdownClass.newInstance().shut();

					if (logger.isInfoEnabled())
						logger.info("DreamLinx Engine is halted.");
				}
				catch (Exception e) {
					logger.error("", e);
				}
			}
		});

		if (logger.isDebugEnabled())
			logger.debug("Bootstrap completed.");
	}

	private class DefaultKernel extends Kernel {

		@Override
		public void setup() throws Exception
		{}

		@Override
		public void alive() throws Exception
		{}
	}

	private class DefaultMemory extends Memory {

		private static final long serialVersionUID = 1770347889523288053L;
	}
}
