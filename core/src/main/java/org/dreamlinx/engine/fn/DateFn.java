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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Functions for handling dates.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class DateFn {

	/**
	 * All possible modes for dates comparisons.
	 * 
	 * @author Marco Merli
	 * @since 1.0
	 */
	public enum Mode {

		BEFORE,
		BEFORE_EQUALS,
		EQUALS,
		AFTER,
		AFTER_EQUALS;
	}

	/**
	 * Compare the first date with the second, in the way expressed by
	 * comparison mode value. Expression: "a [mode] b".
	 * 
	 * @param Date a
	 * @param Date b
	 * @param Mode mode
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean compare(Date a, Mode mode, Date b)
	{
		Validate.notNull(mode, "comparison mode cannot be null");

		if (a == null || b == null)
			return false;

		switch (mode) {

			case BEFORE:
				return (a.compareTo(b) < 0);

			case BEFORE_EQUALS:
				return (a.compareTo(b) <= 0);

			case EQUALS:
				return (a.compareTo(b) == 0);

			case AFTER:
				return (a.compareTo(b) > 0);

			case AFTER_EQUALS:
				return (a.compareTo(b) >= 0);

			default:
				return false;
		}
	}

	/**
	 * Compare the input date with today, in the way expressed by
	 * comparison mode value. Expression: "date [mode] today()".
	 * 
	 * @param Date
	 * @param Mode
	 * @return boolean
	 */
	public static boolean compareToday(Date date, Mode mode)
	{
		return compare(date, mode, today());
	}

	/**
	 * Returns the current date stripped of time.
	 * 
	 * @return Date
	 */
	public static Date today()
	{
		return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
	}

	private DateFn() {}
}
