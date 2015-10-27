package olympus.json.oms;

import olympus.common.JID;
import olympus.xmpp.oms.TenantFactory;

import java.util.Map;

public class MessageHandlerFactory {
    private final Map<String, TenantFactory> tenantFactories;
    private final APIResolver apiResolver;

    public MessageHandlerFactory(Map<String, TenantFactory> tenantFactories) {
        this.tenantFactories = tenantFactories;
        this.apiResolver = new APIResolver();
    }

    public MessageHandler newMessageHandler(JID sessionJID, String socketID) {
        return new MessageHandler(sessionJID, socketID, apiResolver, tenantFactories);
    }
}
