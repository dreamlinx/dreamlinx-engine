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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Define a database SQL query.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DbSQLQuery {

	private String sqlQuery;
	private Map<String, Object> parameters;

	public DbSQLQuery(String sqlQuery) {

		setQuery(sqlQuery);
		parameters = new LinkedHashMap<>();
	}

	public void setQuery(String sqlQuery)
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		sqlQuery = sqlQuery.trim();
		if (sqlQuery.endsWith(";"))
			sqlQuery = sqlQuery.replaceAll("\\;", "");

		this.sqlQuery = sqlQuery;
	}

	public void addParameter(String name, Object value)
	{
		parameters.put(name, value);
	}

	public Boolean hasParameters()
	{
		return ! parameters.isEmpty();
	}

	public String getQuery()
	{
		return sqlQuery;
	}

	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters)
	{
		this.parameters = parameters;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
