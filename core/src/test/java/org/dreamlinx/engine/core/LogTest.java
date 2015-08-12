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
import org.dreamlinx.engine.UnitTestSupport;
import org.dreamlinx.engine.error.InitializationException;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogTest extends UnitTestSupport {

	@Test
	public void init() throws Exception
	{
		try {
			Log.getLogger();
			failWhenExceptionExpected();
		}
		catch (Exception e) {}

		Log.init(Level.INFO);

		try {
			Log.getLogger();
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}

	@Test
	public void logger() throws Exception
	{
		Logger l = Log.getLogger();

		assertNotNull(l);
		assertEquals(Level.INFO, l.getLevel());
	}

	@AfterClass
	public static void after()
	{
		try {
			Log.change(Level.TRACE);
		}
		catch (InitializationException e) {
			Log.init(Level.TRACE);
		}
	}
}
