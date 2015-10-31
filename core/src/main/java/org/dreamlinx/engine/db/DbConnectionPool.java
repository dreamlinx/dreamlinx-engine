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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Log;
import org.dreamlinx.engine.error.DatabaseException;

/**
 * Define a database connection pool.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class DbConnectionPool {

	private static final Logger logger = Log.getEngineLogger();
	protected DbProperties properties;

	protected DbConnectionPool(DbProperties properties) {

		this.properties = properties;
	}

	/**
	 * Returns the database properties in use.
	 * 
	 * @return DbProperties
	 */
	public DbProperties getProperties()
	{
		return properties;
	}

	/**
	 * Setup the connection pool and the notification handler with XML
	 * configuration values.
	 * 
	 * @throws DatabaseException
	 */
	public abstract void init() throws DatabaseException;

	/**
	 * Shutdown the connection pool and unregister eventual
	 * notification listeners.
	 * 
	 * @throws DatabaseException
	 */
	public abstract void shutdown() throws DatabaseException;

	/**
	 * Open a connection based by xml configurations.
	 * 
	 * @return OracleConnection
	 * @throws DatabaseException
	 */
	public abstract Connection open() throws DatabaseException;

	/**
	 * Commit over connection if it is opened. Return false in any other
	 * cases.
	 * 
	 * @param Connection connection
	 * @return Boolean
	 * @throws DatabaseException
	 */
	public boolean commit(Connection connection) throws DatabaseException
	{
		Validate.notNull(connection, "connection cannot be null.");
		try {
			if (! (connection.isClosed() || connection.isReadOnly())) {
				connection.commit();
				return true;
			}

			return false;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Rollback over connection if it is opened. Return false in any other
	 * cases.
	 * 
	 * @param Connection connection
	 * @return Boolean
	 * @throws DatabaseException
	 */
	public boolean rollback(Connection connection) throws DatabaseException
	{
		Validate.notNull(connection, "connection cannot be null.");
		try {
			if (! (connection.isClosed() || connection.isReadOnly())) {
				connection.rollback();
				return true;
			}

			return false;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Alter the session with setting value in parameter.
	 * 
	 * @param String set
	 * @throws DatabaseException
	 */
	public void set(String name, String value) throws DatabaseException
	{
		Validate.notBlank(name, " name cannot be null");
		Validate.notBlank(value, "value cannot be null");

		try (Connection conn = open();
			Statement stmt = conn.createStatement()) {

			stmt.execute(String.format("SET SESSION %s TO %s", name, value));
			if (logger.isDebugEnabled())
				logger.debug(String.format("Set parameter '%s' with value '%s'.", name, value));
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Close a connection if it is opened. Return false if it is already
	 * closed.
	 * 
	 * @param Connection connection
	 * @return Boolean
	 * @throws DatabaseException
	 */
	public boolean close(Connection connection) throws DatabaseException
	{
		Validate.notNull(connection, "connection cannot be null.");
		try {
			if (connection.isClosed())
				return false;

			connection.close();
			connection = null; // From documentation

			return true;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
}
