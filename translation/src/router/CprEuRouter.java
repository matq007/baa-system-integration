package router;

import lib.Config;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import translator.CprEuTranslator;

public class CprEuRouter {

    private static CamelContext context;

    public CprEuRouter() {

        try {
            context = new DefaultCamelContext();
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    Config.username, Config.password, ActiveMQConnection.DEFAULT_BROKER_URL);

            context.addComponent("jms",
                    JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("jms:queue:" + Config.sourceQueueCprEu)
                            .process(new CprEuTranslator())
                            .to("jms:queue:" + Config.destQueueCprEu);
                    }
            });

            context.start();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void stop() {
        try {
            context.stop();
            System.out.println("Stopping router");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
