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

package org.dreamlinx.engine.sys.data;

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class BiMapTest extends UnitTestSupport {

	private static BiMap<String, String> map;

	@Before
	public void before() throws Exception
	{
		map = new BiMap<>();

		map.put("Xfce4", "Thunar");
		map.put("Gnome", "Nautilus");
		map.put("Kde", "Konqueror");
	}

	@Test
	public void testReverseMap()
	{
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertNotNull(map.toString());
	}

	@Test
	public void testGet()
	{
		String value = map.get("Xfce4");
		String aValue = map.get("Kde");

		assertEquals("Xfce4", map.getKey(value));
		assertEquals("Kde", map.getKey(aValue));
	}

	@Test
	public void testContains()
	{
		assertTrue(map.containsKey("Xfce4"));
		assertTrue(map.containsKey("Gnome"));
		assertTrue(map.containsKey("Kde"));

		assertTrue(map.containsValue("Thunar"));
		assertTrue(map.containsValue("Nautilus"));
		assertTrue(map.containsValue("Konqueror"));
	}
}
