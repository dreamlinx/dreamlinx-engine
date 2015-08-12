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

package org.dreamlinx.engine;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * Support a unit test. It extends the standard assertions of JUnit,
 * adding additional over to them and others utilities.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class UnitTestSupport extends Assert {

	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	protected Logger logger = Logger.getLogger(getClass());

	//
	// Asserts
	//

	protected final void assertStringNotBlank(String string)
	{
		assertStringNotBlank(string, null);
	}

	protected final void assertStringNotBlank(String string, String format, Object... args)
	{
		assertFalse(parseMessage(format, args),
			StringUtils.isBlank(string));
	}

	protected final void assertNumberNotZero(Number number)
	{
		assertNumberNotZero(number, null);
	}

	protected final void assertNumberNotZero(Number number, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			number != null && number.longValue() != 0);
	}

	protected final void assertNotEmpty(Object[] arr)
	{
		assertNotEmpty(arr, null);
	}

	protected final void assertNotEmpty(Object[] arr, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			arr != null && arr.length > 0);
	}

	protected final void assertNotEmpty(Collection<?> coll)
	{
		assertNotEmpty(coll, null);
	}

	protected final void assertNotEmpty(Collection<?> coll, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			coll != null && ! coll.isEmpty());
	}

	protected final void assertNotEmpty(Map<?, ?> map)
	{
		assertNotEmpty(map, null);
	}

	protected final void assertNotEmpty(Map<?, ?> map, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			map != null && ! map.isEmpty());
	}

	protected final void failWhenExceptionExpected()
	{
		fail("Exception expected.");
	}

	protected final void failWhenExceptionNotExpected(Exception e)
	{
		logger.error(e.getMessage(), e);
		fail("Exception not expected.");
	}

	//
	// Print
	//

	protected final void println(Object object)
	{
		logger.info(object);
	}

	protected final void printf(String format, Object... args)
	{
		logger.info(parseMessage(format, args));
	}

	protected final void printe(Throwable e)
	{
		logger.error(e.getMessage(), e);
	}

	//
	// Util
	//

	protected Date toDate(String yyyyMMdd) throws Exception
	{
		return df.parse(yyyyMMdd);
	}

	protected File tempDir()
	{
		return new File(System.getProperty("java.io.tmpdir"));
	}

	//
	// Internal
	//

	private String parseMessage(String format, Object... args)
	{
		String message = null;
		if (StringUtils.isNotBlank(format))
			message = String.format(format, args);

		return message;
	}
}
