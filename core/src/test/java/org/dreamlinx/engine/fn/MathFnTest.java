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

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MathFnTest extends UnitTestSupport {

	@Test
	public void fatt() throws Exception
	{
		assertEquals(1, MathFn.fatt(0).longValue());
		assertEquals(1, MathFn.fatt(1).longValue());
		assertEquals(2, MathFn.fatt(2).longValue());
		assertEquals(6, MathFn.fatt(3).longValue());
		assertEquals(24, MathFn.fatt(4).longValue());

		assertEquals(3_628_800, MathFn.fatt(10).longValue());
		assertEquals(479_001_600, MathFn.fatt(12).longValue());

		try {
			MathFn.fatt(- 1);
			failWhenExceptionExpected();
		}
		catch (Exception e) {}
	}

	@Test
	public void nPk() throws Exception
	{
		assertNotNull(MathFn.nPk(12, 4));
		assertEquals(11_880, MathFn.nPk(12, 4).longValue());
	}

	@Test
	public void nCk() throws Exception
	{
		assertNotNull(MathFn.nCk(12, 4));
		assertEquals(495, MathFn.nCk(12, 4).longValue());
	}
}
