import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Subscriber {

    private static final Boolean NON_TRANSACTED = false;
    private static final String DESTINATION_NAME = "topic/simple";
    private static final String CONTROL_DESTINATION_NAME = "topic/control";
    private static final int MESSAGE_TIMEOUT_MILLISECONDS = 10000;

    public static void main(String args[]) {
        Connection connection = null;

        try {

            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(DESTINATION_NAME);
            Topic controlTopic = session.createTopic(CONTROL_DESTINATION_NAME);

            MessageProducer controlProducer = session.createProducer(controlTopic);

            // Setup main topic MessageListener
            String selector = "NUMBER=1";
            MessageConsumer consumer = session.createConsumer(topic, selector);
            consumer.setMessageListener(new JmsMessageListener(session, controlProducer));

            // Must have a separate Session or Connection for the synchronous MessageConsumer
            // per JMS spec you can not have synchronous and asynchronous message consumers
            // on same session
            Session controlSession = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer controlConsumer = controlSession.createConsumer(controlTopic);

            // Note: important to ensure that connection.start() if
            // MessageListeners have been registered
            connection.start();

            System.out.println("Start control message consumer");
            int i = 1;
            while (true) {
                Message message = controlConsumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + (i++) + ". message: " + text);

                        // Break from this loop when we receive a SHUTDOWN message
                        if (text.startsWith("SHUTDOWN")) {
                            break;
                        }
                    }
                }
            }

            // Cleanup
            controlConsumer.close();
            controlSession.close();
            consumer.close();
            controlProducer.close();
            session.close();
        } catch (Throwable t) {
            System.out.println(t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private static class JmsMessageListener implements MessageListener {

        private Session session;
        private MessageProducer producer;

        private int count = 0;
        private long start = System.currentTimeMillis();

        public JmsMessageListener(Session session, MessageProducer producer) {
            this.session = session;
            this.producer = producer;
        }



        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage) {
                    String text = ((TextMessage) message).getText();

                    if ("SHUTDOWN".equals(text)) {
                        System.out.println("Got the SHUTDOWN command -> exit");
                        producer.send(session.createTextMessage("SHUTDOWN is being performed"));
                    } else if ("REPORT".equals(text)) {
                        long time = System.currentTimeMillis() - start;
                        producer.send(session.createTextMessage("Received " + count + " in " + time + "ms"));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            System.out.println("Wait for the report message to be sent our was interrupted");
                        }
                        count = 0;
                    } else {
                        if (count == 0) {
                            start = System.currentTimeMillis();
                        }
                        count++;
                        System.out.println("Received " + count + " messages.");
                    }
                }
            } catch (JMSException e) {
                System.out.println("Got an JMS Exception handling message: " + message + " | " + e);
            }
        }
    }
}