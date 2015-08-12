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

package org.dreamlinx.engine.db;

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DbFunctionTest extends UnitTestSupport {

	@Test
	public void function() throws Exception
	{
		DbFunction func = new DbFunction("dummyFunction");

		assertNotNull(func.getOutputType());
		assertStringNotBlank(func.getFunctionName());
		assertNotNull(func.getParameters());
		assertFalse(func.hasParameters());

		assertStringNotBlank(func.getFunction());
		assertTrue(func.getFunction().contains("()"));

		func.addParameter("p_a", "4FT");
		func.addParameter("p_b", 11309);

		assertTrue(func.hasParameters());

		assertStringNotBlank(func.getFunction());
		assertFalse(func.getFunction().contains("()"));
	}
}
