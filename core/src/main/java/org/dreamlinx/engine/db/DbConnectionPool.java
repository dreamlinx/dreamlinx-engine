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

import org.dreamlinx.engine.error.DatabaseException;

/**
 * Define a database connection pool.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class DbConnectionPool {

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
	public abstract boolean commit(Connection connection) throws DatabaseException;

	/**
	 * Rollback over connection if it is opened. Return false in any other
	 * cases.
	 * 
	 * @param Connection connection
	 * @return Boolean
	 * @throws DatabaseException
	 */
	public abstract boolean rollback(Connection connection) throws DatabaseException;

	/**
	 * Alter the session with setting value in parameter.
	 * 
	 * @param String set
	 * @throws DatabaseException
	 */
	public abstract void set(String name, String value) throws DatabaseException;

	/**
	 * Close a connection if it is opened. Return false if it is already
	 * closed.
	 * 
	 * @param Connection connection
	 * @return Boolean
	 * @throws DatabaseException
	 */
	public abstract boolean close(Connection connection) throws DatabaseException;
}
