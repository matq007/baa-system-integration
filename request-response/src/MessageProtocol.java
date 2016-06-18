import javax.jms.*;


public class MessageProtocol {

    public String handleProtocolMessage(Message messageText) {

        String responseText = "ERROR FAIL";

        try {

            String flightNumber = messageText.getStringProperty("FLIGHT");

            if (flightNumber.equals("FL1234")) {
                responseText = "12.03.2016 14:00";
            }

        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }

        return responseText;
    }
}