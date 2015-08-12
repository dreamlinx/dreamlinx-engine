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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.error.DatabaseException;
import org.dreamlinx.engine.model.Key;
import org.dreamlinx.engine.model.Model;

/**
 * Extension of database support for operation with ROWIDs.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class DbDaoRowId<M extends Model> extends DbDao<M> {

	/**
	 * ORA-01795 is thrown when this limit is excited
	 */
	int DEFAULT_MAX_PARAM_LIST = 1000;

	/**
	 * Retrieve a map of Models from a function by a list of rowids. The
	 * function must have only a parameter of Oracle type `T_LIST_ROWID`.
	 * 
	 * Es. PK_DUMMY.doIt(p_rowids T_LIST_ROWID)
	 * 
	 * @param DbFunction
	 * @param List<String> rowIds
	 * @return Map<Key, M>
	 * @throws DatabaseException
	 */
	protected final Map<Key, M> queryForModelMap(DbFunction function, List<String> rowIds)
		throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notEmpty(rowIds, "rowIds cannot be empty");

		Map<Key, M> map = null;
		if (rowIds.size() > DEFAULT_MAX_PARAM_LIST) {

			for (int split = 0; split < sizeOfSplits(rowIds); split++) {
				List<String> splits = splitRows(rowIds, split);

				if (map == null) {
					map = _queryForModelMap(function, splits);
				}
				else {
					Map<Key, M> subMap = _queryForModelMap(function, splits);

					if (subMap != null && map != null)
						map.putAll(subMap);
					else if (subMap != null)
						map = subMap;
				}
			}
		}
		else
			map = _queryForModelMap(function, rowIds);

		return map;
	}

	/**
	 * Retrieve a list of Models from a function by a list of rowids. The
	 * function must have only a parameter of Oracle type `T_LIST_ROWID`.
	 * 
	 * Es. PK_DUMMY.doIt(p_rowids T_LIST_ROWID)
	 * 
	 * @param DbFunction
	 * @param List<String> rowIds
	 * @return List<M>
	 * @throws DatabaseException
	 */
	protected final List<M> queryForModelList(DbFunction function, List<String> rowIds)
		throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notEmpty(rowIds, "rowIds cannot be empty");

		List<M> list = null;
		if (rowIds.size() > DEFAULT_MAX_PARAM_LIST) {

			for (int split = 0; split < sizeOfSplits(rowIds); split++) {
				List<String> splits = splitRows(rowIds, split);

				if (list == null) {
					list = _queryForModelList(function, splits);
				}
				else {
					List<M> subList = _queryForModelList(function, splits);

					if (subList != null && list != null)
						list.addAll(subList);
					else if (subList != null)
						list = subList;
				}
			}
		}
		else
			list = _queryForModelList(function, rowIds);

		return list;
	}

	//
	// Internal
	//

	private int sizeOfSplits(List<String> rowIds)
	{
		return (int) Math.ceil((double) rowIds.size() / DEFAULT_MAX_PARAM_LIST);
	}

	private List<String> splitRows(List<String> rowIds, int split)
	{
		final int fromIdx = (split * DEFAULT_MAX_PARAM_LIST);
		int toIdx = ((split * DEFAULT_MAX_PARAM_LIST) + DEFAULT_MAX_PARAM_LIST);
		if (toIdx >= rowIds.size())
			toIdx = rowIds.size();

		return rowIds.subList(fromIdx, toIdx);
	}

	private Map<Key, M> _queryForModelMap(DbFunction fn, List<String> rowIds)
		throws DatabaseException
	{
		fn.getParameters().clear();

		String[] arr = rowIds.toArray(new String[rowIds.size()]);
		fn.addParameter("p_rowids", arr, null, "T_LIST_ROWID");

		return queryForModelMap(fn, rowIds.size());
	}

	private List<M> _queryForModelList(DbFunction fn, List<String> rowIds)
		throws DatabaseException
	{
		fn.getParameters().clear();

		String[] arr = rowIds.toArray(new String[rowIds.size()]);
		fn.addParameter("p_rowids", arr, null, "T_LIST_ROWID");

		return queryForModelList(fn, rowIds.size());
	}
}
