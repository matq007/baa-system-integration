import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class MessageProtocol {

    public String handleProtocolMessage(Message messageText) {

        String responseText = "NONE";
        try {
            String command = messageText.getStringProperty("COMMAND").trim();

            if (command.equals("getEuCCID")) {
                String dateTime = new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime());
                String euCCID = dateTime.toString();
                euCCID += "-" + uniqueNumber();
                responseText = euCCID;

                // STORE INTO DB
                //messageText.getStringProperty("NAME").trim();
                //messageText.getStringProperty("SURNAME").trim();
                //messageText.getStringProperty("GENDER").trim();
            }

        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }

        return responseText;
    }

    private int uniqueNumber() {
        // Should be some check with database
        return new Random().nextInt(9999999 - 1000000 + 1) + 1000000;
    }
}