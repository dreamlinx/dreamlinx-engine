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

import java.nio.charset.Charset;

import org.apache.commons.lang3.Validate;
import org.dreamlinx.engine.model.Model;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class MqMessage extends Model {

	private static final long serialVersionUID = 2977876249250985881L;
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

	private byte[] data;
	private Charset charset;
	private String message;

	public MqMessage(String message) {

		Validate.notBlank(message, "message can not be blank.");

		this.message = message;
		data = message.getBytes();
		charset = DEFAULT_CHARSET;
	}

	public MqMessage(byte[] data) {

		this(data, null);
	}

	public MqMessage(byte[] data, Charset charset) {

		Validate.notNull(data, "data can not be null.");
		Validate.isTrue(data.length > 0, "data can not be empty.");

		this.data = data;

		if (charset != null) {
			message = new String(data, charset);
			this.charset = charset;
		}
		else {
			message = new String(data);
			this.charset = DEFAULT_CHARSET;
		}
	}

	@Override
	protected final Object[] defineKey()
	{
		return new Object[] {
			message
		};
	}

	public final String getMessage()
	{
		return message;
	}

	public final byte[] getData()
	{
		return data;
	}

	public final Charset getCharset()
	{
		return charset;
	}
}
