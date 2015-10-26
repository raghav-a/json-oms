package olympus.json.oms;

import olympus.builder.ChatState;
import olympus.builder.MessageBuilder;
import olympus.json.oms.annotations.JSON;
import olympus.message.types.RequestBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MessageTenantMethod {

    private final Method method;
    private final Class<? extends MessageBuilder> builderClass;
    private Map<String, Method> builderMethods;

    public MessageTenantMethod(Method method) {
        this.method = method;
        Class<?> firstparamType = method.getParameterTypes()[0];
        this.builderClass = (Class<? extends MessageBuilder>) firstparamType;
        this.builderMethods = new HashMap<>();
        for (Method builderMethod : this.builderClass.getMethods()) {
            if (builderMethod.getParameterTypes().length == 1) {
                builderMethods.put(builderMethod.getName(), builderMethod);
            }
        }
    }

    public Class<? extends MessageBuilder> getBuilderClass() {
        return builderClass;
    }

    public void invoke(Object tenant, RequestBuilder<?, ?> requestBuilder){

    }


    public static class Service{

        @JSON
        public void api(ChatState.Builder ch) {
        }
    }

    public static void main(String[] args) {
        APIResolver apiResolver = new APIResolver();
        MessageTenantMethod api = apiResolver.getTenantMethod(new Service(), "api");
        System.out.println(api);

    }
}
