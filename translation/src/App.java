import lib.Config;
import lib.Cpr;
import lib.EuCCID;
import router.CprEuRouter;
import router.EuCprRouter;
import services.Consumer;
import services.Producer;

public class App {

    public static void main(String[] args) {

        ///////////////////////////////////////////////////////////
        // FROM CPR -> EU-CCID
        CprEuRouter router = new CprEuRouter();
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Cpr person1 = new Cpr(
                "Martin", "Travolta", "1111111111", "Address 1",
                "Address 2", 10, "City", false, "2222222222",
                "3333333333", "4444444444", "5555555555");

        producer.run("cpr-eu", person1);
        consumer.run("cpr-eu");
        router.stop();


        ///////////////////////////////////////////////////////////
        // FROM CPR -> EU-CCID
        EuCprRouter router2 = new EuCprRouter();
        Producer producer2 = new Producer();
        Consumer consumer2 = new Consumer();

        EuCCID person2 = new EuCCID(
                "Martin", "Travolta", "1111110000",
                "male", "Street 1", 12, "Denmark",
                "Aarhus", "SVK", "DK"
        );

        producer2.run("eu-cpr", person2);
        consumer2.run("eu-cpr");
        router2.stop();

        System.out.println("Program is closing...");
        System.exit(0);

    }

}
