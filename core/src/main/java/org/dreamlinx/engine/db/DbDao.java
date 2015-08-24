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

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Engine;
import org.dreamlinx.engine.core.Log;
import org.dreamlinx.engine.error.CollisionException;
import org.dreamlinx.engine.error.DatabaseException;
import org.dreamlinx.engine.error.InitializationException;
import org.dreamlinx.engine.model.Key;
import org.dreamlinx.engine.model.Model;
import org.dreamlinx.engine.sys.struct.AutoCloseableIterator;

/**
 * Support for database interaction.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @author Giuseppe Avola
 * @since 1.0
 */
public abstract class DbDao<M extends Model> {

	private static final Logger logger = Log.getEngineLogger();

	static final int MAP_INIT_SIZE = 512;
	static DbConnectionPool connectionPool;

	Class<M> modelClass;
	Map<String, Method> columnSetterMapping;
	Map<String, Method> columnGetterMapping;
	Map<String, Class<?>> columnTypeMapping;
	Properties sqlQueryMapping;

	//
	// Init
	//

	@SuppressWarnings("unchecked")
	public DbDao() {

		try {
			if (connectionPool == null)
				throw new InitializationException(DbDao.class);

			// Generic class of the Model
			ParameterizedType paramType = (ParameterizedType) getClass().getGenericSuperclass();
			modelClass = (Class<M>) paramType.getActualTypeArguments()[0];

			// Mapping for binding column with setter methods of the Model
			columnSetterMapping = new LinkedHashMap<>();
			columnGetterMapping = new LinkedHashMap<>();
			columnTypeMapping = new LinkedHashMap<>();

			// Recursive immersion over all fields
			Class<?> currClass = modelClass;
			while (currClass != null) {

				for (Field fld : currClass.getDeclaredFields()) {
					Class<?> columnType = fld.getType();

					// Override the column type, if it presents.
					Annotation annType = null;
					if ((annType = fld.getAnnotation(DbColumnType.class)) != null)
						columnType = ((DbColumnType) annType).value();

					// Prepare setter method with annotations values.
					Annotation annName = fld.getAnnotation(DbColumnName.class);
					if (annName != null) {

						// Column type
						String columnName = ((DbColumnName) annName).value();
						columnTypeMapping.put(columnName, columnType);

						// Column setter
						String name = "set" + StringUtils.capitalize(fld.getName());
						Method meth = currClass.getMethod(name, columnType);
						columnSetterMapping.put(columnName, meth);

						// Column getter
						if (columnType.isAssignableFrom(Boolean.class))
							name = "is" + StringUtils.capitalize(fld.getName());
						else
							name = "get" + StringUtils.capitalize(fld.getName());

						meth = currClass.getMethod(name);
						columnGetterMapping.put(columnName, meth);
					}
				}

				currClass = currClass.getSuperclass();
			}

			// XML queries file
			String xmlFile = getClass().getPackage().getName() + "." + getClass().getSimpleName();
			xmlFile = xmlFile.replace(".", "/");
			xmlFile = "/" + xmlFile + ".xml";

			InputStream xmlIn = getClass().getResourceAsStream(xmlFile);
			if (xmlIn != null) {
				sqlQueryMapping = new Properties();
				sqlQueryMapping.loadFromXML(xmlIn);
				xmlIn.close();
			}
		}
		catch (Exception e) {
			throw new InitializationException(e);
		}
	}

	public static void init(DbConnectionPool connectionPool) throws DatabaseException
	{
		DbDao.connectionPool = connectionPool;
		DbDao.connectionPool.init();
	}

	public static void shutdown() throws DatabaseException
	{
		DbDao.connectionPool.shutdown();
	}

	//
	// Supplied
	//

	/**
	 * Retrieve a single Model from the function. Throws exeption when more
	 * then one row are returned.
	 * 
	 * @param DbFunction
	 * @return M
	 * @throws DatabaseException
	 */
	protected final M queryForModel(DbFunction function) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(function.getOutputType().equals(Types.OTHER),
			"function outputType must be of Types.OTHER");

		M model = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			function.createArrays(conn);
			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			rs = (ResultSet) stmt.getObject(1);
			if (rs != null && rs.next()) {

				model = queryForModel(rs);

				if (rs != null && rs.next())
					throw new IllegalStateException("Function returns more then one row.");
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			function.freeArrays();
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return model;
	}

	/**
	 * Retrieve a single Model from the SQL query. Throws exeption when more
	 * then one row are returned.
	 * 
	 * @param DbSQLQuery
	 * @return M
	 * @throws DatabaseException
	 */
	protected final M queryForModel(DbSQLQuery sqlQuery) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		M model = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());
			prepareParams(stmt, sqlQuery.getParameters(), 1);

			rs = stmt.executeQuery();
			if (rs != null && rs.next()) {

				model = queryForModel(rs);

				if (rs != null && rs.next())
					throw new IllegalStateException("SQL query returns more then one row.");
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return model;
	}

	/**
	 * Retrieve a map of Models from a function. The data are stored in a
	 * ordered map, where the key is the real entity key and the value is a
	 * filled instance of the relative Model object.
	 * 
	 * @param DbFunction
	 * @return Map<Key, M extends Model>
	 * @throws DatabaseException
	 */
	protected final Map<Key, M> queryForModelMap(DbFunction function) throws DatabaseException
	{
		return queryForModelMap(function, null);
	}

	/**
	 * Retrieve a map of Models from a function. The data are stored in a
	 * ordered map, where the key is the real entity key and the value is a
	 * filled instance of the relative Model object. The size parameters
	 * permits to optimize the creation of the map with the expected size of
	 * the interrogation.
	 * 
	 * @param DbFunction function
	 * @param Number size
	 * @return Map<Key, M extends Model>
	 * @throws DatabaseException
	 */
	protected final Map<Key, M> queryForModelMap(DbFunction function, Number size) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(function.getOutputType().equals(Types.OTHER),
			"function outputType must be of Types.OTHER");

		Map<Key, M> map = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			function.createArrays(conn);
			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			rs = (ResultSet) stmt.getObject(1);
			if (rs != null && rs.next()) {
				map = newMap(size);

				do {
					M model = queryForModel(rs);

					Key key = model.getKey();
					if (Engine.isSelfCheckMode())
						if (map.containsKey(key))
							throw new CollisionException(key.hashCode(), model, map.get(key));

					map.put(key, model);
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			function.freeArrays();
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return map;
	}

	/**
	 * Retrieve a map of Models from a query. The data are stored in a ordered
	 * map, where the key is the real entity key and the value is a filled
	 * instance of the relative Model object. Fields of SQL SELECT must match
	 * with the DbColumnName of the Model.
	 * 
	 * @param DbSQLQuery sqlQuery
	 * @return Map<Key, M extends Model>
	 * @throws DatabaseException
	 */
	protected final Map<Key, M> queryForModelMap(DbSQLQuery sqlQuery) throws DatabaseException
	{
		return queryForModelMap(sqlQuery, null);
	}

	/**
	 * Retrieve a map of Models from a query. The data are stored in a ordered
	 * map, where the key is the real entity key and the value is a filled
	 * instance of the relative Model object. Fields of SQL SELECT must match
	 * with the DbColumnName of the Model. The size parameters permits to
	 * optimize the creation of the map with the expected size of the
	 * interrogation.
	 * 
	 * @param DbSQLQuery sqlQuery
	 * @param Number size
	 * @return Map<Key, M extends Model>
	 * @throws DatabaseException
	 */
	protected final Map<Key, M> queryForModelMap(DbSQLQuery sqlQuery, Number size) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		Map<Key, M> map = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());
			prepareParams(stmt, sqlQuery.getParameters(), 1);
			rs = stmt.executeQuery();

			if (rs != null && rs.next()) {
				map = newMap(size);

				do {
					M model = queryForModel(rs);

					Key key = model.getKey();
					if (Engine.isSelfCheckMode())
						if (map.containsKey(key))
							throw new CollisionException(key.hashCode(), model, map.get(key));

					map.put(key, model);
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return map;
	}

	/**
	 * Retrieve a list of Models from a function.
	 * 
	 * @param DbFunction
	 * @return List<M extends Model>
	 * @throws DatabaseException
	 */
	protected final List<M> queryForModelList(DbFunction function) throws DatabaseException
	{
		return queryForModelList(function, null);
	}

	/**
	 * Retrieve a list of Models from a function.
	 * The list has a fixed initial size.
	 * 
	 * @param DbFunction function
	 * @param Number size
	 * @return List<M extends Model>
	 * @throws DatabaseException
	 */
	protected final List<M> queryForModelList(DbFunction function, Number size) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(function.getOutputType().equals(Types.OTHER),
			"function outputType must be of Types.OTHER");

		List<M> list = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			function.createArrays(conn);
			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			rs = (ResultSet) stmt.getObject(1);
			if (rs != null && rs.next()) {
				list = newList(size);

				do {
					list.add(queryForModel(rs));
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			function.freeArrays();
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return list;
	}

	/**
	 * Retrieve a list of Models from a query.
	 * 
	 * @param DbSQLQuery sqlQuery
	 * @return List<M extends Model>
	 * @throws DatabaseException
	 */
	protected final List<M> queryForModelList(DbSQLQuery sqlQuery) throws DatabaseException
	{
		return queryForModelList(sqlQuery, null);
	}

	/**
	 * Retrieve a list of Models from a query.
	 * The list has a fixed initial size.
	 * 
	 * @param DbSQLQuery sqlQuery
	 * @param Number size
	 * @return List<M extends Model>
	 * @throws DatabaseException
	 */
	protected final List<M> queryForModelList(DbSQLQuery sqlQuery, Number size) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		List<M> list = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());

			prepareParams(stmt, sqlQuery.getParameters(), 1);
			rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				list = newList(size);

				do {
					list.add(queryForModel(rs));
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return list;
	}

	/**
	 * Retrieve an iterator of Models from a query. Fields of SQL SELECT
	 * must match with the DbColumnName of the Model. The size parameters
	 * permits to optimize the creation of the map with the expected size
	 * of the interrogation. It is strongly recommended to use this iterator
	 * in a try-with-resources statement.
	 * 
	 * @param DbFunction function
	 * @return AutoCloseableIterator<M extends Model>
	 * @throws DatabaseException
	 */
	protected final AutoCloseableIterator<M> queryForModelIterator(DbFunction function) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(function.getOutputType().equals(Types.OTHER),
			"function outputType must be of Types.OTHER");

		AutoCloseableIterator<M> it = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			function.createArrays(conn);
			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			rs = (ResultSet) stmt.getObject(1);
			it = new ResultSetIterator(conn, stmt, rs);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}

		return it;
	}

	/**
	 * Retrieve an iterator of Models from a query. Fields of SQL SELECT
	 * must match with the DbColumnName of the Model. The size parameters
	 * permits to optimize the creation of the map with the expected size
	 * of the interrogation. It is strongly recommended to use this iterator
	 * in a try-with-resources statement.
	 * 
	 * @param DbSQLQuery sqlQuery
	 * @return AutoCloseableIterator<M extends Model>
	 * @throws DatabaseException
	 */
	protected final AutoCloseableIterator<M> queryForModelIterator(DbSQLQuery sqlQuery) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;
		AutoCloseableIterator<M> it = null;
		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());

			prepareParams(stmt, sqlQuery.getParameters(), 1);
			rs = stmt.executeQuery();
			it = new ResultSetIterator(conn, stmt, rs);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}

		return it;
	}

	/**
	 * Retrieve an object from a function. The object type returned is of the
	 * type provided as parameter. The query must returns only a row and the
	 * only the first column will be returned.
	 * 
	 * @param DbFunction
	 * @param Class<T>
	 * @return T
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T queryForObject(DbFunction function, Class<T> objectType) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(objectType, "objectType cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(! function.getOutputType().equals(Types.OTHER),
			"function outputType cannot be of Types.OTHER");

		T object = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			object = (T) stmt.getObject(1);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return object;
	}

	/**
	 * Retrieve an object from a query. The object type returned is of the
	 * type provided as parameter. The query must returns only a row and the
	 * only the first column will be returned.
	 * 
	 * @param DbSQLQuery
	 * @param Class<T>
	 * @return T
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T queryForObject(DbSQLQuery sqlQuery, Class<T> objectType) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");
		Validate.notNull(objectType, "objectType cannot be null");

		T object = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());

			prepareParams(stmt, sqlQuery.getParameters(), 1);
			rs = stmt.executeQuery();

			if (rs != null && rs.next()) {
				object = (T) getObject(rs, 1, objectType);

				if (rs != null && rs.next())
					throw new IllegalStateException("SQL query returns more then one row.");
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return object;
	}

	/**
	 * Retrieve a list from a function. The object type of the returned list is defined by
	 * the type provided as parameter.
	 * 
	 * @param DbFunction
	 * @param Class<T>
	 * @return List<T>
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> List<T> queryForObjectList(DbFunction function, Class<T> objectType) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");
		Validate.notNull(objectType, "objectType cannot be null");
		Validate.notNull(function.getOutputType(), "function outputType cannot be null");
		Validate.isTrue(! function.getOutputType().equals(Types.OTHER),
			"function outputType cannot be of Types.OTHER");

		List<T> list = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());
			stmt.registerOutParameter(1, function.getOutputType());

			prepareParams(stmt, function.getParameters(), 2);
			stmt.execute();

			rs = (ResultSet) stmt.getObject(1);
			if (rs != null && rs.next()) {
				list = new LinkedList<>();

				do {
					list.add((T) getObject(rs, 1, objectType));
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return list;
	}

	/**
	 * Retrieve a list from a query. The object type of the returned list is defined by
	 * the type provided as parameter.
	 * 
	 * @param DbSQLQuery
	 * @param Class<T>
	 * @return List<T>
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> List<T> queryForObjectList(DbSQLQuery sqlQuery, Class<T> objectType) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");
		Validate.notNull(objectType, "objectType cannot be null");

		List<T> list = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());

			prepareParams(stmt, sqlQuery.getParameters(), 1);
			rs = stmt.executeQuery();

			if (rs != null && rs.next()) {
				list = new LinkedList<>();

				do {
					list.add((T) getObject(rs, 1, objectType));
				}
				while (rs.next());
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return list;
	}

	/**
	 * Execute a function with commit/rollback. The function can have result
	 * output; if it has not result, the ojbectType have to be null.
	 * 
	 * @param DbFunction
	 * @param Class<T>
	 * @return T
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T execute(DbFunction function, Class<T> objectType) throws DatabaseException
	{
		Validate.notNull(function, "function cannot be null");

		T object = null;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareCall(function.getFunction());

			Integer startIndex = 1;
			if (function.hasOuput())
				stmt.registerOutParameter(startIndex++, function.getOutputType());

			function.createArrays(conn);
			prepareParams(stmt, function.getParameters(), startIndex);
			stmt.executeUpdate();

			if (function.hasOuput())
				object = (T) stmt.getObject(1);
		}
		catch (SQLException e) {
			connectionPool.rollback(conn);
			throw new DatabaseException(e);
		}
		finally {
			connectionPool.commit(conn);
			function.freeArrays();
			close(rs, stmt);
			connectionPool.close(conn);
		}

		return object;
	}

	/**
	 * Execute a query for update with commit/rollback. It returns the number
	 * of updated rows.
	 * 
	 * @param DbSQLQuery
	 * @return Integer
	 * @throws DatabaseException
	 */
	protected final Integer execute(DbSQLQuery sqlQuery) throws DatabaseException
	{
		Validate.notNull(sqlQuery, "sqlQuery cannot be null");

		Integer modRows = - 1;
		Connection conn = connectionPool.open();
		CallableStatement stmt = null;

		try {
			stmt = conn.prepareCall(sqlQuery.getQuery());
			prepareParams(stmt, sqlQuery.getParameters(), 1);
			modRows = stmt.executeUpdate();
		}
		catch (SQLException e) {
			connectionPool.rollback(conn);
			throw new DatabaseException(e);
		}
		finally {
			connectionPool.commit(conn);
			close(null, stmt);
			connectionPool.close(conn);
		}

		return modRows;
	}
	/**
	 * Create a DbSQLQuery with the sqlQuery extracted from the associated XML
	 * file. Throw DatabaseException if the file does not exists.
	 * 
	 * @param String xmlKeyName
	 * @return DbSQLQuery
	 * @throws DatabaseException
	 */
	protected final DbSQLQuery loadXMLQuery(String xmlKeyName) throws DatabaseException
	{
		Validate.notNull(xmlKeyName, "xmlKeyName cannot be null");

		if (sqlQueryMapping == null) {
			String m = "This DAO have not an XML file, expected in the same package with name '%s'";
			throw new DatabaseException(String.format(m, getClass().getSimpleName() + ".xml"));
		}

		String sql = sqlQueryMapping.getProperty(xmlKeyName);
		Validate.notBlank(sql, "SQL query cannot be blank.");

		return new DbSQLQuery(sql);
	}

	//
	// Internal
	//

	M queryForModel(ResultSet rs) throws Exception
	{
		M model = modelClass.newInstance();
		return queryForModel(rs, model);
	}

	M queryForModel(ResultSet rs, M model) throws Exception
	{
		ResultSetMetaData rsmd = rs.getMetaData();

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {

			Method setterMethod = columnSetterMapping.get(rsmd.getColumnName(i));
			if (setterMethod == null) {

				if (logger.isDebugEnabled())
					logger.debug("Model has not field for binding the column '" + rsmd.getColumnName(i) + "'");
				continue;
			}

			Class<?> setterClass = columnTypeMapping.get(rsmd.getColumnName(i));
			setterMethod.invoke(model, getObject(rs, i, setterClass));
		}

		model.aftermath();

		return model;
	}

	Object getObject(ResultSet rs, Integer index, Class<?> type)
		throws SQLException
	{
		if (rs.getObject(index) == null) {
			return null;
		}
		else if (type.equals(String.class)) {
			return rs.getString(index);
		}
		else if (type.equals(Integer.class)) {
			return rs.getInt(index);
		}
		else if (type.equals(Long.class)) {
			return rs.getLong(index);
		}
		else if (type.equals(Boolean.class)) {
			return rs.getBoolean(index);
		}
		else if (type.equals(Short.class)) {
			return rs.getShort(index);
		}
		else if (type.equals(Double.class)) {
			return rs.getDouble(index);
		}
		else if (type.equals(Float.class)) {
			return rs.getFloat(index);
		}
		else if (type.equals(Short.class)) {
			return rs.getShort(index);
		}
		else if (type.equals(Date.class)) {

			Timestamp ts = rs.getTimestamp(index);
			if (ts == null)
				return null;

			return new Date(ts.getTime());
		}
		else if (type.equals(Timestamp.class)) {
			return rs.getTimestamp(index);
		}
		else if (type.equals(byte[].class)) {
			return rs.getBytes(index);
		}
		else if (type.equals(BigDecimal.class)) {
			return rs.getBigDecimal(index);
		}
		else if (Object[].class.isAssignableFrom(type)) {
			return rs.getArray(index).getArray();
		}
		else if (List.class.isAssignableFrom(type)) {
			return Arrays.asList(rs.getArray(index).getArray());
		}
		else if (InputStream.class.isAssignableFrom(type)) {
			return rs.getCharacterStream(index);
		}
		else
			throw new DatabaseException(String.format(
				"Type '%s' not yet supported.", type.getName()));
	}

	/**
	 * @author Raph
	 */
	void prepareParams(CallableStatement stmt, Map<String, Object> params, Integer startIndex)
		throws SQLException
	{
		if (! params.isEmpty()) {

			Integer index = startIndex;
			for (Object value : params.values()) {

				if (value == null) {
					stmt.setNull(index, Types.NULL);
				}
				else if (value instanceof java.lang.Short) {
					stmt.setShort(index, (java.lang.Short) value);
				}
				else if (value instanceof java.lang.Integer) {
					stmt.setInt(index, (java.lang.Integer) value);
				}
				else if (value instanceof java.lang.Long) {
					stmt.setLong(index, (java.lang.Long) value);
				}
				else if (value instanceof java.lang.Double) {
					stmt.setDouble(index, (java.lang.Double) value);
				}
				else if (value instanceof java.lang.Boolean) {
					stmt.setBoolean(index, (java.lang.Boolean) value);
				}
				else if (value instanceof java.lang.String) {
					stmt.setString(index, (java.lang.String) value);
				}
				else if (value instanceof java.lang.Character) {
					stmt.setString(index, ((java.lang.Character) value).toString());
				}
				else if (value instanceof java.sql.Timestamp) {
					stmt.setTimestamp(index, (java.sql.Timestamp) value);
				}
				else if (value instanceof java.sql.Date) {
					stmt.setDate(index, (java.sql.Date) value);
				}
				else if (value instanceof java.util.Date) {
					stmt.setDate(index, new java.sql.Date(((java.util.Date) value).getTime()));
				}
				else if (value instanceof java.sql.Array) {
					stmt.setArray(index, ((java.sql.Array) value));
				}
				else if (value instanceof byte[]) {
					stmt.setBytes(index, ((byte[]) value));
				}
				else
					stmt.setObject(index, value);

				index++;
			}
		}
	}

	void close(ResultSet rs, Statement stmt)
	{
		try {
			if (rs != null)
				rs.close();
		}
		catch (SQLException e) {
			// quiet
		}

		try {
			if (stmt != null)
				stmt.close();
		}
		catch (SQLException e) {
			// quiet
		}
	}

	Map<Key, M> newMap(Number initialCapacity)
	{
		if (initialCapacity == null)
			initialCapacity = MAP_INIT_SIZE;

		return new LinkedHashMap<>(initialCapacity.intValue());
	}

	List<M> newList(Number initialCapacity)
	{
		if (initialCapacity != null)
			return new ArrayList<>(initialCapacity.intValue());
		else
			return new LinkedList<>();
	}

	class ResultSetIterator implements AutoCloseableIterator<M> {

		private Connection conn;
		private CallableStatement stmt;
		private ResultSet rs;
		private M record;

		public ResultSetIterator(Connection conn, CallableStatement stmt, ResultSet rs) {

			this.conn = conn;
			this.stmt = stmt;
			this.rs = rs;
		}

		@Override
		public boolean hasNext()
		{
			try {
				if (rs != null && rs.next()) {
					record = queryForModel(rs, modelClass.newInstance());
					return true;
				}
				else {
					close();
					record = null;
					return false;
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public M next()
		{
			return (M) record;
		}

		@Override
		public void close() throws Exception
		{
			DbDao.this.close(rs, stmt);
			connectionPool.close(conn);
		}

		@Override
		public void remove()
		{
			throw new IllegalStateException("remove is not allowed here!");
		}
	}
}
