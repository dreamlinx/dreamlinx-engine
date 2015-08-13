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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.RollingPolicy;
import org.dreamlinx.engine.error.InitializationException;

/**
 * Internal logger of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Log {

	private static final String APP_CONSOLE_NAME = "CONSOLE";
	private static final String APP_FILE_NAME = "FILE";
	private static final String DEFAULT_PATTERN_LAYOUT =
		"%d{ISO8601} [%p] (%C{1}.%M:%L): %m%n";

	protected static Logger rootLogger;

	/**
	 * The Root Logger.
	 * 
	 * @return Logger
	 */
	public static Logger getLogger()
	{
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		return rootLogger;
	}

	/**
	 * Return the right Logger for the class from repository.
	 * 
	 * @param Class<?>
	 * @return Logger
	 */
	public static Logger getLogger(Class<?> clazz)
	{
		Validate.notNull(clazz, "class cannot be null.");
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		return getLogger(clazz.getName());
	}

	/**
	 * Return the right Logger for the name from repository.
	 * 
	 * @param String name
	 * @return Logger
	 */
	public static Logger getLogger(String name)
	{
		Validate.notBlank(name, "name cannot be null.");
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		return rootLogger.getLoggerRepository().getLogger(name);
	}

	/**
	 * The internal Logger for engine.
	 * 
	 * @return Logger
	 */
	public static Logger getEngineLogger()
	{
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		return getLogger("org.dreamlinx.engine");
	}

	/**
	 * Define the level of the Root Logger.
	 * 
	 * @param Level
	 */
	public static void change(Level level)
	{
		Validate.notNull(level, "level cannot be null.");
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		rootLogger.setLevel(level);

		if (rootLogger.isDebugEnabled())
			rootLogger.debug(String.format("Logger level setted to '%s'.", level));
	}

	/**
	 * Define the level of logging for a specified class.
	 * 
	 * @param Level
	 * @param Class<?>
	 */
	public static void change(Level level, Class<?> clazz)
	{
		Validate.notNull(level, "level cannot be null.");
		Validate.notNull(clazz, "class cannot be null.");
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		change(level, clazz.getName());
	}

	/**
	 * Define the level of logging for a name.
	 * 
	 * @param Level
	 * @param String name
	 */
	public static void change(Level level, String name)
	{
		Validate.notNull(level, "level cannot be null.");
		Validate.notBlank(name, "name cannot be null.");
		if (rootLogger == null)
			throw new InitializationException(Log.class);

		getLogger(name).setLevel(level);
	}

	//
	// Internal
	//

	/**
	 * Initialize the Logger to console only with a `Level`.
	 * 
	 * @param Level
	 */
	protected static void init(Level level)
	{
		init(level, null, null, null, true);
	}

	/**
	 * Initialize the Root Logger with the `Level`. The `filename` and `patter`,
	 * if supplied, convey informations to setup an additional FILE appender.
	 * The `hasConsole` governs the appender to console.
	 * 
	 * @param Level
	 * @param String patternLayout
	 * @param String filename
	 * @param RollingPolicy rollingPolicy
	 * @param Boolean hasConsole
	 */
	protected static void init(Level level, String patternLayout, String filename, RollingPolicy rollingPolicy, boolean hasConsole)
	{
		if (rootLogger != null) {
			rootLogger.warn("Logger already initialized.");
			return;
		}

		if (StringUtils.isBlank(filename))
			Validate.isTrue(hasConsole, "hasConsole must be true if filename is null");

		rootLogger = LogManager.getRootLogger();
		PatternLayout layout = new PatternLayout(
			(patternLayout == null) ? DEFAULT_PATTERN_LAYOUT : patternLayout);

		if (filename != null) {

			RollingFileAppender app = new RollingFileAppender();
			app.setName(APP_FILE_NAME);
			app.setAppend(true);
			app.setFile(filename);
			app.setLayout(layout);

			if (rollingPolicy != null)
				app.setRollingPolicy(rollingPolicy);

			app.activateOptions();
			rootLogger.addAppender(app);
		}

		if (hasConsole)
			rootLogger.getAppender(APP_CONSOLE_NAME).setLayout(layout);
		else
			rootLogger.removeAppender(APP_CONSOLE_NAME);

		if (level != null)
			change(level);
	}

	protected Log() {}
}
