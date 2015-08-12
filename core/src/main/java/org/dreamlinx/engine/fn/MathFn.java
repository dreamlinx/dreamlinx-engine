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

/**
 * Functions in addiction to standard math.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MathFn {

	public static boolean notZero(Number n)
	{
		return (n != null && n.longValue() != 0);
	}

	public static Long fatt(Number n)
	{
		long nl = (n != null) ? n.longValue() : 0L;

		if (nl < 0)
			throw new IllegalArgumentException(
				"Not defined for negative numbers.");

		if (nl == 0)
			return 1L;

		return (nl * fatt(nl - 1));
	}

	public static Long nPk(Number n, Number k)
	{
		long nl = (n != null) ? n.longValue() : 0L;
		long kl = (k != null) ? k.longValue() : 0L;

		return fatt(nl) / fatt(nl - kl);
	}

	public static Long nCk(Number n, Number k)
	{
		long nl = (n != null) ? n.longValue() : 0L;
		long kl = (k != null) ? k.longValue() : 0L;

		return nPk(nl, kl) * 1 / fatt(kl);
	}

	private MathFn() {}
}
