package com.example.accessibility.bean;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Created by xingxiaogang on 2016/5/20.
 */
public abstract class Base {

    private static Gson GSON;

    static {
        GSON = new Gson();
    }

    public <T extends Base> T fromJson(String json, Class<? extends Base> clazz) {
        return (T) GSON.fromJson(json, clazz);
    }

    public static <T extends Base> T fromJson(JsonReader reader, Class<? extends Base> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public String toJsonString() {
        return GSON.toJson(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
