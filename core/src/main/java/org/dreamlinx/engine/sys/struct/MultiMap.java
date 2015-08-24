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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * This Map offers multiple values for one key. It covers methods of a standard 
 * Map<K, V>, even though it not an instance of that. Constructors permit to define 
 * the kind of implementation of the collections of values you prefer - LinkedList 
 * is used as default.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MultiMap<K, V> implements Serializable {

	private static final long serialVersionUID = 8979501410936127456L;

	private Map<K, Collection<V>> map;
	@SuppressWarnings("rawtypes")
	private Class<? extends Collection> coll;

	public MultiMap() {

		this(LinkedList.class);
	}

	public MultiMap(
		@SuppressWarnings("rawtypes") Class<? extends Collection> collectionType) {

		map = new LinkedHashMap<K, Collection<V>>();
		this.coll = collectionType;
	}

	public MultiMap(int initialCapacity) {

		this(initialCapacity, LinkedList.class);
	}

	public MultiMap(int initialCapacity,
		@SuppressWarnings("rawtypes") Class<? extends Collection> collectionType) {

		map = new LinkedHashMap<K, Collection<V>>(initialCapacity);
		this.coll = collectionType;
	}

	public MultiMap(Map<K, Collection<V>> map) {

		this.map = map;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || ! (obj instanceof MultiMap))
			return false;

		return map.equals(((MultiMap<?, ?>) obj).map);
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	public void clear()
	{
		map.clear();
	}

	public boolean containsKey(K key)
	{
		return map.containsKey(key);
	}

	public boolean containsValue(V value)
	{
		for (K key : keySet())
			if (get(key).contains(value))
				return true;

		return false;
	}

	public boolean containsValue(K key, V value)
	{
		Collection<V> vals = map.get(key);
		if (vals != null && vals.contains(value))
			return true;

		return false;
	}

	public boolean containsValues(Collection<V> values)
	{
		for (V v : values) {

			boolean r = false;
			for (K key : keySet())
				if (r |= get(key).contains(v))
					break;

			if (! r)
				return false;
		}

		return true;
	}

	public Collection<V> get(K key)
	{
		return map.get(key);
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set<K> keySet()
	{
		return map.keySet();
	}

	public Collection<V> put(K key, V value)
	{
		Collection<V> vals = map.get(key);
		if (vals == null)
			map.put(key, (vals = newCollection()));

		vals.add(value);
		return vals;
	}

	public Collection<V> putAll(K key, Collection<V> values)
	{
		Collection<V> vals = map.get(key);
		if (vals == null)
			map.put(key, (vals = newCollection()));

		vals.addAll(values);
		return vals;
	}

	public Collection<V> remove(K key)
	{
		return map.remove(key);
	}

	public Collection<V> remove(K key, V value)
	{
		Collection<V> values = map.get(key);

		if (values != null) {
			values.remove(value);

			if (values.isEmpty())
				map.remove(key);
		}

		return values;
	}

	public int size()
	{
		return map.size();
	}

	public int size(K key)
	{
		Collection<V> vals = map.get(key);
		if (vals != null)
			return vals.size();

		return 0;
	}

	public Collection<V> values()
	{
		Collection<V> values = newCollection();

		for (K key : keySet())
			for (V value : get(key))
				values.add(value);

		return values;
	}

	@Override
	public String toString()
	{
		return map.toString();
	}

	//
	// Internal
	//

	@SuppressWarnings("unchecked")
	private Collection<V> newCollection()
	{
		try {
			return coll.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
