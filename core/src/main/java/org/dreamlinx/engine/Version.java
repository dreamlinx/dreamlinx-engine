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

import java.io.InputStream;
import java.util.Properties;

/**
 * Containing version informations.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class Version {

	private static final String POM_PROPS = "/META-INF/maven/org.dreamlinx.engine/core/pom.properties";
	private static String version;

	/**
	 * Returns the version in the form of "<major>.<minor>",
	 * as it is defined in the pom.xml of this module.
	 * 
	 * @return String
	 * @throws Exception
	 */
	public static String get() throws Exception
	{
		if (version == null) {

			try (InputStream in = Version.class.getResourceAsStream(POM_PROPS)) {
				if (in != null) {

					Properties props = new Properties();
					props.load(in);

					version = props.getProperty("version");
				}
			}
			catch (Exception e) {
				version = "?.?";
			}
		}

		return version;
	}

	private Version() {}
}
