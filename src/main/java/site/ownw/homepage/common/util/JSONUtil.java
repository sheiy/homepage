package site.ownw.homepage.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JSONUtil {

    private static volatile ObjectMapper objectMapper;

    @SneakyThrows
    public static String toJSON(Object object) {
        return getObjectMapper().writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T fromJSON(String json, Class<T> valueType) {
        return getObjectMapper().readValue(json, valueType);
    }

    @SneakyThrows
    public static <T> T fromJSON(String json, TypeReference<T> valueTypeRef) {
        return getObjectMapper().readValue(json, valueTypeRef);
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (JSONUtil.class) {
                if (objectMapper == null) {
                    objectMapper = AppUtil.getApplicationContext().getBean(ObjectMapper.class);
                }
            }
        }
        return objectMapper;
    }
}
