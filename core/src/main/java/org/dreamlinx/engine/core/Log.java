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
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
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

	protected static Logger logger;

	/**
	 * Singleton instance of the Logger.
	 * 
	 * @return Logger
	 */
	public static Logger getLogger()
	{
		if (logger == null)
			throw new InitializationException(Log.class);

		return logger;
	}

	/**
	 * Change Logger level.
	 * 
	 * @param Level
	 */
	public static void change(Level level)
	{
		Validate.notNull(level, "level cannot be null.");
		if (logger == null)
			throw new InitializationException(Log.class);

		logger.setLevel(level);

		if (logger.isDebugEnabled())
			logger.debug(String.format("Logger level setted to '%s'.", level));
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
	 * Initialize the Logger with the `Level`. The `filename` and `patter`,
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
		if (logger != null) {
			logger.warn("Logger already initialized.");
			return;
		}

		Validate.notNull(level, "level cannot be null.");
		if (StringUtils.isBlank(filename))
			Validate.isTrue(hasConsole, "hasConsole must be true if filename is null");

		logger = Logger.getRootLogger();
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
			logger.addAppender(app);
		}

		if (hasConsole) {

			ConsoleAppender app = new ConsoleAppender();
			app.setName(APP_CONSOLE_NAME);
			app.setTarget("System.out");
			app.setLayout(layout);

			app.activateOptions();
			logger.addAppender(app);
		}

		logger.setLevel(level);
	}

	protected Log() {}
}
