package olympus.json.oms;

import com.google.gson.Gson;
import olympus.apollo.ApolloServiceFactory;
import olympus.common.JID;
import olympus.common.OlympusService;
import olympus.json.message.payload.ChatState;
import olympus.json.message.payload.GenericMessagePayload;
import olympus.json.message.payload.IgnoreAction;
import olympus.message.types.Message;
import olympus.xmpp.oms.TenantFactory;
import olympus.xson.Xson;
import olympus.xson.XsonBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static olympus.json.message.payload.Action.History;
import static olympus.json.message.payload.Action.Notify;


public class ApiTest {

    @Ignore
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
        /*messageHandler.handle(
                "{\"payloads\":{\"chatState\":{\"t\":\"COMPOSING\"}},\"oid\":\"37dbf091-69eb-8a00-0000-000000000001\",\"flowId\":\"37dbf091-69eb-8a00-0000-000000000001\"" +
                        ",\"from\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"raghav\"}," +
                        "\"to\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"hemanshu\"}, " +
                        "\"id\":\"message-id\"}");

        String another = "{\"payloads\":{\"genericMessage\":{\"body\":\"Body! Body!\",\"content\":\"Lots of content\"}}," +
                "\"oid\":\"2cbc7c3b-f1a5-1600-0000-000000000002\",\"flowId\":\"2cbc7c3b-f1a5-1600-0000-000000000002\"," +
                "\"from\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"raghav\"}," +
                "\"to\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"hemanshu\"}," +
                "\"id\":\"message-id\"}";
        messageHandler.handle(another);*/

        String withActions = "{\"payloads\":{\"chatState\":{\"t\":\"COMPOSING\",\"actions\":[\"History\",\"Notify\"],\"ignoreActions\":[\"Reflection\"]}},\"oid\":\"37dbf091-69eb-8a00-0000-000000000001\",\"flowId\":\"37dbf091-69eb-8a00-0000-000000000001\"" +
                ",\"from\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"raghav\"}," +
                "\"to\":{\"appDomain\":\"go.to\",\"serviceName\":\"apollo\",\"nodeId\":\"hemanshu\"}, " +
                "\"id\":\"message-id\"}";

        messageHandler.handle(withActions);
    }

    @Test
    public void test3() {
        Xson xson = new XsonBuilder().create();
        Message messageWithChatStatePayload = new Message();
        messageWithChatStatePayload.to(new JID("hemanshu@go.to"));
        messageWithChatStatePayload.from(new JID("raghav@go.to"));
        messageWithChatStatePayload.id("message-id");
        messageWithChatStatePayload.payload(new ChatState(ChatState.Type.ACTIVE, newArrayList(), newArrayList()));
        messageWithChatStatePayload.payload(new ChatState(ChatState.Type.COMPOSING, newArrayList(), newArrayList()));
        System.out.println(new Gson().toJson(messageWithChatStatePayload));


        Message messageWithGenericPayload = new Message();
        messageWithGenericPayload.to(new JID("hemanshu@go.to"));
        messageWithGenericPayload.from(new JID("raghav@go.to"));
        messageWithGenericPayload.id("message-id");
        GenericMessagePayload payload = new GenericMessagePayload();
        payload.put("content", "Lots of content");
        payload.put("body", "Body! Body!");
        messageWithGenericPayload.payload(payload);
        System.out.println(new Gson().toJson(messageWithGenericPayload));


        Message mess2 = new Message();
        mess2.to(new JID("hemanshu@go.to"));
        mess2.from(new JID("raghav@go.to"));
        mess2.id("message-id");
        mess2.payload(new ChatState(ChatState.Type.ACTIVE, newArrayList(History, Notify), newArrayList(IgnoreAction.Reflection)));
        System.out.println(new Gson().toJson(mess2));


    }
}
