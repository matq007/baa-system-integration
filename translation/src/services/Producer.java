package services;

import lib.Config;
import lib.Cpr;
import lib.EuCCID;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Producer {

    public void run(String type, Object person) {

        try {

            String queue = "None";
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            if (type == "cpr-eu")
                queue = Config.sourceQueueCprEu;

            if (type == "eu-cpr")
                queue = Config.sourceQueueEuCpr;

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage();

            if (type == "cpr-eu")
                message.setText(((Cpr) person).exportXML());

            if (type == "eu-cpr")
                message.setText(((EuCCID) person).exportXML());

            producer.send(message);
            System.out.println("Sent: " + message.getText());

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}