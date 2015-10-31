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

import org.zeromq.ZMQ;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class ZeroMqReceiver extends MqReceiver {

	private ZMQ.Context context;
	private ZMQ.Socket responder;
	private int typeFlag;

	public ZeroMqReceiver(String bind, int typeFlag) {

		super(bind);
		this.typeFlag = typeFlag;
	}

	@Override
	public void init() throws Exception
	{
		context = ZMQ.context(1);
		responder = context.socket(typeFlag);
		responder.connect(getBind());
	}

	@Override
	public MqMessage receive(MqMessage reply) throws Exception
	{
		while (! Thread.currentThread().isInterrupted()) {

			Charset charset = (reply != null
				? reply.getCharset() : Charset.defaultCharset());

			MqMessage resp = new MqMessage(responder.recv(0), charset);

			if (reply != null)
				responder.send(reply.getMessage(), 0);

			return resp;
		}

		return null;
	}

	@Override
	public void shutdown() throws Exception
	{
		responder.close();
		context.term();
	}
}
