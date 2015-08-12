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

package org.dreamlinx.engine.sys;

import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DaemonPoolTest extends UnitTestSupport {

	private static Task t1 = new Task(1 << 24);
	private static Task t2 = new Task(1 << 6);

	@Before
	public void init()
	{
		try {
			DaemonPool.init(8);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}

	@Test
	public void start() throws Exception
	{
		Future<Long> f1 = DaemonPool.startForFuture(t1);
		Future<Long> f2 = DaemonPool.startForFuture(t2);

		assertNotNull(f2.get());
		assertTrue(t1.isRunning());
		assertFalse(t2.isRunning());

		assertNotNull(f1.get());
		assertFalse(t1.isRunning());
	}

	@After
	public void shutdown()
	{
		try {
			DaemonPool.shutdown(5, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}
}

class Task extends Daemon<Long> {

	Integer factor;

	public Task(Integer factor) {

		this.factor = factor;
	}

	@Override
	public Long execute() throws Exception
	{
		Random rnd = new Random();
		long time = System.currentTimeMillis();

		while (rnd.nextInt(factor) != 1)
			;

		return (System.currentTimeMillis() - time);
	}
}
