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

import java.util.Random;

import org.apache.log4j.Level;
import org.dreamlinx.engine.UnitTestSupport;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ChronoLogTest extends UnitTestSupport {

	@Test
	public void traceTimer() throws Exception
	{
		Log.change(Level.INFO);

		assertTrue(ChronoLog.start(this));
		assertFalse(ChronoLog.start(this));

		Random rand = new Random();
		while (rand.nextInt(256 * 128 * 64) != 1)
			;

		assertNotSame(0L, ChronoLog.stop(this, "traceTimer"));
	}

	@Test
	public void notTraceTimer() throws Exception
	{
		Log.change(Level.ERROR);

		assertFalse(ChronoLog.start(this));
		assertSame(- 1L, ChronoLog.stop(this, "notTraceTimer"));
	}

	@AfterClass
	public static void after()
	{
		Log.change(Level.TRACE);
	}
}
