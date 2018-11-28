package com.github.jingshouyan.jdbc.core.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeBindings;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author jingshouyan
 * 10/10/18 8:49 PM
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = Optional.<ObjectMapper>empty().orElseGet(()->{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    });


    /**
     * java bean json 序列化
     * @param value java bean
     * @return json 字符串
     */
    @SneakyThrows
    public static String toJsonString(Object value) {
        return OBJECT_MAPPER
//                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(value);
    }

    /**
     * json 转 java bean
     * @param json json 字符串
     * @param clazz java 类型
     * @param <T> java 类型
     * @return java bean
     */
    @SneakyThrows
    public static <T> T toBean(String json, Class<T> clazz){
        return OBJECT_MAPPER.readValue(json,clazz);
    }

    /**
     * json 转 java bean
     * @param json json字符串
     * @param clazz java 类型
     * @param classes java 类型中的泛型
     * @param <T> java 类型
     * @return java bean
     */
    @SneakyThrows
    public static <T> T toBean(String json, Class<T> clazz, Class<?>... classes){
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(clazz,classes);
        return OBJECT_MAPPER.readValue(json,javaType);
    }
    /**
     * json 转 java bean
     * @param json json字符串
     * @param type java 类型
     * @param <T> java 类型
     * @return java bean
     */
    @SneakyThrows
    public static <T> T toBean(String json, Type type){
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
        return OBJECT_MAPPER.readValue(json,javaType);
    }

    public static JavaType getJavaType(Type type, TypeBindings typeBindings) {
        return OBJECT_MAPPER.getTypeFactory().constructType(type,typeBindings);
    }

    /**
     * json 转 list
     * @param json json 字符串
     * @param clazz java 类型
     * @param <T> java 类型
     * @return java bean list
     */
    @SneakyThrows
    public static <T> List<T> toList(String json, Class<T> clazz){
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class,clazz);
        return OBJECT_MAPPER.readValue(json,javaType);
    }

    /**
     * json 字符串 转 json node
     * @param json json 字符串
     * @return json node
     */
    @SneakyThrows
    public static JsonNode readTree(String json){
        return OBJECT_MAPPER.readTree(json);
    }

    /**
     * java bean 转 json node
     * @param obj java bean
     * @return json node
     */
    public static JsonNode valueToTree(Object obj){
        return OBJECT_MAPPER.valueToTree(obj);
    }

    /**
     * 获取 json 中 key 的值
     * @param json FastJSON 对象
     * @param key 含层级的key 例：cs.[0]?.b.name
     *            ?. 表示当前层级为null则返回null
     *            . 当前层级为null会抛出NPE
     * @return json 中的值
     */
    public static JsonNode get(JsonNode json, String key){
        String pattern = "^\\[\\d]$";
        String[] keys = key.split("\\.");
        JsonNode obj = json;
        for (int i = 0; i < keys.length; i++) {
            String str = keys[i];
            if(str.endsWith("?")){
                str = str.substring(0,str.length()-1);
                if(null == obj){
                    return null;
                }
            }
            if(Pattern.matches(pattern,str)){
                int index = Integer.parseInt(str.substring(1,str.length()-1));
                obj = obj.get(index);
            }else {
                obj = obj.get(str);
            }
        }
        return obj;
    }
}
