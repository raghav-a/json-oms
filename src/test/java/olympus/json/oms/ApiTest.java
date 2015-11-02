package olympus.json.oms;

import com.google.gson.Gson;
import olympus.apollo.ApolloServiceFactory;
import olympus.common.JID;
import olympus.common.OlympusService;
import olympus.json.message.payload.ChatState;
import olympus.json.message.payload.GenericPayload;
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

        String another = "{\"payloads\":{\"onAnyMessage\":{\"body\":\"Body! Body!\",\"content\":\"Lots of content\"}},\"oid\":\"2cbc7c3b-f1a5-1600-0000-000000000002\",\"flowId\":\"2cbc7c3b-f1a5-1600-0000-000000000002\",\"from\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"raghav\"},\"to\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"hemanshu\"},\"id\":\"message-id\"}";
        messageHandler.handle(another);

    }

    @Test
    public void test3() {
        Xson xson = new XsonBuilder().create();
        Message messageWithChatStatePayload = new Message();
        messageWithChatStatePayload.to(new JID("hemanshu@go.to"));
        messageWithChatStatePayload.from(new JID("raghav@go.to"));
        messageWithChatStatePayload.id("message-id");
        messageWithChatStatePayload.payload(new ChatState(ACTIVE));
        messageWithChatStatePayload.payload(new ChatState(COMPOSING));
        System.out.println(new Gson().toJson(messageWithChatStatePayload));


        Message messageWithGenericPayload = new Message();
        messageWithGenericPayload.to(new JID("hemanshu@go.to"));
        messageWithGenericPayload.from(new JID("raghav@go.to"));
        messageWithGenericPayload.id("message-id");
        GenericPayload payload = new GenericPayload();
        payload.put("content","Lots of content");
        payload.put("body","Body! Body!");
        messageWithGenericPayload.payload(payload);
        System.out.println(new Gson().toJson(messageWithGenericPayload));


    }
}
