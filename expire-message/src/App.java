/**
 * Created by mproksik on 09/02/16.
 */

public class App {

    public static void main(String[] args) {

        Producer sender = new Producer();
        Consumer receiver = new Consumer();

        sender.run();

        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        receiver.run();

        System.exit(0);

    }


}
