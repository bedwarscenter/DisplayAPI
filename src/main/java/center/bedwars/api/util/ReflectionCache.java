package center.bedwars.api.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionCache {

    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    private ReflectionCache() {
    }

    public static void setField(Object object, String fieldName, Object value) {
        try {
            String key = object.getClass().getName() + "." + fieldName;
            Field field = FIELD_CACHE.computeIfAbsent(key, k -> {
                try {
                    Field f = object.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            });
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    public static Object getField(Object object, String fieldName) {
        try {
            String key = object.getClass().getName() + "." + fieldName;
            Field field = FIELD_CACHE.computeIfAbsent(key, k -> {
                try {
                    Field f = object.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            });
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field: " + fieldName, e);
        }
    }

    public static void clearCache() {
        FIELD_CACHE.clear();
    }

    public static int getCacheSize() {
        return FIELD_CACHE.size();
    }
}
