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

package org.dreamlinx.engine.conf;

import org.apache.log4j.Level;
import org.dreamlinx.engine.UnitTestSupport;
import org.dreamlinx.engine.error.ConfigurationException;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ConfigurationValidatorTest extends UnitTestSupport {

	@Test
	public void validate() throws Exception
	{
		try {
			ConfigurationValidator.validate(new Configuration());
			failWhenExceptionExpected();
		}
		catch (ConfigurationException e) {}

		try {
			ConfExt conf = new ConfExt();
			conf.setNodeName("DREAMLINX");
			conf.setPidFile("/tmp/dreamlinx.pid");

			ConfigurationValidator.validate(conf);
			failWhenExceptionExpected();
		}
		catch (ConfigurationException e) {}

		try {
			ConfExt conf = new ConfExt();
			conf.setNodeName("DREAMLINX");
			conf.setPidFile("/tmp/dreamlinx.pid");
			conf.setLogLevel(Level.FATAL);

			ConfigurationValidator.validate(conf);
			failWhenExceptionExpected();
		}
		catch (ConfigurationException e) {}

		try {
			ConfExt conf = new ConfExt();
			conf.setNodeName("DREAMLINX");
			conf.setPidFile("/tmp/dreamlinx.pid");
			conf.setLogLevel(Level.FATAL);
			conf.setThreadsNumber((short) 0);

			ConfigurationValidator.validate(conf);
			failWhenExceptionExpected();
		}
		catch (ConfigurationException e) {}

		try {
			ConfExt conf = new ConfExt();
			conf.setNodeName("DREAMLINX");
			conf.setPidFile("/tmp/dreamlinx.pid");
			conf.setLogLevel(Level.FATAL);
			conf.setThreadsNumber((short) 12);

			ConfigurationValidator.validate(conf);
		}
		catch (ConfigurationException e) {
			failWhenExceptionNotExpected(e);
		}
	}

	@SuppressWarnings("serial")
	private class ConfExt extends Configuration {

		@ConfNotZero
		private Short threadsNumber;

		public void setThreadsNumber(Short threadsNumber)
		{
			this.threadsNumber = threadsNumber;
		}
	}
}
