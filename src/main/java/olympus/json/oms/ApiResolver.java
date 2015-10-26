package olympus.json.oms;

import olympus.json.oms.annotations.JSON;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class APIResolver {


    private final ConcurrentMap<Class<?>, Map<String, MessageTenantMethod>> tenantMethods = new ConcurrentHashMap<>();

    MessageTenantMethod getTenantMethod(Object tenant, String api) {
        if (!tenantMethods.containsKey(tenant.getClass())) {
            populateTenantMethods(tenant);
        }

        return tenantMethods.get(tenant.getClass()).get(api);
    }

    private void populateTenantMethods(Object tenant) {
        final Map<String, MessageTenantMethod> methods = new HashMap<>();
        final Class<?> tenantClass = tenant.getClass();

        for (Method method : tenantClass.getMethods()) {
            JSON jsonAnnotation = method.getAnnotation(JSON.class);
            if (jsonAnnotation == null) {
                continue;
            }

            String apiName = method.getName();
            methods.put(apiName, new MessageTenantMethod(method));
        }

        tenantMethods.putIfAbsent(tenantClass, methods);
    }
}
