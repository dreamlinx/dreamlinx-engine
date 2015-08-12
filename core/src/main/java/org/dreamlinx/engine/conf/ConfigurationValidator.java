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

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.error.ConfigurationException;
import org.dreamlinx.engine.fn.MathFn;

/**
 * Validate the configuration.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ConfigurationValidator {

	public static void validate(Configuration configuration)
		throws ConfigurationException
	{
		Validate.notNull(configuration, "configuration can not be null.");

		Class<?> currClass = configuration.getClass();
		while (currClass != null) {

			for (Field fld : currClass.getDeclaredFields()) {
				fld.setAccessible(true);

				if (fld.getAnnotation(ConfNotNull.class) != null) {
					try {
						if (fld.get(configuration) == null)
							throw new ConfigurationException(fld.getName(), "not null");
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						throw new ConfigurationException(e);
					}
				}

				if (fld.getAnnotation(ConfNotBlank.class) != null) {
					try {
						Object value;
						if ((value = fld.get(configuration)) == null
							|| ! (value instanceof String)
							|| StringUtils.isBlank((String) value))
							throw new ConfigurationException(fld.getName(), "not a blank String");
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						throw new ConfigurationException(e);
					}
				}

				if (fld.getAnnotation(ConfNotZero.class) != null) {
					try {
						Object value;
						if ((value = fld.get(configuration)) == null
							|| ! (value instanceof Number)
							|| ! MathFn.notZero((Number) value)) {
							throw new ConfigurationException(fld.getName(), "not a zero Number");
						}
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						throw new ConfigurationException(e);
					}
				}

				fld.setAccessible(false);
			}

			currClass = currClass.getSuperclass();
		}
	}

	private ConfigurationValidator() {}
}
