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

package org.dreamlinx.engine.model;

import org.dreamlinx.engine.UnitTestSupport;
import org.dreamlinx.engine.db.DbColumnName;
import org.dreamlinx.engine.db.DbColumnSkip;
import org.junit.Test;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ModelTest extends UnitTestSupport {

	@Test
	public void key() throws Exception
	{
		DummyA aM = new DummyA();
		aM.id = 12L;
		aM.type = "A";
		aM.flag = true;

		DummyA bM = new DummyA();
		bM.id = 13L;
		bM.type = "A";
		bM.flag = true;

		assertNotNull(aM.getKey());
		assertNotNull(bM.getKey());

		assertStringNotBlank(aM.getKey().stringKey);
		assertNumberNotZero(aM.getKey().hashKey);

		assertSame(0, new DummyA().getKey().hashKey);
		assertSame(0, new DummyB().getKey().hashKey);

		assertEquals(aM.getKey(), aM.getKey());
		assertNotEquals(aM.getKey(), bM.getKey());

		assertEquals(aM, aM);
		assertNotEquals(aM, bM);
		assertNotEquals(aM, new DummyB());
	}
}

@SuppressWarnings("serial")
class DummyA extends Model {

	@DbColumnName("ID")
	Long id;

	@DbColumnName("TYPE")
	String type;

	@DbColumnName("FLAG")
	Boolean flag;

	@Override
	protected Object[] defineKey()
	{
		return new Object[] {
			id, type, flag
		};
	}
}

@SuppressWarnings("serial")
class DummyB extends Model {

	@DbColumnName("SERIAL_ID")
	private Long id;

	@DbColumnSkip
	private String unused;

	@Override
	protected Object[] defineKey()
	{
		return new Object[] {
			id
		};
	}
}
