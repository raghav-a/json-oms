package olympus.json.oms;

import com.google.gson.Gson;
import olympus.common.JID;
import olympus.json.message.builder.MessageBuilder;
import olympus.json.message.payload.GenericMessagePayload;
import olympus.message.json.JSONSerializer;
import olympus.util.ReflectionUtils;
import olympus.xmpp.oms.RequestHandlingException;
import olympus.xmpp.oms.TenantFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private static final Gson gson = new Gson();
    private JID sessionJID;
    private final String socketID;
    private final APIResolver apiResolver;
    private final Map<String, TenantFactory> tenantFactories;
    private JSONSerializer json = new olympus.message.json.JSONSerializer();

    public MessageHandler(JID sessionJID, String socketID, APIResolver apiResolver, Map<String, TenantFactory> tenantFactories) {
        this.sessionJID = sessionJID;
        this.socketID = socketID;
        this.apiResolver = apiResolver;
        this.tenantFactories = tenantFactories;
    }

    public void handle(String messageData) throws IOException {
        if (messageData != null && !messageData.isEmpty()) {

            Map<String, Object> mapOfJsonRcvdFromDoor = new ObjectMapper().readValue(messageData, new TypeReference<Map<String, Object>>() {
            });

            String payloadsInJson = gson.toJson(mapOfJsonRcvdFromDoor.remove("payloads"));
            Map<String, Object> payloads = new ObjectMapper().readValue(payloadsInJson, new TypeReference<Map<String, Object>>() {
            });


            String api;
            Map<String, Object> dataMapOfFirstPayload;

            if (payloads.isEmpty()) {
                throw new IllegalStateException("payloads cannot be empty" + messageData);
            } else if (payloads.size() > 1) {
                throw new IllegalStateException("payloads cannot be more than one" + messageData);
            } else {
                Map.Entry<String, Object> firstPayload = payloads.entrySet().iterator().next();
                api = firstPayload.getKey();
                dataMapOfFirstPayload = new ObjectMapper().readValue(gson.toJson(firstPayload.getValue()), new TypeReference<Map<String, Object>>() {
                });
                String id = (String) mapOfJsonRcvdFromDoor.remove("id");
                JID to = new ObjectMapper().readValue(gson.toJson(mapOfJsonRcvdFromDoor.remove("to")), new TypeReference<JID>() {
                });
                mapOfJsonRcvdFromDoor.put("socketId", socketID);
                TenantFactory tenantFactory = tenantFactories.get(to.getPrimaryServiceName());
                Object tenant = tenantFactory.getTenant(to.getAppDomain(), null);
                MessageTenantMethod messageTenantMethod = apiResolver.getTenantMethod(tenant, api);
                if (messageTenantMethod == null) {
                    throw new RequestHandlingException("Unknown api: " + api);
                }
                Object[] params = getParametersForApiMethodCall(messageTenantMethod.method, to, sessionJID, dataMapOfFirstPayload, id, mapOfJsonRcvdFromDoor);
                messageTenantMethod.invoke(tenant, params);
            }
        }
    }

    private Object[] getParametersForApiMethodCall(Method method, JID to, JID sessionJID, Map<String, Object> postDoc, String id, Map<String, Object> mapOfJsonRcvdFromDoor) {


        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes == null || paramTypes.length == 0 || paramTypes.length > 2) {
            throw new IllegalArgumentException("incorrect argument list in method");
        }
        if (!MessageBuilder.class.isAssignableFrom(paramTypes[0])) {
            throw new IllegalArgumentException("incorrect argument type in method");
        }
        /*if (paramTypes.length > 1 && !FragmentListener.class.isAssignableFrom(paramTypes[1])) {
            throw new IllegalArgumentException("incorrect argument type in method");
        }*/
        // setup the argument list

        Object args[] = new Object[paramTypes.length];
        Class<?> messageBuilderClazz = method.getParameterTypes()[0];
        MessageBuilder messageBuilder;
        try {
            messageBuilder = (MessageBuilder) messageBuilderClazz.newInstance();
            args[0] = messageBuilder;
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }

        for (Map.Entry<String, Object> entry : mapOfJsonRcvdFromDoor.entrySet()) {
            Object entryValue = entry.getValue();
            if (entryValue instanceof String) {
                String value = (String) entryValue;
                messageBuilder.addAttribute(entry.getKey(), value);
            }
        }

        messageBuilder.from(sessionJID);
        messageBuilder.to(to);
        messageBuilder.id(id);

        for (Map.Entry<String, Object> entry : postDoc.entrySet()) {
            try {
                if (messageBuilder instanceof GenericMessagePayload.Builder) {
                    ((GenericMessagePayload.Builder) messageBuilder).put(entry.getKey(), entry.getValue());
                } else {
                    Method m = ReflectionUtils.getMethod(messageBuilderClazz, entry.getKey());
                    if (m != null) {
                        System.out.println("method" + method);
                        String json = gson.toJson(entry.getValue());
                        Object arg = gson.fromJson(json, m.getParameterTypes()[0]);
                        m.invoke(messageBuilder, arg);
                    } else {
                        logger.debug("no builder method found for property {}. ignoring.", entry.getKey());
                    }
                }


            } catch (Throwable cause) {
                logger.error("error while invoking builder", cause);
            }
        }

        return args;
    }

    public static void main(String[] args) {

    }


    public static class Payloads {
        private List<Object> payloads;

        public List<Object> getPayloads() {
            return payloads;
        }

        public void setPayloads(List<Object> payloads) {
            this.payloads = payloads;
        }
    }
}
