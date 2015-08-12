package org.dreamlinx.engine.db;

import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.aq.AQDequeueOptions;
import oracle.jdbc.aq.AQDequeueOptions.DequeueMode;
import oracle.jdbc.aq.AQDequeueOptions.NavigationOption;
import oracle.jdbc.aq.AQDequeueOptions.VisibilityOption;
import oracle.jdbc.aq.AQMessage;
import oracle.jdbc.aq.AQNotificationEvent;
import oracle.jdbc.aq.AQNotificationListener;
import oracle.sql.STRUCT;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.dreamlinx.engine.core.Log;

/**
 * For implement a queue notification listener.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public abstract class OracleDbQueue implements AQNotificationListener {

	protected static final Logger logger = Log.getLogger();

	private String queueName;
	private String typeName;
	private Properties[] queueOpt;
	private AQDequeueOptions dequeueOpt;

	public OracleDbQueue(String queueName, String typeName) throws Exception {

		Validate.notNull(queueName, "queueName cannot be null.");
		Validate.notNull(typeName, "typeName cannot be null.");

		this.queueName = queueName.toUpperCase();
		this.typeName = typeName.toUpperCase();

		queueOpt = new Properties[1];
		queueOpt[0] = new Properties();
		queueOpt[0].setProperty(OracleConnection.NTF_AQ_PAYLOAD, "true");

		dequeueOpt = new AQDequeueOptions();
		dequeueOpt.setRetrieveMessageId(false);
		dequeueOpt.setDequeueMode(DequeueMode.REMOVE);
		dequeueOpt.setNavigation(NavigationOption.NEXT_MESSAGE);
		dequeueOpt.setVisibility(VisibilityOption.ON_COMMIT);
	}

	public abstract void callback(AQNotificationEvent event) throws Exception;

	@Override
	public final void onAQNotification(AQNotificationEvent event)
	{
		try {
			callback(event);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected Object[] getValues(AQNotificationEvent event) throws Exception
	{
		try (OracleConnection conn = OracleDbConnectionPool.get().open()) {

			dequeueOpt.setConsumerName(event.getConsumerName());

			AQMessage m = conn.dequeue(queueName, dequeueOpt, typeName);
			Validate.notNull(m, "Event does not return an AQMessage.");

			STRUCT struct = m.getSTRUCTPayload();
			Validate.notNull(struct, "Event does not return a STRUCT.");

			return struct.getAttributes();
		}
	}

	protected String getQueueName()
	{
		return queueName;
	}

	protected String getTypeName()
	{
		return typeName;
	}

	protected Properties[] getQueueOpt()
	{
		return queueOpt;
	}

	protected AQDequeueOptions getDequeueOpt()
	{
		return dequeueOpt;
	}
}
