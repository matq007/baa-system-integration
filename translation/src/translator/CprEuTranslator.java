package translator;

import lib.EuCCID;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class CprEuTranslator implements Processor {

    private Document doc;
    private Element root;

    @Override
    public void process(Exchange exchange) throws Exception {

        String xml = exchange.getIn().getBody(String.class);
        doc = parseXML(xml);
        root = doc.getDocumentElement();
        exchange.getIn().setBody(translate());

    }

    public String get(String elementName) {
        return root.getElementsByTagName(elementName).item(0).getFirstChild().getNodeValue();
    }

    public String translate() {

        String cpr = root.getAttribute("number");
        String number = cpr.substring(0,2) + cpr.substring(2,4) + "19" + cpr.substring(4,6) + "000000";
        String gender = (Integer.parseInt(cpr.substring(cpr.length() - 1)) % 2 == 0) ? "female" : "male";
        String address = get("address1");

        EuCCID eu = new EuCCID(
                get("firstName"),
                get("surname"),
                number,
                gender,
                address,
                Integer.parseInt(address.substring(address.length() - 1)),
                "COUNTRY (FROM DATABASE)",
                get("city"),
                "BIRTH COUNTRY (FROM DATABASE)",
                "Unknown"
        );

        return eu.exportXML();

    }

    public static Document parseXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
