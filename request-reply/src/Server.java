import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Server implements MessageListener {

    private static final String messageQueueName = "client.messages";

    private Session session;
    private MessageProducer replyProducer;
    private MessageProtocol messageProtocol;
    private Connection connection;
    private BrokerService broker;

    public Server() {
        try {
            //This message broker is embedded
            broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.messageProtocol = new MessageProtocol();
        this.setupMessageQueueConsumer();
    }

    private void setupMessageQueueConsumer() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination adminQueue = this.session.createQueue(messageQueueName);

            //Setup a message producer to respond to messages from clients, we will get the destination
            //to send to from the JMSReplyTo header field from a Message
            this.replyProducer = this.session.createProducer(null);
            this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //Set up a consumer to consume messages off of the admin queue
            MessageConsumer consumer = this.session.createConsumer(adminQueue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message) {

        try {
            TextMessage response = this.session.createTextMessage();
            if (message instanceof TextMessage) {
                response.setText(this.messageProtocol.handleProtocolMessage(message));
            }

            //Set the correlation ID from the received message to be the correlation id of the response message
            //this lets the client identify which message this is a response to if it has more than
            //one outstanding message to the server
            response.setJMSCorrelationID(message.getJMSCorrelationID());

            //Send the response to the Destination specified by the JMSReplyTo field of the received message,
            //this is presumably a temporary queue created by the client
            this.replyProducer.send(message.getJMSReplyTo(), response);

        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        try {
            broker.stop();
            session.close();
            connection.stop();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}