/**
 * Created by mproksik on 06/04/16.
 */
public class App {

    public static void main(String[] args) {

        Server server = new Server();
        Client client = new Client();

        client.send();

        try {
            Thread.sleep(2000);
            client.stop();
            server.stop();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.exit(0);
    }

}
