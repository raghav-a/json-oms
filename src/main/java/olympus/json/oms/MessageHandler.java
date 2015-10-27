package olympus.json.oms;

import com.google.gson.Gson;
import olympus.builder.MessageBuilder;
import olympus.common.JID;
import olympus.common.UserAgent;
import olympus.util.ReflectionUtils;
import olympus.xmpp.oms.RequestHandlingException;
import olympus.xmpp.oms.TenantFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private static final Gson gson = new Gson();
    private JID sessionJID;
    private final UserAgent userAgent;
    private final APIResolver apiResolver;
    private final Map<String, TenantFactory> tenantFactories;

    public MessageHandler(JID sessionJID, UserAgent userAgent, APIResolver apiResolver, Map<String, TenantFactory> tenantFactories) {
        this.sessionJID = sessionJID;
        this.userAgent = userAgent;
        this.apiResolver = apiResolver;
        this.tenantFactories = tenantFactories;
    }

    public void handle(String messageData) throws IOException {

        if (messageData != null && !messageData.isEmpty()) {
            Map<String, Object> postMap = new ObjectMapper().readValue(messageData, new TypeReference<Map<String, Object>>() {
            });
            JID to = new JID((String)postMap.get("to"));
            String version = (String)postMap.get("version");
            String api  = (String)postMap.get("api");
            String type   = (String)postMap.get("type");
            String id   = (String)postMap.get("id");

            TenantFactory tenantFactory = tenantFactories.get(to.getPrimaryServiceName());
            Object tenant = tenantFactory.getTenant(to.getAppDomain(), version);
            MessageTenantMethod messageTenantMethod = apiResolver.getTenantMethod(tenant, api);
            if (messageTenantMethod == null) {
                throw new RequestHandlingException("Unknown api: " + api);
            }

            Object[] build = build(messageTenantMethod.method, to, sessionJID, type, id, postMap);
            messageTenantMethod.invoke(tenant, build);

        }

    }

    public Object[] build(Method method, JID to, JID sessionJID, String type, String id, Map<String, Object> postDoc) {
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

        messageBuilder.from(sessionJID);
        messageBuilder.to(to);
        messageBuilder.type(type);
        messageBuilder.id(id);

        for (Map.Entry<String, Object> entry : postDoc.entrySet()) {
            try {
                Method m = ReflectionUtils.getMethod(messageBuilderClazz, entry.getKey());
                if (m != null) {
                    String json = gson.toJson(entry.getValue());
                    Object arg = gson.fromJson(json, m.getParameterTypes()[0]);
                    m.invoke(messageBuilder, arg);
                } else {
                    logger.debug("no builder method found for property {}. ignoring.", entry.getKey());
                }
            } catch (Throwable cause) {
                logger.error("error while invoking builder", cause);
            }
        }

        return args;
    }

    public static void main(String[] args) {

    }


}
