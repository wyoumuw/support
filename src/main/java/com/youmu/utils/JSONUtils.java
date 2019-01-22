package com.youmu.utils;

import java.io.IOException;
import java.text.DateFormat;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.youmu.exception.WrappedThrowable;

/**
 * Created by YOUMU on 2017/7/11.
 */
public class JSONUtils {

    private static ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static <T> String serialize(T obj) {
        if (null == obj) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new WrappedThrowable(e);
        }
    }

    /**
     * String jsonString="[{'id':'1'},{'id':'2'}]"; <br>
     * ObjectMapper mapper = new ObjectMapper(); <br>
     * JavaType javaType=mapper.getTypeFactory().constructParametricType(List.class,
     * Bean.class);<br>
     * //如果是Map类型 <br>
     * mapper.getTypeFactory().constructParametricType(HashMap.class,String.
     * class,Bean.class); <br>
     * List<Bean> lst = (List<Bean>)mapper.readValue(jsonString, javaType); <br/>
     * 也可以查看
     * {@link JSONUtils#deserialize(java.lang.String, com.fasterxml.jackson.core.type.TypeReference)}
     * @param str 要反序列化的内容
     * @param clazz 要转换的类型的类
     * @param <T> 要转换成的类型
     */
    public static <T> T deserialize(String str, Class<T> clazz) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            throw new WrappedThrowable(e);
        }
    }

    /**
     * @param str
     * @param tf
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T deserialize(String str, TypeReference<T> tf) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, tf);
        } catch (IOException e) {
            throw new WrappedThrowable(e);
        }
    }

    /**
     * 此方法创建一个JsonNode是对象类型的，如果是array类型则使用 {@link JSONUtils#createArray()} <br>
     * &nbsp;JsonNode <br>
     * &nbsp;&nbsp;↑ <br>
     * &nbsp;&nbsp;| <br>
     * &nbsp;&nbsp;| <br>
     * ArrayNode ObjectNode<br>
     * @return
     */
    public static ObjectNode createNode() {
        return objectMapper.createObjectNode();
    }

    public static ArrayNode createArray() {
        return objectMapper.createArrayNode();
    }

    /**
     * 所有 obj里的date类型都会按照df转
     * @param obj
     * @param df
     * @param <T>
     * @return
     */
    public static <T> String serialize(T obj, DateFormat df) {
        try {
            return objectMapper.writer().with(df).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new WrappedThrowable(e);
        }
    }

    public static <T> JsonNode serializeJsonNode(T obj, DateFormat df) {
        if (obj == null)
            return null;
        TokenBuffer buf = null;
        try {
            buf = new TokenBuffer(objectMapper, false);
            if (objectMapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                buf = buf.forceUseOfBigDecimal(true);
            }
            JsonNode result;
            objectMapper.writer().with(df).writeValue(buf, obj);
            JsonParser p = buf.asParser();
            result = objectMapper.readTree(p);
            p.close();
            return result;
        } catch (Exception e) {
            throw new WrappedThrowable(e);
        } finally {
            if (null != buf) {
                try {
                    buf.close();
                } catch (IOException e) {
                    throw new WrappedThrowable(e);
                }
            }
        }
    }
}
