package olympus.json.oms;

import olympus.common.JID;
import olympus.common.UserAgent;
import olympus.xmpp.oms.RequestHandlingException;
import olympus.xmpp.oms.TenantFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

public class MessageHandler {
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
            TenantFactory tenantFactory = tenantFactories.get(to.getPrimaryServiceName());
            Object tenant = tenantFactory.getTenant(to.getAppDomain(), version);
            MessageTenantMethod messageTenantMethod = apiResolver.getTenantMethod(tenant, api);
            if (messageTenantMethod == null) {
                throw new RequestHandlingException("Unknown api: " + api);
            }

            Class<?> builderType = messageTenantMethod.getBuilderClass();
            //RequestBuilder<?, ?> requestBuilder = xson.get().fromXML(xmlReader, builderType);
           // addEnvelopeParameters(requestBuilder, tenantMethod, envelope);

            //messageTenantMethod.invoke(tenant, requestBuilder);

        }





    }


}
