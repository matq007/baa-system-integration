import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

public class Client implements MessageListener {

    private static final String clientQueueName = "client.messages";
    private MessageProducer producer;
    private Connection connection;
    private Session session;

    public Client() {

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination adminQueue = session.createQueue(clientQueueName);

            //Setup a message producer to send message to the queue the server is consuming from
            this.producer = session.createProducer(adminQueue);
            this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }

    public void send() {

        try {
            //Create a temporary queue that this client will listen for responses on then create a consumer
            //that consumes message from this temporary queue...for a real application a client should reuse
            //the same temp queue for each message to the server...one temp queue per client
            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);

            //This class will handle the messages to the temp queue as well
            responseConsumer.setMessageListener(this);

            //Now create the actual message you want to send
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setStringProperty("COMMAND", "getEuCCID");
            txtMessage.setStringProperty("NAME", "Martin");
            txtMessage.setStringProperty("SURNAME", "Travolta");
            txtMessage.setStringProperty("GENDER", "male");
            txtMessage.setText("");

            //Set the reply to field to the temp queue you created above, this is the queue the server
            //will respond to
            txtMessage.setJMSReplyTo(tempDest);

            //Set a correlation ID so when you get a response you know which sent message the response is for
            //If there is never more than one outstanding message to the server then the
            //same correlation ID can be used for all the messages...if there is more than one outstanding
            //message to the server you would presumably want to associate the correlation ID with this
            //message somehow...a Map works good
            String correlationId = this.createRandomString();
            txtMessage.setJMSCorrelationID(correlationId);
            this.producer.send(txtMessage);

            System.out.println("Message ID: " + txtMessage.getJMSCorrelationID());
            System.out.println("Message command: " + txtMessage.getStringProperty("COMMAND"));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    @Override
    public void onMessage(Message message) {

        String messageText = null;

        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();

                System.out.println();
                System.out.println("Message ID: " + message.getJMSCorrelationID());
                System.out.println("Message reply: " + messageText);
                //System.out.println("messageText = " + messageText);
            }
        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        try {
            session.close();
            producer.close();
            connection.stop();
            System.out.println("Stopping client");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
