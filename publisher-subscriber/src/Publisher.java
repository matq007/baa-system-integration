import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {

    private static final Boolean NON_TRANSACTED = false;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 10;
    private static final String DESTINATION_NAME = "topic/simple";

    public static void main(String args[]) {
        Connection connection = null;

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(DESTINATION_NAME);
            MessageProducer producer = session.createProducer(topic);

            for (int i = 1; i <= (NUM_MESSAGES_TO_BE_SENT / 10); i++) {
                for (int j = 1; j <= 10; j++) {
                    TextMessage message = session.createTextMessage(j + ". message sent");
                    message.setObjectProperty("NUMBER", j);
                    System.out.println("Sending to destination: " + topic.toString() + " this text: '" + message.getText());
                    producer.send(message);
                    Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
                }
                System.out.println("Send the Report Message");
                producer.send(session.createTextMessage("REPORT"));
            }
            System.out.println("Send the Shutdown Message");
            producer.send(session.createTextMessage("SHUTDOWN"));

            // Cleanup
            producer.close();
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
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}