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

package org.dreamlinx.engine.error;

import java.lang.reflect.Field;

import org.dreamlinx.engine.model.Model;

/**
 * Exception for models errors.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ModelException extends RuntimeException {

	private static final long serialVersionUID = 6127548941320891094L;

	private static final String classMsg = "%s model has not the DbTableName annotation.";
	private static final String fieldMsg = "%s model has not the DbColumnName annotation for field '%s'.";

	public ModelException(Class<? extends Model> model, String message) {

		super(String.format("%s model " + message, model.getSimpleName()));
	}

	public ModelException(Class<? extends Model> model) {

		super(String.format(classMsg, model.getSimpleName()));
	}

	public ModelException(Class<? extends Model> model, Field field) {

		super(String.format(fieldMsg, model.getSimpleName(), field.getName()));
	}
}
