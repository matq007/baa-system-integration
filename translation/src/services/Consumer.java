package services;

import lib.Config;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class Consumer {

    public void run(String type) {

        try {

            String queue = "None";
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            if (type == "cpr-eu")
                queue = Config.destQueueCprEu;

            if (type == "eu-cpr")
                queue = Config.destQueueEuCpr;

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);
            MessageConsumer consumer = session.createConsumer(destination);

            Message message = consumer.receive();
            if (message instanceof TextMessage) {
                // STORE INTO SYSTEM - TRANSLATED DATA
                TextMessage text = (TextMessage) message;
                System.out.println("Message is: \n" + text.getText());
            }

            consumer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

}
