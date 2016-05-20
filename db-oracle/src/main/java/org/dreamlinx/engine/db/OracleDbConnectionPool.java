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

import java.lang.Thread.State;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.aq.AQNotificationRegistration;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.sql.StructDescriptor;
import oracle.ucp.UniversalConnectionPoolAdapter;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Log;
import org.dreamlinx.engine.error.DatabaseException;
import org.dreamlinx.engine.error.InitializationException;
import org.dreamlinx.engine.fn.MathFn;

/**
 * Define database connections pool.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class OracleDbConnectionPool extends DbConnectionPool {

	private static final Logger logger = Log.getEngineLogger();
	private static final String NTF_LISTENER_CLASS = "oracle.jdbc.driver.NTFListener";

	private UniversalConnectionPoolManager poolManager;
	private PoolDataSource connPool;

	private static final Map<Long, DatabaseChangeRegistration> notifRegs = new LinkedHashMap<>();
	private static final Map<String, AQNotificationRegistration> queueRegs = new LinkedHashMap<>();

	public OracleDbConnectionPool(DbProperties properties) {

		super(properties);
	}

	@Override
	public void init() throws DatabaseException
	{
		try {
			poolManager = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();

			connPool = PoolDataSourceFactory.getPoolDataSource();
			connPool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
			connPool.setDataSourceName(properties.getSourceName());
			connPool.setValidateConnectionOnBorrow(true);

			if (StringUtils.isNotBlank(properties.getUrl()))
				connPool.setURL("jdbc:oracle:thin:@" + properties.getUrl());
			else {
				connPool.setServerName(properties.getHost());
				connPool.setPortNumber(properties.getPort());
				connPool.setDatabaseName(properties.getDatabase());
			}

			connPool.setUser(properties.getUsername());
			connPool.setPassword(properties.getPassword());
			connPool.setInitialPoolSize(5);
			connPool.setMinPoolSize(5);
			connPool.setMaxPoolSize(properties.getPoolSize());

			poolManager.createConnectionPool((UniversalConnectionPoolAdapter) connPool);
			poolManager.startConnectionPool(properties.getSourceName());

			if (logger.isDebugEnabled())
				logger.debug(String.format("ConnectionPool '%s' connected to '%s:%d/%s'",
					connPool.getDataSourceName(), connPool.getServerName(),
					connPool.getPortNumber(), connPool.getDatabaseName()));
		}
		catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void shutdown() throws DatabaseException
	{
		if (logger.isDebugEnabled())
			logger.debug("Shutting down Connection pool...");

		try {
			unregisterAll();
			poolManager.purgeConnectionPool(properties.getSourceName());
		}
		catch (UniversalConnectionPoolException e) {
			// Suppressed
		}

		logger.info("Connection pool is terminated.");
	}

	@Override
	public final OracleConnection open() throws DatabaseException
	{
		if (connPool == null)
			throw new InitializationException(OracleDbConnectionPool.class);

		try {
			OracleConnection conn = (OracleConnection) connPool.getConnection();
			conn.setAutoCommit(false);

			if (MathFn.notZero(properties.getFetchSize()))
				conn.setDefaultRowPrefetch(properties.getFetchSize());

			return conn;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	//
	// Additional
	//

	/**
	 * Register a notification listener for database change notification.
	 * It throws the UnsupportedOperationException when the notification
	 * feature is not supported by database.
	 * 
	 * @param OracleDbNotification
	 * @throws DatabaseException
	 * @throws UnsupportedOperationException
	 */
	public void register(OracleDbNotification notif) throws DatabaseException
	{
		Validate.notNull(notif, "notification cannot be null");

		String notifName = notif.getClass().getSimpleName();
		if (logger.isDebugEnabled())
			logger.debug("Registering '" + notifName + "'...");

		try (OracleConnection conn = (OracleConnection) open()) {

			Properties opt = notif.getOptions();
			DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(opt);
			dcr.addListener(notif);

			try {
				OracleStatement stmt = (OracleStatement) conn.createStatement();
				stmt.setDatabaseChangeRegistration(dcr);
				stmt.execute(notif.getQuery());

				conn.commit();
				stmt.close();
			}
			catch (Exception e) {
				conn.unregisterDatabaseChangeNotification(dcr);
				throw e;
			}

			notifRegs.put(dcr.getRegId(), dcr);
			notif.setRegId(dcr.getRegId());

			if (logger.isInfoEnabled())
				logger.info(String.format("The notification '%s' with REGID #%d is now in listening at port '%s'.",
					notifName, notif.getRegId(), opt.get(OracleConnection.NTF_LOCAL_TCP_PORT)));
		}
		catch (ClassCastException e) {
			throw new UnsupportedOperationException("Notification not supported by database.");
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Register a notification listener for advanced queue.
	 * 
	 * @param OracleDbQueue
	 * @throws DatabaseException
	 */
	public void register(OracleDbQueue queue) throws DatabaseException
	{
		Validate.notNull(queue, "queue cannot be null");

		try (OracleConnection conn = open()) {

			Properties globalOptions = new Properties();
			String[] queueNameArr = new String[1];
			queueNameArr[0] = queue.getQueueName();

			StructDescriptor.createDescriptor(queue.getTypeName(), conn);

			AQNotificationRegistration[] regArr = conn
				.registerAQNotification(queueNameArr, queue.getQueueOpt(), globalOptions);

			AQNotificationRegistration reg = regArr[0];
			reg.addListener(queue);

			queueRegs.put(reg.getQueueName(), reg);

			if (logger.isInfoEnabled())
				logger.info(String.format("The notification '%s' on queue is now in listening.",
					queue.getQueueName()));
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Unregister a notification listener of database change notification.
	 * Return true if it will be successful - false otherwise.
	 * 
	 * @param OracleDbNotification
	 * @return boolean
	 * @throws DatabaseException
	 */
	public boolean unregister(OracleDbNotification notification) throws DatabaseException
	{
		Validate.notNull(notification, "notification cannot be null");

		if (notification.getRegId() == null) {
			logger.warn("Notification is not been registered yet!");
			return false;
		}

		try (OracleConnection conn = (OracleConnection) open()) {

			DatabaseChangeRegistration reg = notifRegs.get(notification.getRegId());
			if (reg != null) {

				conn.unregisterDatabaseChangeNotification(reg);

				if (logger.isDebugEnabled())
					logger.debug(String.format(
						"Notifications with regId '%d' is unregistered now.",
						notification.getRegId()));

				notifRegs.remove(notification.getRegId());
				notification.setRegId(null);

				return true;
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}

		return false;
	}

	/**
	 * Unregister a notification listener of advanced queue.
	 * Return true if it will be successful - false otherwise.
	 * 
	 * @param OracleDbQueue
	 * @return boolean
	 * @throws DatabaseException
	 */
	public boolean unregister(OracleDbQueue queue) throws DatabaseException
	{
		Validate.notNull(queue, "queue cannot be null");

		try (OracleConnection conn = (OracleConnection) open()) {

			AQNotificationRegistration reg = queueRegs.get(queue.getQueueName());
			if (reg != null) {
				conn.unregisterAQNotification(reg);

				if (logger.isDebugEnabled())
					logger.debug(String.format(
						"Notifications on queue '%d' is unregistered now.",
						queue.getQueueName()));

				queueRegs.remove(queue.getQueueName());
				return true;
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}

		return false;
	}

	/**
	 * Unregister all notification listeners.
	 */
	public boolean unregisterAll()
	{
		try (OracleConnection conn = (OracleConnection) open()) {

			for (Iterator<DatabaseChangeRegistration> it = notifRegs.values().iterator(); it.hasNext();) {
				conn.unregisterDatabaseChangeNotification(it.next());
				it.remove();
			}

			for (Iterator<AQNotificationRegistration> it = queueRegs.values().iterator(); it.hasNext();) {
				conn.unregisterAQNotification(it.next());
				it.remove();
			}

			if (logger.isInfoEnabled())
				logger.info("All notifications are unregistered now.");
		}
		catch (SQLException e) {
			// Suppressed
		}

		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		for (Thread th : threads.keySet()) {

			StackTraceElement[] stacks = threads.get(th);
			if (stacks != null && stacks.length > 0) {
				for (StackTraceElement stack : stacks)

					if (stack.getClassName().equals(NTF_LISTENER_CLASS)
						&& th.getState().equals(State.RUNNABLE))
						return false;
			}
		}

		return true;
	}
}
