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

package org.dreamlinx.engine.mq;

import org.dreamlinx.engine.model.Model;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MqMessage extends Model {

	private static final long serialVersionUID = 2977876249250985881L;

	private String message;
	private String endpoint;

	public MqMessage(String data) {

		this(data, null);
	}

	public MqMessage(String data, String to) {

		this.message = data;
		this.endpoint = to;
	}

	@Override
	protected final Object[] defineKey()
	{
		return new Object[] {
			message, endpoint
		};
	}

	public final String getMessage()
	{
		return message;
	}

	public final String getEndpoint()
	{
		return endpoint;
	}
}
