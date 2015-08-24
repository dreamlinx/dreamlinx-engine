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

import java.util.List;
import java.util.Set;

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class HierarchyTreeTest extends UnitTestSupport {

	private HierarchyTree<Integer, String> tree;

	@Before
	public void before() throws Exception
	{
		tree = new HierarchyTree<Integer, String>(1, "ORHVSUVHOWRUVHO");

		assertTrue(tree.put(1, 11, "EWIVHOWEUVHWO"));
		assertTrue(tree.put(1, 12, "OBIHWROBIHROW"));
		assertTrue(tree.put(1, 13, "RBHRIBHOWWRTB"));

		assertTrue(tree.put(11, 111, "OEHBOEVHWEVYHWV"));
		assertTrue(tree.put(11, 112, "WIHOWGHOEGHOWEG"));
		assertTrue(tree.put(12, 121, "LDBEIBRPBWNPBNW"));
		assertTrue(tree.put(12, 122, "RTBPORJTBITBJPE"));
		assertTrue(tree.put(13, 131, "WEGIHWOEGHOGWEOHIUH"));

		assertTrue(tree.put(111, 1111, "BHWROUBHOWBHOWRTUHB"));
		assertTrue(tree.put(111, 1112, "RIBHORBHOWBHOBHOIUH"));
		assertTrue(tree.put(121, 1211, "RUBHROBHROBHIBHBUH"));
		assertTrue(tree.put(121, 1212, "WRTBHWROBUWRHBOHBO"));
		assertTrue(tree.put(131, 1311, "IHROBHRHBWOIBHWROBH"));

		assertFalse(tree.put(14, 141, "ASBKJFLJDFOVKJFGVCD"));
	}

	@Test
	public void contains()
	{
		assertTrue(tree.contains(1));
		assertTrue(tree.contains(11));
		assertTrue(tree.contains(111));
		assertTrue(tree.contains(1111));
		assertTrue(tree.contains(12));
		assertTrue(tree.contains(121));
		assertTrue(tree.contains(1211));
		assertTrue(tree.contains(13));
		assertTrue(tree.contains(131));
		assertTrue(tree.contains(1311));
		assertFalse(tree.contains(2));
		assertFalse(tree.contains(14));
		assertFalse(tree.contains(114));
		assertFalse(tree.contains(1114));
	}

	@Test
	public void size()
	{
		assertTrue(tree.equals(tree));

		assertNotSame(0, tree.size());
		assertEquals(14, tree.size());
		assertEquals(3, tree.deep());
	}

	@Test
	public void get()
	{
		assertEquals("ORHVSUVHOWRUVHO", tree.get(1));
		assertEquals("EWIVHOWEUVHWO", tree.get(11));
		assertEquals("OEHBOEVHWEVYHWV", tree.get(111));
		assertEquals("BHWROUBHOWBHOWRTUHB", tree.get(1111));
		assertEquals("OBIHWROBIHROW", tree.get(12));
		assertEquals("LDBEIBRPBWNPBNW", tree.get(121));
		assertEquals("RUBHROBHROBHIBHBUH", tree.get(1211));
		assertEquals("RBHRIBHOWWRTB", tree.get(13));
		assertEquals("WEGIHWOEGHOGWEOHIUH", tree.get(131));
		assertEquals("IHROBHRHBWOIBHWROBH", tree.get(1311));

		assertNull(tree.get(2));
		assertNull(tree.get(14));
		assertNull(tree.get(114));
		assertNull(tree.get(1114));
	}

	@Test
	public void remove()
	{
		String v = tree.remove(13);
		assertStringNotBlank(v);
		assertEquals("RBHRIBHOWWRTB", v);

		assertNull(tree.get(13));
		assertNull(tree.get(131));
		assertNull(tree.get(1311));
		assertEquals(11, tree.size());

		v = tree.remove(1212);
		assertStringNotBlank(v);
		assertEquals("WRTBHWROBUWRHBOHBO", v);

		assertNull(tree.get(1212));
		assertEquals(10, tree.size());

		tree.clear(1, "ABC");

		assertEquals(1, tree.size());
		assertEquals(1, tree.deep());
	}

	@Test
	public void keyValues()
	{
		Set<Integer> keySet = tree.keySet();
		List<String> values = tree.values();

		assertNotNull(keySet);
		assertTrue(keySet.contains(1));
		assertTrue(keySet.contains(1112));
		assertEquals(tree.size(), keySet.size());

		assertNotNull(values);
		assertTrue(values.contains("ORHVSUVHOWRUVHO"));
		assertTrue(values.contains("RIBHORBHOWBHOBHOIUH"));
		assertEquals(tree.size(), values.size());
	}

	@Test
	public void merge()
	{
		HierarchyTree<Integer, String> merge =
			new HierarchyTree<Integer, String>(112, "IUHBOSJBHSDOFUBHSODB");

		merge.put(112, 1123, "AVBOADFHBODFBHSDOFBH");
		merge.put(112, 1124, "STIBUHWIBHWRTIBUHOIO");
		merge.put(1123, 11231, "OSBIHSDOFBHSODFBHOHOI");
		merge.put(11231, 112311, "EIUVTJEROTCIMJEROIGMC");
		merge.put(1124, 11241, "RTIHUROHROBHOBUHUOIBH");
		merge.put(1124, 11242, "ESRGCJNXFNVG,JCXDFCGN");
		String image = tree.toString();

		assertFalse(tree.equals(merge));
		assertNotSame(tree.hashCode(), merge.hashCode());
		assertTrue(tree.merge(merge));

		assertTrue(tree.contains(1123));
		assertTrue(tree.contains(1124));
		assertTrue(tree.contains(11231));
		assertTrue(tree.contains(11242));
		assertEquals(20, tree.size());
		assertEquals(5, tree.deep());

		assertTrue(tree.merge(merge));
		assertEquals(20, tree.size());
		assertNotSame(image, tree.toString());
	}
}
