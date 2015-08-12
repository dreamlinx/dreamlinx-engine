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
public class InitializationException extends RuntimeException {

	private static final long serialVersionUID = - 5183716165398997937L;
	private static final String message = "%s is not initialized; call init method first.";

	public InitializationException(String message) {

		super(message);
	}

	public InitializationException(Throwable error) {

		super(error);
	}

	public InitializationException(String message, Throwable error) {

		super(message, error);
	}

	public InitializationException(Class<?> caller) {

		super(String.format(message, caller.getSimpleName()));
	}

	public InitializationException(Class<?> caller, Throwable cause) {

		super(String.format(message, caller.getSimpleName()), cause);
	}
}
