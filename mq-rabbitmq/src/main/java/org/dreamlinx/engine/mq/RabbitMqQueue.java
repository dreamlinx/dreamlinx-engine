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

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class RabbitMqQueue extends MqQueue {

	private String queueName;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;

	public RabbitMqQueue(String bind, String queueName) {

		super(bind);
		this.queueName = queueName;
	}

	@Override
	public void init() throws Exception
	{
		factory = new ConnectionFactory();
		factory.setHost(getBind());
		connection = factory.newConnection();

		channel = connection.createChannel();
		channel.queueDeclare(queueName, false, false, false, null);
	}

	@Override
	public void send(MqMessage message) throws Exception
	{
		channel.basicPublish("", queueName, null, message.getData());
	}

	@Override
	public MqMessage receive() throws Exception
	{
		InternalConsumer consumer = new InternalConsumer(channel);
		channel.basicConsume(queueName, true, consumer);

		return consumer.getMessage();
	}

	@Override
	public void shutdown() throws Exception
	{
		channel.close();
		connection.close();
	}

	private class InternalConsumer extends DefaultConsumer {

		private MqMessage message;

		public InternalConsumer(Channel channel) {

			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
			AMQP.BasicProperties properties, byte[] body) throws IOException
		{
			message = new MqMessage(body);
		}

		public MqMessage getMessage()
		{
			return message;
		}
	}
}
