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

import org.dreamlinx.engine.UnitTestSupport;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class RandomFnTest extends UnitTestSupport {

	private static final Long SIZE = 12L;

	@Test
	public void nextUID()
	{
		Long uid = RandomFn.nextUid();
		Long aUid = RandomFn.nextUid();
		Long aaUid = RandomFn.nextUid();
		Long aaaUid = RandomFn.nextUid();
		Long aaaaUid = RandomFn.nextUid();
		Long aaaaaUid = RandomFn.nextUid();

		assertNotSame(uid, RandomFn.nextUid());
		assertNotSame(aUid, RandomFn.nextUid());
		assertNotSame(aaUid, RandomFn.nextUid());
		assertNotSame(aaaUid, RandomFn.nextUid());
		assertNotSame(aaaaUid, RandomFn.nextUid());
		assertNotSame(aaaaaUid, RandomFn.nextUid());
	}

	@Test
	public void nextBytes()
	{
		byte[] bytes = RandomFn.nextBytes(SIZE);
		byte[] aBytes = RandomFn.nextBytes(SIZE);
		byte[] aaBytes = RandomFn.nextBytes(SIZE);
		byte[] aaaBytes = RandomFn.nextBytes(SIZE);
		byte[] aaaaBytes = RandomFn.nextBytes(SIZE);
		byte[] aaaaaBytes = RandomFn.nextBytes(SIZE);

		assertNotSame(bytes, RandomFn.nextBytes(SIZE));
		assertNotSame(aBytes, RandomFn.nextBytes(SIZE));
		assertNotSame(aaBytes, RandomFn.nextBytes(SIZE));
		assertNotSame(aaaBytes, RandomFn.nextBytes(SIZE));
		assertNotSame(aaaaBytes, RandomFn.nextBytes(SIZE));
		assertNotSame(aaaaaBytes, RandomFn.nextBytes(SIZE));
	}

	@Test
	public void nextString()
	{
		String string = RandomFn.nextString(SIZE);
		String aString = RandomFn.nextString(SIZE);
		String aaString = RandomFn.nextString(SIZE);
		String aaaString = RandomFn.nextString(SIZE);
		String aaaaString = RandomFn.nextString(SIZE);
		String aaaaaString = RandomFn.nextString(SIZE);

		assertNotSame(string, RandomFn.nextString(SIZE));
		assertNotSame(aString, RandomFn.nextString(SIZE));
		assertNotSame(aaString, RandomFn.nextString(SIZE));
		assertNotSame(aaaString, RandomFn.nextString(SIZE));
		assertNotSame(aaaaString, RandomFn.nextString(SIZE));
		assertNotSame(aaaaaString, RandomFn.nextString(SIZE));
	}

	@Test
	public void nextAddress()
	{
		String address = RandomFn.nextAddress();
		String aAddress = RandomFn.nextAddress();
		String aaAddress = RandomFn.nextAddress();
		String aaaAddress = RandomFn.nextAddress();
		String aaaaAddress = RandomFn.nextAddress();
		String aaaaaAddress = RandomFn.nextAddress();

		assertTrue(address.split("\\.").length == 4);
		assertTrue(aAddress.split("\\.").length == 4);
		assertTrue(aaAddress.split("\\.").length == 4);
		assertTrue(aaaAddress.split("\\.").length == 4);
		assertTrue(aaaaAddress.split("\\.").length == 4);
		assertTrue(aaaaaAddress.split("\\.").length == 4);

		assertNotSame(address, RandomFn.nextAddress());
		assertNotSame(aAddress, RandomFn.nextAddress());
		assertNotSame(aaAddress, RandomFn.nextAddress());
		assertNotSame(aaaAddress, RandomFn.nextAddress());
		assertNotSame(aaaaAddress, RandomFn.nextAddress());
		assertNotSame(aaaaaAddress, RandomFn.nextAddress());
	}

	@Test
	public void nextPid()
	{
		Integer pid = RandomFn.nextPid();
		Integer aPid = RandomFn.nextPid();
		Integer aaPid = RandomFn.nextPid();
		Integer aaaPid = RandomFn.nextPid();
		Integer aaaaPid = RandomFn.nextPid();
		Integer aaaaaPid = RandomFn.nextPid();

		assertNotSame(pid, RandomFn.nextPid());
		assertNotSame(aPid, RandomFn.nextPid());
		assertNotSame(aaPid, RandomFn.nextPid());
		assertNotSame(aaaPid, RandomFn.nextPid());
		assertNotSame(aaaaPid, RandomFn.nextPid());
		assertNotSame(aaaaaPid, RandomFn.nextPid());
	}

	@Test
	public void nextPort()
	{
		Integer port = RandomFn.nextPort();
		Integer aPort = RandomFn.nextPort();
		Integer aaPort = RandomFn.nextPort();
		Integer aaaPort = RandomFn.nextPort();
		Integer aaaaPort = RandomFn.nextPort();
		Integer aaaaaPort = RandomFn.nextPort();

		assertNotSame(port, RandomFn.nextPort());
		assertNotSame(aPort, RandomFn.nextPort());
		assertNotSame(aaPort, RandomFn.nextPort());
		assertNotSame(aaaPort, RandomFn.nextPort());
		assertNotSame(aaaaPort, RandomFn.nextPort());
		assertNotSame(aaaaaPort, RandomFn.nextPort());
	}

	@Test
	public void nextPath()
	{
		String path = RandomFn.nextPath(5);
		String aPath = RandomFn.nextPath(5);
		String aaPath = RandomFn.nextPath(5);
		String aaaPath = RandomFn.nextPath(5);
		String aaaaPath = RandomFn.nextPath(5);
		String aaaaaPath = RandomFn.nextPath(5);

		assertNotSame(path, RandomFn.nextPath(5));
		assertNotSame(aPath, RandomFn.nextPath(5));
		assertNotSame(aaPath, RandomFn.nextPath(5));
		assertNotSame(aaaPath, RandomFn.nextPath(5));
		assertNotSame(aaaaPath, RandomFn.nextPath(5));
		assertNotSame(aaaaaPath, RandomFn.nextPath(5));
	}
}
