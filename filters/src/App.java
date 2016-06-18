import javax.jms.JMSException;

/**
 * Created by mproksik on 04/03/16.
 */
public class App {

    public static void main(String[] args) {

        /*Publisher publisher = new Publisher();

        try {
            publisher.create("PUBLISHER", "SKUSKA");
            publisher.sendName("MARTIN", "MARTIN");

        } catch (JMSException ex) {
            System.out.println(ex.getMessage());
        }*/

        try {
            Subscriber subscriber = new Subscriber();
            subscriber.create("SUBSCRIBER", "SKUSKA");
            subscriber.getGreeting(30);

        } catch (JMSException ex) {
            System.out.println(ex.getMessage());
        }

        //publisher.closeConnection();
        //subscriber.closeConnection();
    }

}
