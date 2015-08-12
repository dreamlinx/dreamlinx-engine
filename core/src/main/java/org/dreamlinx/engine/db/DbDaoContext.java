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

package org.dreamlinx.engine.db;

import java.util.HashMap;
import java.util.Map;

import org.dreamlinx.engine.model.Model;

/**
 * Context for all instances of DbDao objects in a static way.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DbDaoContext {

	protected static Map<Class<? extends DbDao<? extends Model>>, DbDao<? extends Model>> context;
	static {
		context = new HashMap<>();
	}

	public static DbDao<? extends Model> get(Class<? extends DbDao<? extends Model>> daoClass)
		throws Exception
	{
		DbDao<? extends Model> dao = context.get(daoClass);
		if (dao == null)
			dao = renew(daoClass);

		return dao;
	}

	public static DbDao<? extends Model> renew(Class<? extends DbDao<? extends Model>> daoClass)
		throws Exception
	{
		DbDao<? extends Model> dao = daoClass.newInstance();
		context.put(daoClass, dao);

		return dao;
	}

	public static void free()
	{
		for (Class<? extends DbDao<? extends Model>> key : context.keySet()) {
			context.put(key, null);
			context.remove(key);
		}
	}
}
