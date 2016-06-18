/**
 * Created by mproksik on 09/02/16.
 */
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class Consumer {

    public void run() {

        try {

            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("AIRPORT/FLIGHT");
            MessageConsumer consumer = session.createConsumer(destination);

            Message message = consumer.receive(20);
            if (message instanceof TextMessage) {
                TextMessage text = (TextMessage) message;
                System.out.println("Correlation ID: " + message.getJMSCorrelationID() + "\n" +
                        "Message ID: " + message.getJMSMessageID() + "\n" +
                        "Destination: " + message.getJMSDestination() + "\n" +
                        "Delivery: " + message.getJMSDeliveryMode() + "\n"
                );
                System.out.println("Message is: \n" + text.getText());
                Document xml = parseXML(text.getText());
                System.out.println(xml.getElementsByTagName("flight").item(0).getAttributes().item(0));
            }

            consumer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public static Document parseXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

}
