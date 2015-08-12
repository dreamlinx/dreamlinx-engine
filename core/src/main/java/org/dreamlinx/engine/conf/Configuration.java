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

package org.dreamlinx.engine.conf;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.rolling.RollingPolicy;

/**
 * Define the configuration of the engine.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Configuration implements Serializable {

	private static final long serialVersionUID = - 8820680519661092610L;

	@ConfNotBlank
	private String nodeName;
	@ConfNotBlank
	private String pidFile;
	@ConfNotNull
	private Level logLevel;

	private Boolean traceMode = false;
	private String logPatternLayout;
	private Boolean logConsole = true;
	private String logFile;
	private RollingPolicy logRollPolicy;

	public String getNodeName()
	{
		return nodeName;
	}

	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}

	public Level getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(Level logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getLogFile()
	{
		return logFile;
	}

	public void setLogFile(String logFile)
	{
		this.logFile = logFile;
	}

	public Boolean getLogConsole()
	{
		return logConsole;
	}

	public void setLogConsole(Boolean logConsole)
	{
		this.logConsole = logConsole;
	}

	public String getLogPatternLayout()
	{
		return logPatternLayout;
	}

	public void setLogPatternLayout(String logPatternLayout)
	{
		this.logPatternLayout = logPatternLayout;
	}

	public RollingPolicy getLogRollPolicy()
	{
		return logRollPolicy;
	}

	public void setLogRollPolicy(RollingPolicy logRollPattern)
	{
		this.logRollPolicy = logRollPattern;
	}

	public String getPidFile()
	{
		return pidFile;
	}

	public void setPidFile(String pidFile)
	{
		this.pidFile = pidFile;
	}

	public Boolean getTraceMode()
	{
		return traceMode;
	}

	public void setTraceMode(Boolean traceMode)
	{
		this.traceMode = traceMode;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
