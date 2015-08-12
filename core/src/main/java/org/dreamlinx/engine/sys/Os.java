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

package org.dreamlinx.engine.sys;

import java.io.Serializable;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Os implements Serializable {

	private static final long serialVersionUID = 1341285323438707450L;

	public enum OsType {

		LINUX("Linux"),
		FREE_BSD("BSD"),
		MAC_OS("Mac"),
		SOLARIS("Solaris"),
		OS_2("OS/2"),
		UNIX("Unix"),
		AIX("AIX"),
		IRIX("Irix"),
		HP_UX("HP"),
		NETWARE("Netware"),
		WINDOWS("Windows"),
		OTHER("-");

		String key;

		OsType(String key) {

			this.key = key;
		}
	}

	private static Os instance;

	private String arch;
	private String name;
	private String version;
	private OsType type;

	protected Os(OsType type, String arch, String name, String version) {

		this.type = type;
		this.arch = arch;
		this.name = name;
		this.version = version;
	}

	public static Os instance()
	{
		if (instance == null)
			instance = discover();

		return instance;
	}

	protected static Os discover()
	{
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		OsType type = OsType.OTHER;

		for (OsType osType : OsType.values())
			if (name.contains(osType.key))
				type = osType;

		return new Os(type, arch, name, version);
	}

	public Boolean is64Bit()
	{
		return arch.contains("64");
	}

	public Boolean isUnixLike()
	{
		if (type.equals(OsType.LINUX) || type.equals(OsType.FREE_BSD)
			|| type.equals(OsType.MAC_OS) || type.equals(OsType.SOLARIS)
			|| type.equals(OsType.UNIX) || type.equals(OsType.AIX)
			|| type.equals(OsType.IRIX) || type.equals(OsType.HP_UX))
			return true;

		return false;
	}

	public String arch()
	{
		return arch;
	}

	public String name()
	{
		return name;
	}

	public OsType type()
	{
		return type;
	}

	public String version()
	{
		return version;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s %s", name, version, arch);
	}
}
