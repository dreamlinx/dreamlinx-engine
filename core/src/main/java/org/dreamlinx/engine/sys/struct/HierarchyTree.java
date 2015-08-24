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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * It offers a binary tree adding a map of nodes.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class HierarchyTree<K, V> implements Serializable {

	private static final long serialVersionUID = 5176363622843155684L;

	protected TreeNode<K, V> rootNode;
	protected Map<K, TreeNode<K, V>> nodeMap;

	protected int size = 1;
	protected int deep = 0;

	public HierarchyTree(K rootKey, V rootValue) {

		rootNode = new TreeNode<>(rootKey, rootValue);
		(nodeMap = new LinkedHashMap<>()).put(rootKey, rootNode);
	}

	public TreeNode<K, V> find(K key)
	{
		return rootNode.find(key);
	}

	public boolean contains(K key)
	{
		return nodeMap.containsKey(key);
	}

	public V get(K key)
	{
		TreeNode<K, V> node = nodeMap.get(key);
		if (node != null)
			return node.value;

		return null;
	}

	public boolean put(K father, K key, V value)
	{
		TreeNode<K, V> node = nodeMap.get(key);
		if (node != null
			&& nodeMap.containsKey(father)
			&& node.father.equals(father)) {

			node.value = value;
			return true;
		}
		else {
			node = find(father);
			if (node != null) {

				TreeNode<K, V> newNode = new TreeNode<>(key, value, node);
				node.add(newNode);
				nodeMap.put(key, newNode);

				size++;
				if (newNode.level > deep)
					deep = newNode.level;

				return true;
			}
		}

		return false;
	}

	public V remove(K key)
	{
		TreeNode<K, V> node = nodeMap.get(key);
		if (node != null) {

			List<TreeNode<K, V>> sons = progeny(node);
			for (TreeNode<K, V> son : sons) {
				son.sons.clear();
				nodeMap.remove(son.key);
			}

			if (node.father != null)
				node.father.sons.remove(key);

			nodeMap.remove(key);
			size -= sons.size() + 1;

			return node.value;
		}

		return null;
	}

	public boolean merge(HierarchyTree<K, V> tree)
	{
		TreeNode<K, V> a = null;
		if ((a = find(tree.rootNode.key)) != null) {

			a.value = tree.rootNode.value;
			tree.rootNode.level = a.level;

			for (TreeNode<K, V> b : tree.rootNode.sons.values())
				a.add(b);

			for (TreeNode<K, V> b : tree.progeny(tree.rootNode)) {

				TreeNode<K, V> aa;
				if ((aa = nodeMap.get(b.key)) == null) {
					b.level += tree.rootNode.level;

					nodeMap.put(b.key, b);

					size++;
					if (b.level > deep)
						deep = b.level;
				}
				else
					aa.value = b.value;
			}

			return true;
		}

		return false;
	}

	public void clear(K rootKey, V rootValue)
	{
		for (TreeNode<K, V> son : nodeMap.values())
			son.sons.clear();

		nodeMap.clear();
		rootNode = new TreeNode<>(rootKey, rootValue);
		nodeMap.put(rootKey, rootNode);

		size = 1;
		deep = 1;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || ! (obj instanceof HierarchyTree))
			return false;

		HierarchyTree<?, ?> tree = (HierarchyTree<?, ?>) obj;
		if (tree.size != size || tree.deep != deep)
			return false;

		return nodeMap.equals(tree.nodeMap);
	}

	@Override
	public int hashCode()
	{
		int hash = 0x11;
		for (TreeNode<K, V> node : nodeMap.values())
			hash = (hash * 31) + node.hashCode();

		return hash;
	}

	public boolean isEmpty()
	{
		return (size == 0);
	}

	public int size()
	{
		return size;
	}

	public int deep()
	{
		return deep;
	}

	public List<TreeNode<K, V>> ancestry(TreeNode<K, V> node)
	{
		List<TreeNode<K, V>> ancestry = new ArrayList<>(size);

		TreeNode<K, V> father;
		while ((father = node.father) != null)
			ancestry.add(father);

		return ancestry;
	}

	public List<TreeNode<K, V>> progeny(TreeNode<K, V> node)
	{
		List<TreeNode<K, V>> progeny = new ArrayList<>(size);

		for (TreeNode<K, V> n : node.sons.values()) {
			progeny.add(n);
			progeny.addAll(progeny(n));
		}

		return progeny;
	}

	public Set<K> keySet()
	{
		return nodeMap.keySet();
	}

	public List<V> values()
	{
		List<V> values = new ArrayList<>(size);
		values.add(rootNode.value);

		for (TreeNode<K, V> node : progeny(rootNode))
			values.add(node.value);

		return values;
	}

	@Override
	public String toString()
	{
		return rootNode.toString();
	}
}
