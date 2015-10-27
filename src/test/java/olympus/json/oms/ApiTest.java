package olympus.json.oms;

import olympus.apollo.ApolloServiceFactory;
import olympus.common.JID;
import olympus.common.OlympusService;
import olympus.xmpp.oms.TenantFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiTest {

    @Test
    public void test() throws IOException {
        Map<String, TenantFactory> tenantFactoryMap = new HashMap<>();
        ApolloServiceFactory apolloServiceFactory =
                new ApolloServiceFactory(new MessagingServiceStub(OlympusService.apollo));

        tenantFactoryMap.put(OlympusService.apollo.getPrimaryName(), new TenantFactory() {
            @Override
            public Object getTenant(String appDomain, String version) {
                return apolloServiceFactory.getService(appDomain);
            }
        });

        MessageHandlerFactory messageHandlerFactory = new MessageHandlerFactory(tenantFactoryMap);
        MessageHandler messageHandler = messageHandlerFactory.newMessageHandler(new JID("raghav@go.to"), "socket");
        messageHandler.handle(
                "{\"to\":\"raghav@go.to\"" +
                        ",\"id\" :\"xyz\"," +
                        "\"type\":\"xyz\"," +
                        "\"api\":\"chatstate\"," +
                        "\"payload\":{\"state\":\"typing\"}" +
                        "}");


    }

    @Test
    public void test3() {
        JID jid = new JID("raghava@go.to");
    }
}
