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

import java.lang.management.ManagementFactory;

/**
 * Retrieve system informations.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class SysFn {

	private static final Integer pid;
	private static final String hostname;
	static {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String msg = "Runtime name '%s' is not in the form of 'pid@hostname': OS not supported maybe.";

		if (! name.contains("@"))
			throw new IllegalStateException(String.format(msg, name));

		String[] splits = name.split("@");
		if (splits.length != 2)
			throw new IllegalStateException(String.format(msg, name));

		pid = new Integer(splits[0]);
		hostname = splits[1];
	}

	public static Integer getPid()
	{
		return pid;
	}

	public static String getHostname()
	{
		return hostname;
	}

	private SysFn() {}
}
