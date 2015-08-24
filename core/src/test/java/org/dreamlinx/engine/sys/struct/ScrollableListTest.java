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

package org.dreamlinx.engine.sys.struct;

import org.dreamlinx.engine.UnitTestSupport;
import org.dreamlinx.engine.sys.struct.ScrollableList;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ScrollableListTest extends UnitTestSupport {

	private static ScrollableList<String> list;

	@Before
	public void before() throws Exception
	{
		list = new ScrollableList<String>();
		list.add("Terminal");
		list.add("Thunar");
		list.add("Mousepad");
		list.add("Nautilus");
		list.add("GEdit");
		list.add("Konqueror");
		list.add("Kopete");
	}

	@Test
	public void testScrollableList()
	{
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertNotNull(list.toString());
	}

	@Test
	public void testScroll()
	{
		assertSame("Terminal", list.next());
		assertSame("Thunar", list.next());
		assertNotSame("Thunar", list.prev());
		assertSame("Kopete", list.prev());
	}
}
