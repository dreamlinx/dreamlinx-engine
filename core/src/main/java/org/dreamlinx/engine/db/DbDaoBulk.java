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

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.error.DatabaseException;
import org.dreamlinx.engine.fn.MathFn;
import org.dreamlinx.engine.model.Model;

/**
 * Extension of database support for operation with ROWIDs.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class DbDaoBulk<M extends Model> extends DbDao<M> {

	/**
	 * Default commit size for bulk operations.
	 */
	private int commitSize = 1000;

	public DbDaoBulk() {

		Integer customCommitSize = connectionPool.getProperties().getCommitSize();
		if (MathFn.notZero(customCommitSize))
			commitSize = customCommitSize;
	}

	/**
	 * Execute a function with commit/rollback. The function must declare names
	 * of the field of the model as parameter names (without any values - it is
	 * just a marker). The function can have result output; if it has not result,
	 * the ojbectType have to be null.
	 * 
	 * @param DbFunction
	 * @param List<M> models
	 * @param Class<T> retType
	 * @return T[]
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T[] executeBulk(DbFunction function, List<M> models, Class<T> objectType)
		throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notEmpty(models, "models cannot be empty");

		boolean hasResult;
		T[] res = null;
		int iRes = 0;

		final int splits = (int) Math.ceil((double) models.size() / commitSize);
		if (hasResult = (objectType != null))
			res = (T[]) java.lang.reflect.Array.newInstance(objectType, splits);

		if (splits > 1) {
			for (int split = 0; split < splits; split++) {

				final int fromIdx = (split * commitSize);
				int toIdx = ((split * commitSize) + commitSize);
				if (toIdx >= models.size())
					toIdx = models.size();

				List<M> splitModels = models.subList(fromIdx, toIdx);
				T r = _executeBulk(function, splitModels, objectType);
				if (hasResult)
					res[iRes++] = r;
			}
		}
		else {
			T r = _executeBulk(function, models, objectType);
			if (hasResult)
				res[iRes] = r;
		}

		return res;
	}

	//
	// Internal
	//

	private <T> T _executeBulk(DbFunction fn, List<M> mods, Class<T> type)
		throws DatabaseException
	{
		Map<String, Object> params = fn.getParameters();
		for (String param : params.keySet()) {

			Object[] data = new Object[mods.size()];
			Method getMeth = columnGetterMapping.get(param);
			Class<?> getType = columnTypeMapping.get(param);

			// Prepare data values
			for (int iData = 0; iData < mods.size(); iData++) {

				Object val = null;
				try {
					val = data[iData] = getMeth.invoke(mods.get(iData));
				}
				catch (Exception e) {}

				if (val != null && getType.isAssignableFrom(Date.class))
					data[iData] = new java.sql.Timestamp(((Date) val).getTime());
			}
			params.put(param, data);

			// Prepare database types
			String oraType;
			try {
				getType.asSubclass(Number.class);
				oraType = "T_NUMBER_T";
			}
			catch (Exception e) {

				if (getType.isAssignableFrom(Boolean.class)) {
					oraType = "T_NUMBER_T";
				}
				else if (getType.isAssignableFrom(String.class)) {
					oraType = "T_VARCHAR2_T";
				}
				else if (getType.isAssignableFrom(Date.class)) {
					oraType = "T_DATE_T";
				}
				else
					throw new DatabaseException(
						"No such Oracle type found for bind the Java type " + getType);
			}
			fn.getParametersTypes().put(param, oraType);
		}

		return execute(fn, type);
	}
}
