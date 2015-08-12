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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class HierarchyTree<K, V> implements Serializable {

	private static final long serialVersionUID = 5176363622843155684L;

	protected Node root;

	public HierarchyTree() {}

	public HierarchyTree(K rootKey, V rootValue) {

		root = new Node(rootKey, rootValue);
	}

	public void clear()
	{
		root = null;
	}

	public boolean contains(K key)
	{
		boolean contains = false;
		if (root != null)
			if (root.find(key) != null)
				contains = true;

		return contains;
	}

	protected List<Node> dynasty(Node node)
	{
		List<Node> dynasty = new LinkedList<Node>();
		for (Node n : node.daughters.values()) {
			dynasty.add(n);
			dynasty.addAll(dynasty(n));
		}

		return dynasty;
	}

	public V get(K key)
	{
		V value = null;
		if (root != null) {
			Node node = root.find(key);
			if (node != null)
				value = node.value;
		}

		return value;
	}

	public boolean isEmpty()
	{
		return (size() == 0) ? true : false;
	}

	public Set<K> keySet()
	{
		Set<K> keySet = new LinkedHashSet<K>();
		if (root != null) {
			keySet.add(root.key);
			for (Node node : dynasty(root))
				keySet.add(node.key);
		}

		return keySet;
	}

	public boolean merge(HierarchyTree<K, V> tree)
	{
		boolean merge = false;
		if (root != null && tree.root != null) {
			Node node = null;
			if ((node = root.find(tree.root.key)) != null) {
				root.value = tree.root.value;
				for (Node n : tree.root.daughters.values())
					node.daughters.put(n.key, n);

				merge = true;
			}
		}
		else {
			root = tree.root;
			merge = true;
		}

		return merge;
	}

	public boolean put(K father, K key, V value)
	{
		boolean put = false;
		if (this.root != null) {
			Node node = this.root.find(father);
			if (node != null) {
				Node daughter = new Node(key, value);
				daughter.father = father;
				node.daughters.put(daughter.key, daughter);

				put = true;
			}
		}

		return put;
	}

	public boolean remove(K key)
	{
		boolean remove = false;
		if (root != null) {
			Node node = null;
			if ((node = root.find(key)) != null) {
				if ((node = root.find(node.father)) != null) {
					node = node.daughters.remove(key);
					remove = true;
				}
			}
		}

		return remove;
	}

	public HierarchyTree<K, V> setRoot(K key, V value)
	{
		root = new Node(key, value);

		return this;
	}

	public int size()
	{
		return dynasty(root).size() + 1;
	}

	@Override
	public String toString()
	{
		return (root != null) ? root.toString() : ":{}";
	}

	public List<V> values()
	{
		List<V> values = new LinkedList<V>();
		if (root != null) {
			values.add(root.value);
			for (Node node : dynasty(root))
				values.add(node.value);
		}

		return values;
	}

	protected class Node implements Serializable {

		private static final long serialVersionUID = 4975003868194043062L;
		public K key;
		public V value;
		public K father;
		public Map<K, Node> daughters;

		public Node(K key, V value) {

			this.key = key;
			this.value = value;
			daughters = new LinkedHashMap<K, Node>();
		}

		public Node find(K key)
		{
			Node node = null;
			if (this.key.equals(key))
				node = this;
			else {
				node = daughters.get(key);
				if (node == null) {
					for (Node n : daughters.values()) {
						node = n.find(key);
						if (node != null)
							return node;
					}
				}
			}

			return node;
		}

		@Override
		public String toString()
		{
			return key + ":" + value + daughters;
		}
	}
}
