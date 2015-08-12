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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.RowChangeDescription;
import oracle.jdbc.dcn.TableChangeDescription;
import oracle.sql.ROWID;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Log;
import org.dreamlinx.engine.error.DatabaseException;

/**
 * For implementation of a notification listener.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class OracleDbNotification implements DatabaseChangeListener {

	protected static final Logger logger = Log.getLogger();

	private Long regId;
	private String query;
	private Properties options;

	/**
	 * The SQL query used to bind the notification on the database object.
	 * 
	 * @param String query
	 */
	protected OracleDbNotification(String query, String listenPort) {

		this.query = query;

		options = new Properties();
		options.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
		options.setProperty(OracleConnection.NTF_LOCAL_TCP_PORT, listenPort);
	}

	/**
	 * Callback executed when the notification event will rise.
	 * 
	 * @param DatabaseChangeEvent
	 * @throws Exception
	 */
	public abstract void callback(DatabaseChangeEvent event) throws Exception;

	@Override
	public final void onDatabaseChangeNotification(DatabaseChangeEvent event)
	{
		try {
			callback(event);
		}
		catch (Exception e) {
			String msg = ExceptionUtils.getRootCauseMessage(e);
			logger.error(msg, e);
		}
	}

	public Long getRegId()
	{
		return regId;
	}

	/**
	 * Extract all rowIds from the event. Return always null if the property
	 * OracleConnection.DCN_NOTIFY_ROWIDS is not setted.
	 * 
	 * @param DatabaseChangeEvent
	 * @return List<String> rowIds
	 */
	protected List<String> getRowId(DatabaseChangeEvent event) throws DatabaseException
	{
		if (event.getTableChangeDescription().length > 1)
			throw new DatabaseException("Notification over multiple table not allowed.");

		TableChangeDescription table = event.getTableChangeDescription()[0];
		Validate.notNull(table, "TableChangeDescription cannot be null");

		RowChangeDescription[] rows = table.getRowChangeDescription();
		Validate.notNull(rows, "RowChangeDescription cannot be null");

		List<String> rowIds = null;
		if (rows.length > 0) {

			rowIds = new ArrayList<>(rows.length);
			for (RowChangeDescription row : rows) {

				ROWID r = row.getRowid();
				Validate.notNull(r, "ROWID cannot be null");
				rowIds.add(r.stringValue());
			}
		}

		return rowIds;
	}

	protected String getQuery()
	{
		return query;
	}

	protected void setRegId(Long regId)
	{
		this.regId = regId;
	}

	protected Properties getOptions()
	{
		return options;
	}
}
