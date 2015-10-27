package olympus.util;

import olympus.proteus.annotations.Proteus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

    private static Map<String, Method> cache = new HashMap<>();

    public static Method getNoArgsMethod(Class<?> clazz, String name) {
        Method m = null;
        try {
            m = clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
        }
        return m;
    }

    public static Method getMethod(Class<?> clazz, String name) {
        String key = clazz.getName() + ":" + name;
        if (!cache.containsKey(key)) {
            Method[] methods = clazz.getMethods();
            for(Method m : methods){
                if(m.getName().equals(name)) {
                    cache.put(key, m);
                    break;
                }
            }
        }
        return cache.get(key);
    }

    public static Method getProteusMethod(Class<?> clazz, String name) {
        String key = clazz.getName() + ":" + name;
        if (!cache.containsKey(key)) {
            Method[] methods = clazz.getMethods();
            for(Method m : methods){
                if(m.getName().equals(name) && m.isAnnotationPresent(Proteus.class)) {
                    cache.put(key, m);
                    break;
                }
            }
        }
        return cache.get(key);
    }
}
