package translator;

import lib.Cpr;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class EuCprTranslator implements Processor {

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

        String eu = root.getAttribute("number");
        int gender = (get("gender").equals("male")) ? 1 : 0;
        String number = eu.substring(0,2) + eu.substring(2,4) + eu.substring(6,8) + "000" + gender;

        Cpr cpr = new Cpr(
                get("christianName"),
                get("familyName"),
                number,
                get("street") + " " + get("apartmentNumber"),
                "-",
                1234,
                get("city"),
                false,
                "SPOUSE NUMBER",
                "CHILDREN CPR",
                "PARENTS CPR",
                "DOCTOR CPR"
        );

        return cpr.exportXML();

    }

    public static Document parseXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
