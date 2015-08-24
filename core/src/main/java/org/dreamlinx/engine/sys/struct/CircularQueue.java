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

import java.util.LinkedList;

/**
 * A first-in first-out queue with a fixed maximum size that replaces
 * its oldest element if full. The add method is now synchronized.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class CircularQueue<E> extends LinkedList<E> {

	private static final long serialVersionUID = - 7179145557688283651L;
	private int maxSize;

	public CircularQueue(int maxSize) {

		this.maxSize = maxSize;
	}

	@Override
	public synchronized boolean add(E o)
	{
		boolean added = super.add(o);
		while (added && size() > maxSize)
			removeFirst();

		return added;
	}
}
