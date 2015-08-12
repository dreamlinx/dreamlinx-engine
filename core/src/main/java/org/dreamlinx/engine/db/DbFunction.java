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

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.error.DatabaseException;

/**
 * Define a database function.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DbFunction {

	private static final String FN_BLOCK = "{%s call %s(%s)}";

	protected String functionName;
	protected Integer outputType;

	protected Map<String, Object> parameters;
	protected Map<String, String> parametersFn;
	protected Map<String, String> parametersTypes;

	public DbFunction(String functionName) {

		this(functionName, Types.OTHER);
	}

	public DbFunction(String functionName, Integer outputType) {

		this.functionName = functionName;
		this.outputType = outputType;

		parameters = new LinkedHashMap<>();
	}

	public String getFunction()
	{
		StringBuilder paramsList = new StringBuilder();

		if (hasParameters()) {
			for (String name : parameters.keySet()) {

				if (paramsList.length() != 0)
					paramsList.append(',');

				String fn;
				if (parametersFn != null && (fn = parametersFn.get(name)) != null) {
					paramsList.append(fn);
					paramsList.append("(?)");
				}
				else
					paramsList.append('?');
			}
		}

		return String.format(FN_BLOCK,
			(hasOuput() ? "? = " : ""),
			functionName, paramsList.toString());
	}

	public void addParameter(String name, Object value)
	{
		addParameter(name, value, null, null);
	}

	public void addParameter(String name, Object value, String function)
	{
		addParameter(name, value, function, null);
	}

	public void addParameter(String name, Object value, String function, String type)
	{
		Validate.notBlank(name, "name cannot be null");

		parameters.put(name, value);

		if (StringUtils.isNotBlank(function))
			getParametersFn().put(name, function.toUpperCase());

		if (StringUtils.isNotBlank(type))
			getParametersTypes().put(name, type.toUpperCase());
	}

	public String getFunctionName()
	{
		return functionName;
	}

	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	public Integer getOutputType()
	{
		return outputType;
	}

	public Boolean hasParameters()
	{
		return (parameters != null && ! parameters.isEmpty());
	}

	public Boolean hasOuput()
	{
		return (outputType != null);
	}

	@Override
	public String toString()
	{
		return "[" + getFunction() + "]";
	}

	//
	// Internal
	//

	protected void createArrays(Connection conn) throws DatabaseException
	{
		if (parametersTypes == null)
			return; // skip

		try {
			for (String param : parametersTypes.keySet()) {
				String type = parametersTypes.get(param);

				Object raw = parameters.get(param);
				Validate.notNull(raw,
					"Value of parameter `%s` for type `%s` can not be null.",
					param, type);

				if (! raw.getClass().isArray())
					throw new IllegalArgumentException(String.format(
						"Parameter `%s` for type `%s` has not an array value.", param, type));

				parameters.put(param, conn.createArrayOf(type, (Object[]) raw));
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	protected void freeArrays() throws DatabaseException
	{
		if (parametersTypes == null)
			return; // skip

		try {
			for (String param : parametersTypes.keySet()) {
				Object arr = parameters.get(param);
				if (arr instanceof Array)
					((Array) arr).free();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	protected Map<String, String> getParametersFn()
	{
		if (parametersFn == null)
			parametersFn = new LinkedHashMap<>();

		return parametersFn;
	}

	protected Map<String, String> getParametersTypes()
	{
		if (parametersTypes == null)
			parametersTypes = new LinkedHashMap<>();

		return parametersTypes;
	}
}
