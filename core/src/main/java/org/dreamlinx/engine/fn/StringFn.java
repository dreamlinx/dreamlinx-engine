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

import java.util.List;
import java.util.Map;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class StringFn {

	public static char[] content(final String source)
	{
		char[] chars = new char[source.length()];
		for (int i = 0; i < source.length(); i++)
			chars[i] = source.charAt(i);

		return chars;
	}

	public static String cut(final String source, char start, char end)
	{
		return cut(source, start, end, true);
	}

	public static String cut(final String source, char start, char end, boolean inverse)
	{
		String cut = null;

		int iLeft = 0;
		boolean rLeft = false;
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == start) {
				iLeft = i;
				rLeft = true;
				break;
			}
		}

		int iRight = 0;
		boolean rRight = false;
		if (inverse) {
			iRight = source.length() - 1;
			for (int i = source.length() - 1; i >= 0; i--) {
				if (source.charAt(i) == end) {
					iRight = i;
					rRight = true;
					break;
				}
			}
		}
		else {
			iRight = iLeft + 1;
			for (int i = 0; i < source.length(); i++) {
				if (source.charAt(i) == end) {
					iRight = i;
					rRight = true;
					break;
				}
			}
		}

		if (rLeft && rRight)
			cut = source.substring(iLeft + 1, iRight);
		else
			cut = source;

		return cut;
	}

	public static String cut(final String source, String start, String end)
	{
		return cut(source, start, end, true);
	}

	public static String cut(final String source, String start, String end, boolean inverse)
	{
		String cut = null;
		int iLeft = source.indexOf(start);

		int iRight = 0;
		if (inverse) {
			while (source.indexOf(end, iRight + 1) != - 1)
				iRight = source.indexOf(end, iRight + 1);
		}
		else
			iRight = source.indexOf(end);

		if (iLeft != - 1 && iRight != - 1)
			cut = source.substring(iLeft + start.length(), iRight);
		else
			cut = source;

		return cut;
	}

	public static String trimSpace(final String source)
	{
		return source.replaceAll(" ", "");
	}

	public static String toString(@SuppressWarnings("rawtypes") final List list)
	{
		StringBuilder toString = new StringBuilder();

		for (Object e : list)
			toString.append(e + "\n");

		return toString.toString();
	}

	public static String toString(@SuppressWarnings("rawtypes") final Map map)
	{
		StringBuilder toString = new StringBuilder();

		for (Object k : map.keySet())
			toString.append(k + " = " + map.get(k) + "\n");

		return toString.toString();
	}

	private StringFn() {}
}
