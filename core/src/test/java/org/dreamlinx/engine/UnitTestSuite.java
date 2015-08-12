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

package org.dreamlinx.engine;

import org.apache.log4j.Logger;
import org.dreamlinx.engine.conf.ConfigurationValidatorTest;
import org.dreamlinx.engine.core.ChronoLogTest;
import org.dreamlinx.engine.core.LogTest;
import org.dreamlinx.engine.core.MemoryTest;
import org.dreamlinx.engine.core.ModuleTest;
import org.dreamlinx.engine.db.DbFunctionTest;
import org.dreamlinx.engine.fn.DateFnTest;
import org.dreamlinx.engine.fn.MathFnTest;
import org.dreamlinx.engine.fn.NetworkFnTest;
import org.dreamlinx.engine.fn.RandomFnTest;
import org.dreamlinx.engine.fn.SerialFnTest;
import org.dreamlinx.engine.fn.StringFnTest;
import org.dreamlinx.engine.fn.SysFnTest;
import org.dreamlinx.engine.model.KeyTest;
import org.dreamlinx.engine.model.ModelTest;
import org.dreamlinx.engine.sys.DaemonPoolTest;
import org.dreamlinx.engine.sys.OsTest;
import org.dreamlinx.engine.sys.data.BiMapTest;
import org.dreamlinx.engine.sys.data.HierarchyTreeTest;
import org.dreamlinx.engine.sys.data.MultiMapTest;
import org.dreamlinx.engine.sys.data.ScrollableListTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all unit tests. Must be the only way to launch tests.
 * The order of classes defined in the SuiteClass annotation is mandatory.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	// core
	LogTest.class, ChronoLogTest.class,
	// conf
	ConfigurationValidatorTest.class,
	// core
	MemoryTest.class, ModuleTest.class,
	// model
	KeyTest.class, ModelTest.class,
	// db
	DbFunctionTest.class,
	// fn
	DateFnTest.class, MathFnTest.class, SysFnTest.class,
	SerialFnTest.class, RandomFnTest.class, NetworkFnTest.class,
	StringFnTest.class,
	// sys
	DaemonPoolTest.class, OsTest.class,
	// data
	HierarchyTreeTest.class, MultiMapTest.class,
	BiMapTest.class, ScrollableListTest.class
})
public class UnitTestSuite {

	private static final Logger logger = Logger.getLogger(UnitTestSuite.class);

	@BeforeClass
	public static void before()
	{
		logger.debug("Starting test suite..");
	}

	@AfterClass
	public static void after() throws Exception
	{
		logger.debug("Test suite completed.");
	}
}
