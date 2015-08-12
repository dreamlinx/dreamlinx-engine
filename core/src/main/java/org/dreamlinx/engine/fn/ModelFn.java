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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.dreamlinx.engine.db.DbColumnName;
import org.dreamlinx.engine.db.DbColumnSkip;
import org.dreamlinx.engine.error.ModelException;
import org.dreamlinx.engine.model.Model;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public final class ModelFn {

	/**
	 * Validate the syntax of a Model, throwing errors as exection.
	 * 
	 * @param Model
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public static void validate(Model model) throws ModelException
	{
		Class<? extends Model> modelClass = model.getClass();
		while (modelClass != null) {

			// Check for DbColumnName annotations
			Boolean hasAnnotation = false;
			Boolean hasSkip = false;
			for (Field field : modelClass.getDeclaredFields()) {

				if (field.getName().equals("serialVersionUID"))
					continue;

				hasAnnotation = false;
				hasSkip = false;
				for (Annotation ann : field.getAnnotations()) {
					if (ann instanceof DbColumnName)
						hasAnnotation = true;
					else if (ann instanceof DbColumnSkip)
						hasSkip = true;
				}

				if (! hasAnnotation && ! hasSkip)
					throw new ModelException(modelClass, field);
			}

			// Recursion over hierarchy
			modelClass = (Class<? extends Model>) modelClass.getSuperclass();
		}
	}

	private ModelFn() {}
}
