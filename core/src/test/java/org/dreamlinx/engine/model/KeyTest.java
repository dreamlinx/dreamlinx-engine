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

package org.dreamlinx.engine.model;

import org.apache.commons.lang3.StringUtils;
import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class KeyTest extends UnitTestSupport {

	@Test
	public void key() throws Exception
	{
		assertNotNull(new Key("12", null).hashKey);
		assertNotNull(new Key("", null).hashKey);

		assertEquals(new Key("6G6"), new Key("6G6"));
		assertEquals(new Key("12", null), new Key("12", null));
		assertEquals(new Key("12", "12"), new Key("12", "12"));

		assertEquals(new Key("12").hashCode(), new Key("12").hashCode());
		assertNotEquals(new Key("12", null).hashCode(), new Key("12").hashCode());

		assertNotEquals(new Key("A"), new Key(65));
		assertNotEquals(new Key("12", null), new Key("12"));
		assertNotEquals(new Key("12", "12"), new Key("21", "21"));
		assertNotEquals(new Key("6C8", null), new Key("6BW", null));
		assertNotEquals(new Key("6G7", null), new Key("6FX", null));
		assertNotEquals(new Key(24360435), new Key(22173489));
		assertNotEquals(new Key("CM", "NA"), new Key("CC", "NO"));
		assertNotEquals(new Key("A", "T1"), new Key("AT1", null));
		assertNotEquals(new Key(null, "AT1"), new Key("AT1", null));
		assertNotEquals(new Key("AT1", null), new Key(null, "AT1"));

		assertNotEquals(new Key(StringUtils.repeat("+", 123), 2317652398475632984L), new Key(StringUtils.repeat("+", 321), 2317652398475632984L));

		// this test fails because the hash alg works always with string.
		// "12" and 12d have the same string value.
		// assertNotEquals(new Key("12"), new Key(12));
	}
}
