package olympus.json.oms;

import com.google.gson.Gson;
import olympus.apollo.ApolloServiceFactory;
import olympus.common.JID;
import olympus.common.OlympusService;
import olympus.json.message.payload.ChatState;
import olympus.message.types.Message;
import olympus.xmpp.oms.TenantFactory;
import olympus.xson.Xson;
import olympus.xson.XsonBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static olympus.json.message.payload.ChatState.Type.ACTIVE;
import static olympus.json.message.payload.ChatState.Type.COMPOSING;


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
                "{\"payloads\":{\"chatState\":{\"t\":\"COMPOSING\"}},\"oid\":\"37dbf091-69eb-8a00-0000-000000000001\",\"flowId\":\"37dbf091-69eb-8a00-0000-000000000001\"" +
                        ",\"from\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"raghav\"}," +
                        "\"to\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"hemanshu\"}, " +
                        "\"id\":\"message-id\", \"api\":\"chatState\"}");


    }

    @Test
    public void test3() {
        Xson xson = new XsonBuilder().create();
        Message src = new Message();
        src.to(new JID("hemanshu@go.to"));
        src.from(new JID("raghav@go.to"));
        src.id("message-id");
        src.payload(new ChatState(ACTIVE));
        src.payload(new ChatState(COMPOSING));
        System.out.println(new Gson().toJson(src));
    }
}
