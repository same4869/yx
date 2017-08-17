package com.ml.yx.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

/**
 *
 */
public class JsonToBeanHandler {
    public static final String tag = JsonToBeanHandler.class.getSimpleName();

    private static JsonToBeanHandler instance;

    private Gson gsonMapper;

    private JsonToBeanHandler() {
        GsonBuilder builer = new GsonBuilder();
        builer.disableHtmlEscaping();

        gsonMapper = builer.create();
    }

    private static synchronized void initInstanceSyn() {
        if (instance == null) {
            instance = new JsonToBeanHandler();
        }
    }

    public static synchronized JsonToBeanHandler getInstance() {
        if (instance == null) {
            initInstanceSyn();
        }
        return instance;
    }

    /**
     * 数据源转化为 对象bean
     *
     * @param data
     * @param cls
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    public <T> T fromJsonString(String data, Class<T> cls) throws JsonSyntaxException {
        return gsonMapper.fromJson(data, cls);
    }


    /**
     * 对象bean转化为json字符串
     *
     * @param value
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    public String toJsonString(Object value) throws Exception {
        return gsonMapper.toJson(value);
    }

    /**
     * 将对象转化为 字节数组
     * @param value
     * @return
     * @throws Exception
     */
    public byte[] toByteArray(Object value) throws Exception {
        byte[] data = gsonMapper.toJson(value).getBytes();

        return data;
    }

    /**
     * 数据源转化为 对象bean
     *
     * @param data
     * @param cls
     *
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    public <T> T fromByteArray(byte[] data, Class<T> cls) throws JsonParseException {
        if (data == null) {
            return null;
        }

        String dataString = new String(data);

        //bug compatiblity
        if (dataString.lastIndexOf("}") != dataString.length() - 1) {
            dataString = dataString.substring(0, dataString.lastIndexOf("}") + 1);
        }

        return gsonMapper.fromJson(dataString, cls);
    }


}
