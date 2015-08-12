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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MultiMapTest extends UnitTestSupport {

	private MultiMap<String, String> map;

	@Before
	public void before() throws Exception
	{
		map = new MultiMap<String, String>(LinkedHashSet.class);
		assertTrue(map.isEmpty());
		assertNotNull(map);

		Collection<String> coll =
			map.put("Xfce4", "Terminal");
		map.put("Xfce4", "Thunar");
		map.put("Xfce4", "Mousepad");
		map.put("Gnome", "Nautilus");
		map.put("Gnome", "GEdit");
		map.put("Kde", "Konqueror");
		map.put("Kde", "Kopete");

		assertFalse(map.isEmpty());
		assertEquals(3, map.size());
		assertEquals(3, coll.size());
		assertStringNotBlank(map.toString());
	}

	@Test
	public void get()
	{
		Collection<String> values = map.get("Xfce4");

		assertTrue(values instanceof LinkedHashSet);
		assertEquals(3, map.keySet().size());
		assertEquals(7, map.values().size());
		assertEquals(3, values.size());
	}

	@Test
	public void contains()
	{
		assertTrue(map.containsKey("Xfce4"));
		assertTrue(map.containsKey("Gnome"));
		assertTrue(map.containsKey("Kde"));
		assertFalse(map.containsKey("Fluxbox"));

		assertTrue(map.containsValue("Xfce4", "Thunar"));
		assertFalse(map.containsValue("Xfce4", "Kopete"));
		assertFalse(map.containsValue("Kde", "GEdit"));

		assertTrue(map.containsValue("Mousepad"));
		assertFalse(map.containsValue("Dolphin"));

		Collection<String> values = new LinkedList<String>();
		values.add("Terminal");
		values.add("Nautilus");
		values.add("Konqueror");

		assertTrue(map.containsValues(values));

		values.add("Exploder");
		assertFalse(map.containsValues(values));
	}

	@Test
	public void putAll()
	{
		Collection<String> collection = new ArrayList<String>(3);
		collection.add("K3b");
		collection.add("Amarok");
		collection.add("Kdevelop");

		map.putAll("Kde", collection);

		assertEquals(5, map.size("Kde"));
		assertEquals(10, map.values().size());
		assertEquals(3, map.size());
	}

	@Test
	public void remove()
	{
		Collection<String> coll = map.remove("Xfce4", "Thunar");

		assertNotNull(coll);
		assertEquals(2, coll.size());
		assertFalse(coll.contains("Thunar"));
		assertFalse(map.values().contains("Thunar"));

		coll = map.remove("Gnome", "Konqueror");

		assertNotNull(coll);
		assertEquals(2, coll.size());
		assertFalse(coll.contains("Konqueror"));
		assertNull(map.remove("Fluxbox", "Nautilus"));

		coll = map.remove("Gnome");

		assertNotNull(coll);
		assertEquals(2, coll.size());
		assertFalse(map.containsKey("Gnome"));
		assertEquals(2, map.size());
		assertNull(map.remove("Fluxbox"));

		map.clear();

		assertNotNull(map);
		assertTrue(map.isEmpty());
		assertEquals(0, map.size());
	}
}
