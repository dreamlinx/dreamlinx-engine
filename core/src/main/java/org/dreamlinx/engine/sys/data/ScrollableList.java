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

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ScrollableList<E> extends LinkedList<E> implements Serializable {

	private static final long serialVersionUID = 4680084538952772754L;
	private transient int index = - 1;

	public E next()
	{
		index++;
		if (index >= size())
			index = 0;

		return get(index);
	}

	public E prev()
	{
		index--;
		if (index < 0)
			index = size() - 1;

		return get(index);
	}
}
