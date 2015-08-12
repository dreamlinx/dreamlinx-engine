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

package org.dreamlinx.engine.fn;

import java.io.File;

import org.apache.commons.lang3.RandomUtils;
import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class SerialFnTest extends UnitTestSupport {

	private String target;
	private String filename;

	@Before
	public void before()
	{
		target = new String(RandomUtils.nextBytes(1024));
		filename = tempDir().getAbsolutePath() + File.separator + "target.ser";
	}

	@Test
	public void wrd() throws Exception
	{
		try {
			SerialFn.write(filename, target);

			String readTarget = (String) SerialFn.read(filename);
			assertEquals(target, readTarget);

			SerialFn.delete(filename);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}

	@Test
	public void load() throws Exception
	{
		try {
			String newTarget = SerialFn.load(filename, String.class);
			assertNotNull(newTarget);

			SerialFn.delete(filename);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}
}
