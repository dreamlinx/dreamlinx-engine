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

/**
 * Exception for initialization errors.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class CollisionException extends RuntimeException {

	private static final long serialVersionUID = 5301494408766374972L;

	private static final String msg = "Key %s%s has collided%s";
	private static final String subMsg = " for the object %s\n";

	public CollisionException(Number key) {

		this(key, null, null);
	}

	public CollisionException(Number key, Object newObj, Object existObj) {

		super(String.format(msg, key,
			(newObj != null ? String.format(subMsg, newObj) : ""),
			(existObj != null ? String.format(subMsg, existObj) : "")));
	}
}
