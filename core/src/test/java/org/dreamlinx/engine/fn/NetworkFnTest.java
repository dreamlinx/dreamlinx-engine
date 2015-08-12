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

import java.util.List;

import org.dreamlinx.engine.UnitTestSupport;
import org.dreamlinx.engine.fn.NetworkFn.PortRange;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class NetworkFnTest extends UnitTestSupport {

	static final String HOSTNAME = "127.0.0.1";

	@Test
	public void scan() throws Exception
	{
		List<Integer> scan = NetworkFn.scan(HOSTNAME, PortRange.WELL_KNOWN_PORTS);

		assertNotNull(scan);
		assertNotSame(0, scan.size());
	}

	@Test
	public void ping() throws Exception
	{
		assertTrue(NetworkFn.ping(HOSTNAME));
	}

	@Test
	public void localAddress() throws Exception
	{
		String address = NetworkFn.localAddress();
		String aAddress = NetworkFn.localAddress();

		assertNotNull(address);
		assertNotNull(aAddress);
		assertEquals(address, aAddress);
		assertTrue(address.split("\\.").length == 4);
	}
}
