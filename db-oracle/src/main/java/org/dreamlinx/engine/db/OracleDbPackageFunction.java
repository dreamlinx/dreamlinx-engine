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

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;

import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.error.DatabaseException;

/**
 * For handling Oracle function.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class OracleDbPackageFunction extends DbFunction {

	private static final String FN_BLOCK = "begin %s %s%s.%s(%s); end;";

	private String packageName;

	public OracleDbPackageFunction(String packageName, String functionName) {

		this(packageName, functionName, OracleTypes.CURSOR);
	}

	public OracleDbPackageFunction(String packageName, String functionName, Integer outOracleTypes) {

		super(functionName, outOracleTypes);

		this.packageName = packageName;
	}

	@Override
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
			(hasOuput() ? "? := " : ""),
			packageName, functionName,
			paramsList.toString());
	}

	public String getPackageName()
	{
		return packageName;
	}

	//
	// Internal
	//

	@Override
	protected void createArrays(Connection conn) throws DatabaseException
	{
		if (parametersTypes == null)
			return; // skip

		try {
			OracleConnection oraConn = (OracleConnection) conn;

			for (String param : parametersTypes.keySet()) {
				String type = parametersTypes.get(param);

				Object raw = parameters.get(param);
				Validate.notNull(raw,
					"Value of parameter `%s` for type `%s` can not be null.",
					param, type);

				if (! raw.getClass().isArray())
					throw new IllegalArgumentException(String.format(
						"Parameter `%s` for type `%s` has not an array value.", param, type));

				parameters.put(param, oraConn.createARRAY(type, raw));
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	protected void freeArrays() throws DatabaseException
	{
		if (parametersTypes == null)
			return; // skip

		try {
			for (String param : parametersTypes.keySet()) {
				Object arr = parameters.get(param);
				if (arr instanceof Array || arr instanceof ARRAY)
					((Array) arr).free();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
}
