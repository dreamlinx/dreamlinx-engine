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

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class MqQueue {

	private String bind;

	protected MqQueue(String bind) {

		this.bind = bind;
	}

	public abstract void init() throws Exception;

	public abstract void shutdown() throws Exception;

	public abstract void send(MqMessage message) throws Exception;

	public abstract MqMessage receive() throws Exception;

	public final String getBind()
	{
		return bind;
	}
}
