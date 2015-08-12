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
public class StringFnTest extends UnitTestSupport {

	private static final String STRING = "Linux linux LiNuX";

	@Test
	public void trimSpace()
	{
		String trimmed = StringFn.trimSpace(STRING);

		assertNotNull(trimmed);
		assertNotSame(trimmed.length(), STRING.length());
	}

	@Test
	public void content()
	{
		char[] chars = StringFn.content(STRING);
		char[] aChars = StringFn.content(STRING);

		assertNotNull(chars);
		assertNotNull(aChars);
		assertEquals(STRING.length(), chars.length);
		assertEquals(chars.length, aChars.length);
	}

	@Test
	public void cut()
	{
		String seq = "{:{;{~};}:}";

		assertNotNull(StringFn.cut(seq, '{', '}'));
		assertEquals(9, StringFn.cut(seq, '{', '}').length());
		assertEquals(5, StringFn.cut(seq, '{', '}', false).length());
		assertEquals(3, StringFn.cut(seq, ':', '~').length());
		assertEquals(11, StringFn.cut(seq, '[', ']').length());
		assertEquals(11, StringFn.cut(seq, '[', ']', false).length());
	}

	@Test
	public void cutString()
	{
		String seq = "{:{;{~};}:}";

		assertNotNull(StringFn.cut(seq, "{:", "~}"));
		assertEquals(7, StringFn.cut(seq, "{:", ":}").length());
		assertEquals(3, StringFn.cut(seq, "{:", "~}").length());
		assertEquals(1, StringFn.cut(seq, "{:{;{", "};}:}").length());

		assertEquals(17, StringFn.cut("a{b{}, c{d{e{}}, f{}}", "{", "}}").length());
		assertEquals(11, StringFn.cut("a{b{}, c{d{e{}}, f{}}", "{", "}}", false).length());
	}
}
