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

package org.dreamlinx.engine.model;

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.fn.MurmurHashFn;

/**
 * This is a strong and unique key from values in constructor. This class encapsulates
 * an integer hash key built with the Murmur hash function, and a plain string key built
 * appending all values together. The {@link #equals(Object)} and compare method use the
 * hash key first and, if it collides, the string key as safe and strong way to solve
 * the ambiguity. The {@link #compare(Key)} methods sort keys using the string key only;
 * the order of values is mandatory. The {@link #hashCode()} returns the hash key instead.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Key implements Serializable, Comparable<Key> {

	private static final long serialVersionUID = - 789478276238220197L;

	protected int hashKey;
	protected String stringKey;

	public Key(Object... values) {

		Validate.notEmpty(values, "values cannot be empty.");

		// Creating the string key
		StringBuilder keyBuilder = new StringBuilder();
		for (Object v : values) {

			if (keyBuilder.length() != 0)
				keyBuilder.append("|");

			if (v != null)
				keyBuilder.append(v.toString());
		}
		stringKey = keyBuilder.toString();

		// Calculating the numeric key
		if (keyBuilder.length() == 0)
			hashKey = 0;
		else
			hashKey = MurmurHashFn.hash32(stringKey);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || ! (obj instanceof Key))
			return false;

		// Comparing over numeric key
		Key key = (Key) obj;
		if (hashKey != key.hashKey)
			return false;

		// Comparing over string key, when numeric key has collided
		return (stringKey.equals(key.stringKey));
	}

	@Override
	public int hashCode()
	{
		return hashKey;
	}

	@Override
	public int compareTo(Key obj)
	{
		if (obj == null)
			return - 1;

		return (stringKey.compareTo(((Key) obj).stringKey));
	}

	@Override
	public String toString()
	{
		return "{" + hashKey + ":[" + stringKey + "]}";
	}
}
