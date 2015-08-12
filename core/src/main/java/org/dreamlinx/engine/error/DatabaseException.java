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

import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Exception for database handling errors.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class DatabaseException extends SQLException {

	private static final long serialVersionUID = 360466702800041070L;

	public DatabaseException(String message) {

		super(message);
	}

	public DatabaseException(Throwable cause) {

		super(ExceptionUtils.getRootCauseMessage(cause), cause);
	}

	public DatabaseException(String message, Throwable cause) {

		super(message, cause);
	}

	public DatabaseException(SQLException e) {

		super(ExceptionUtils.getRootCauseMessage(e), e.getSQLState(), e.getErrorCode(), e);
	}
}
