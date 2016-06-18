/**
 * Created by mproksik on 09/02/16.
 */
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Producer {

    public void run() {

        try {

            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("AIRPORT/FLIGHT");
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage();
            message.setStringProperty("TYPE", "SAS");
            message.setText("<?xml version=\"1.0\"?>\n" +
                            "<flight num=\"KL1108\">\n" +
                            "  <to>Amsterdam Schipol (AMS)</to>\n" +
                            "  <time>11:25</time>\n" +
                            "  <expected>11:30</expected>\n" +
                            "  <gate num=\"17\">\n" +
                            "    <checkin>10:25</checkin>\n" +
                            "    <status>Open</status>\n" +
                            "  </gate>\n" +
                            "</flight>"
                            );
            //producer.send(message, DeliveryMode.NON_PERSISTENT, 2, 10);
            producer.send(message, 1, 1, 20);
            //producer.send(message);
            System.out.println("Sent: " + message.getText());

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}