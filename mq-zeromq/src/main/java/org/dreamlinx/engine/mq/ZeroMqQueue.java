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

import org.zeromq.ZMQ;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ZeroMqQueue extends MqQueue {

	private ZMQ.Context context;
	private ZMQ.Socket socket;
	private int typeFlag;

	public ZeroMqQueue(String bind, int typeFlag) {

		super(bind);
		this.typeFlag = typeFlag;
	}

	@Override
	public void init() throws Exception
	{
		context = ZMQ.context(1);
		socket = context.socket(typeFlag);
		socket.connect(getBind());
	}

	@Override
	public void send(MqMessage message) throws Exception
	{
		socket.send(message.getMessage(), 0);
	}

	@Override
	public MqMessage receive() throws Exception
	{
		while (! Thread.currentThread().isInterrupted()) {

			byte[] data = socket.recv(0);

			if (data != null)
				return new MqMessage(data);
		}

		return null;
	}

	@Override
	public void shutdown() throws Exception
	{
		socket.close();
		context.term();
	}
}
