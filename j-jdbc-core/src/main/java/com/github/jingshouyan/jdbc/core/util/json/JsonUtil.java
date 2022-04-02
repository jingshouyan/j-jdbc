package com.github.jingshouyan.jdbc.core.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author jingshouyan
 * 10/10/18 8:49 PM
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = Optional.<ObjectMapper>empty().orElseGet(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    });


    /**
     * java bean json 序列化
     *
     * @param value java bean
     * @return json 字符串
     */
    @SneakyThrows
    public static String toJsonString(Object value) {
        return OBJECT_MAPPER
                .writeValueAsString(value);
    }

    /**
     * json 转 java bean
     *
     * @param json json字符串
     * @param type java 类型
     * @param <T>  java 类型
     * @return java bean
     */
    @SneakyThrows
    public static <T> T toBean(String json, Type type) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
        return OBJECT_MAPPER.readValue(json, javaType);
    }


}
