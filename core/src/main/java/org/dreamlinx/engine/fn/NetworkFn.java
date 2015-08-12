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

package org.dreamlinx.engine.fn;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class NetworkFn {

	private static final int TIMEOUT = 1000;

	public enum PortRange
	{
		ALL_PORTS(1, 65535),
		WELL_KNOWN_PORTS(1, 1023),
		REGISTERED_PORTS(1024, 49151),
		DYNAMIC_PORTS(49152, 65535);

		int from, to;

		PortRange(int from, int to) {

			this.from = from;
			this.to = to;
		}
	}

	public static String localAddress() throws Exception
	{
		String address = null;

		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {

			for (InterfaceAddress a : e.nextElement().getInterfaceAddresses()) {
				address = a.getAddress().getHostAddress();
				if (address.indexOf(":") == - 1 && ! address.equals("127.0.0.1"))
					return address;
			}
		}

		return address;
	}

	public static boolean ping(String address) throws Exception
	{
		return InetAddress.getByName(address).isReachable(TIMEOUT);
	}

	public static List<Integer> scan(String address) throws Exception
	{
		return scan(address, PortRange.ALL_PORTS);
	}

	public static List<Integer> scan(String address, PortRange range) throws Exception
	{
		return scan(address, range.from, range.to);
	}

	public static List<Integer> scan(String address, Integer from, Integer to) throws Exception
	{
		List<Integer> scan = new LinkedList<Integer>();

		for (int p = from; p <= to; p++)
			if (scan(address, p))
				scan.add(p);

		return scan;
	}

	public static boolean scan(String address, Integer port) throws Exception
	{
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkConnect(address, port);

		try (Socket socket = new Socket(address, port)) {
			if (socket.isConnected() && socket.isBound())
				return true;
		}
		catch (ConnectException e) {
			return false;
		}

		return false;
	}

	private NetworkFn() {}
}
