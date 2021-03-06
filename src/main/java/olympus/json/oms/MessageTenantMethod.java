package olympus.json.oms;


import olympus.json.message.builder.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MessageTenantMethod {

    final Method method;
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

    public void invoke(Object tenant, Object[] params){
        try {
            method.invoke(tenant, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
