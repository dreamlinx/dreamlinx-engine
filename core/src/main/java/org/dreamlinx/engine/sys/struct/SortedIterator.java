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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class SortedIterator<M extends Comparable<M>> implements Iterable<M> {

	private Iterator<M> it;

	public SortedIterator(Collection<M> collection) {

		this(collection, false);
	}

	public SortedIterator(Collection<M> collection, boolean reversed) {

		List<M> list = new ArrayList<>(collection);

		Collections.sort(list);
		if (reversed)
			Collections.reverse(list);

		it = list.iterator();
	}

	@Override
	public Iterator<M> iterator()
	{
		return it;
	}
}
