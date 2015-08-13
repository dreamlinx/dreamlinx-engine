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
import org.dreamlinx.engine.error.InitializationException;
import org.postgresql.ds.PGPoolingDataSource;

/**
 * Define database connections pool.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class PostgresDbConnectionPool extends DbConnectionPool {

	protected static final Logger logger = Log.getEngineLogger();

	private static PostgresDbConnectionPool instance;

	private PGPoolingDataSource connPool;

	public static PostgresDbConnectionPool get()
	{
		if (instance == null)
			throw new InitializationException(PostgresDbConnectionPool.class);

		return instance;
	}

	public static void init(DbProperties properties) throws DatabaseException
	{
		instance = new PostgresDbConnectionPool(properties);
		instance.init();
	}

	public PostgresDbConnectionPool(DbProperties properties) {

		super(properties);
	}

	@Override
	public void init() throws DatabaseException
	{
		connPool = new PGPoolingDataSource();

		connPool.setDataSourceName(properties.getSourceName());
		connPool.setServerName(properties.getHost());
		connPool.setPortNumber(properties.getPort());
		connPool.setDatabaseName(properties.getDatabase());
		connPool.setUser(properties.getUsername());
		connPool.setPassword(properties.getPassword());
		connPool.setMaxConnections(properties.getPoolSize());

		if (logger.isDebugEnabled())
			logger.debug(String.format("ConnectionPool '%s' connected to '%s:%d/%s'",
				connPool.getDataSourceName(), connPool.getServerName(),
				connPool.getPortNumber(), connPool.getDatabaseName()));
	}

	@Override
	public void shutdown() throws DatabaseException
	{
		connPool.close();
	}

	@Override
	public final Connection open() throws DatabaseException
	{
		if (connPool == null)
			throw new InitializationException(PostgresDbConnectionPool.class);

		try {
			Connection conn = (Connection) connPool.getConnection();
			conn.setAutoCommit(false);

			return conn;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public final boolean commit(Connection connection) throws DatabaseException
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

	@Override
	public final boolean rollback(Connection connection) throws DatabaseException
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

	@Override
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

	@Override
	public final boolean close(Connection connection) throws DatabaseException
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
