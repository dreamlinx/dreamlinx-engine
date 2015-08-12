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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dreamlinx.engine.db.DbColumnSkip;

/**
 * Prototype for building models with basic supports.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class Model implements Serializable {

	private static final long serialVersionUID = 5643258163987348741L;

	@DbColumnSkip
	private Key key;

	/**
	 * The own Key of the Model.
	 * 
	 * @return Key
	 */
	public final Key getKey()
	{
		if (key == null)
			key = new Key(defineKey());

		return key;
	}

	/**
	 * Refresh the Key of the Model.
	 * 
	 * @return Key
	 */
	public final Key refreshKey()
	{
		return (key = new Key(defineKey()));
	}

	/**
	 * Force the Model to define values of its Key.
	 * 
	 * @return Object[]
	 */
	protected abstract Object[] defineKey();

	/**
	 * This method is invoked after the `automagic` binding.
	 * It is a empty method here - override it if you have to do.
	 * 
	 * @throws Exception
	 */
	public void aftermath() throws Exception
	{}

	@Override
	public final boolean equals(Object obj)
	{
		if (obj == null || ! (obj.getClass().isInstance(this)))
			return false;

		return getKey().equals(((Model) obj).getKey());
	}

	@Override
	public final int hashCode()
	{
		return getKey().hashCode();
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
