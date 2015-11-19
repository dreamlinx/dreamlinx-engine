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

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Define parameters to connet to the database.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DbProperties implements Serializable {

	private static final long serialVersionUID = - 7262666330611546697L;

	private String sourceName;

	private String url;
	private String host;
	private Integer port;
	private String database;

	private String username;
	private String password;

	private Short poolSize = 10;
	private Integer fetchSize = 100;
	private Integer commitSize = 1000;

	private Integer notificationPort;

	public String getSourceName()
	{
		return sourceName;
	}

	public void setSourceName(String sourceName)
	{
		this.sourceName = sourceName;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public Integer getPort()
	{
		return port;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}

	public String getDatabase()
	{
		return database;
	}

	public void setDatabase(String database)
	{
		this.database = database;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Short getPoolSize()
	{
		return poolSize;
	}

	public void setPoolSize(Short poolSize)
	{
		this.poolSize = poolSize;
	}

	public Integer getFetchSize()
	{
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize)
	{
		this.fetchSize = fetchSize;
	}

	public Integer getCommitSize()
	{
		return commitSize;
	}

	public void setCommitSize(Integer commitSize)
	{
		this.commitSize = commitSize;
	}

	public Integer getNotificationPort()
	{
		return notificationPort;
	}

	public void setNotificationPort(Integer notificationPort)
	{
		this.notificationPort = notificationPort;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
