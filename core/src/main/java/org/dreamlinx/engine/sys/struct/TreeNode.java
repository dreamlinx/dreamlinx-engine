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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class TreeNode<K, V> implements Serializable {

	private static final long serialVersionUID = 1385367675734224139L;

	protected int level = 0;
	protected K key;
	protected V value;
	protected K father;
	protected Map<K, TreeNode<K, V>> sons;

	protected TreeNode(K key, V value) {

		this(key, value, null);
	}

	protected TreeNode(K key, V value, TreeNode<K, V> father) {

		this.key = key;
		this.value = value;

		if (father != null) {
			this.father = father.key;
			level = (father.level + 1);
		}

		sons = new LinkedHashMap<>();
	}

	protected TreeNode<K, V> find(K key)
	{
		TreeNode<K, V> node;
		if (this.key.equals(key)) {
			node = this;
		}
		else {
			node = sons.get(key);
			if (node == null) {
				for (TreeNode<K, V> n : sons.values()) {
					node = n.find(key);
					if (node != null)
						break;
				}
			}
		}

		return node;
	}

	protected void add(TreeNode<K, V> son)
	{
		sons.put(son.key, son);
	}

	public boolean isRoot()
	{
		return (father == null);
	}

	public boolean isLeaf()
	{
		return (sons.isEmpty());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || ! (obj instanceof TreeNode))
			return false;

		TreeNode<?, ?> node = (TreeNode<?, ?>) obj;
		if (father != null && ! father.equals(node.father))
			return false;

		return key.equals(node.key)
			&& value.equals(node.value);
	}

	@Override
	public int hashCode()
	{
		return (Objects.hashCode(key) ^ Objects.hashCode(value));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (TreeNode<?, ?> son : sons.values()) {

			if (sb.length() == 0) {
				sb.append('\n');
			}
			else
				sb.append('\n');

			sb.append(StringUtils.repeat('\t', level + 1));
			sb.append(son.toString());
		}

		return String.format("{%d:%s[%s]}",
			key, value, sb.toString());
	}
}
