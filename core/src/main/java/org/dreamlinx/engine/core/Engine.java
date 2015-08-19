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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.Version;
import org.dreamlinx.engine.conf.Configuration;
import org.dreamlinx.engine.conf.ConfigurationValidator;
import org.dreamlinx.engine.error.ConfigurationException;
import org.dreamlinx.engine.fn.SysFn;

/**
 * Main class of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class Engine<C extends Configuration, M extends Memory> {

	private static boolean selfCheckMode = false;

	private C configuration;
	private M memory;

	private Class<? extends Bootstrap<C>> bootstrapClass;
	private Class<? extends Initialize<M>> initializeClass;
	private Class<? extends Kernel> kernelClass;
	private Class<? extends Shutdown<M>> shutdownClass;

	public Engine(C configuration, M memory) throws ConfigurationException {

		this(configuration);

		this.memory = memory;
	}

	public Engine(C configuration, Class<M> memory) throws ConfigurationException {

		this(configuration);

		try {
			this.memory = memory.newInstance();
		}
		catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	protected Engine(C configuration) throws ConfigurationException {

		ConfigurationValidator.validate(configuration);

		this.configuration = configuration;
		selfCheckMode = configuration.getSelfCheckMode();
	}

	public static boolean isSelfCheckMode()
	{
		return selfCheckMode;
	}

	//
	// Launcher
	//

	public void launch() throws Exception
	{
		boot();

		if (initializeClass != null)
			initializeClass.newInstance().init(memory);

		Kernel kernel;
		if (kernelClass != null)
			kernel = kernelClass.newInstance();
		else
			kernel = new DefaultKernel();

		kernel._alive();
	}

	public void fork() throws Exception
	{
		new Thread() {

			@Override
			public void run()
			{
				try {
					launch();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}.start();
	}

	//
	// Access
	//

	public C getConfiguration()
	{
		return configuration;
	}

	public M getMemory()
	{
		return memory;
	}

	//
	// Setup
	//

	public void setBootstrap(Class<? extends Bootstrap<C>> bootstrap)
	{
		this.bootstrapClass = bootstrap;
	}

	public void setInitialize(Class<? extends Initialize<M>> initialize)
	{
		this.initializeClass = initialize;
	}

	public void setKernelClass(Class<? extends Kernel> kernelClass)
	{
		this.kernelClass = kernelClass;
	}

	public void setShutdown(Class<? extends Shutdown<M>> shutdown)
	{
		this.shutdownClass = shutdown;
	}

	//
	// Internal
	//

	private void boot() throws Exception
	{
		// PID
		Integer pid = SysFn.getPid();

		File pidFile = new File(configuration.getPidFile());
		if (pidFile.exists())
			throw new IllegalStateException("Pid file already exists - check for another running process or zap the file first.");
		else
			pidFile.createNewFile();

		BufferedWriter bw = new BufferedWriter(new FileWriter(pidFile));
		bw.write(pid.toString());
		bw.flush();
		bw.close();

		// Log
		Log.init(null,
			configuration.getLogPatternLayout(), configuration.getLogFile(),
			configuration.getLogRollPolicy(), configuration.getLogConsole());

		Logger logger = Log.getEngineLogger();
		if (logger.isInfoEnabled())
			logger.info("DreamLinx Engine " + Version.get());

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
		memory.init();

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
						shutdownClass.newInstance().shut(memory);

					File pid = new File(configuration.getPidFile());
					if (pid.exists()) {

						try (Scanner scanner = new Scanner(pid)) {
							if (SysFn.getPid().equals(scanner.nextInt()))
								if (pid.delete() && logger.isDebugEnabled())
									logger.debug("Pid file is zapped.");
						}
					}

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
}
