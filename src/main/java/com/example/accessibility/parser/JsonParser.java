package com.example.accessibility.parser;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public abstract class JsonParser<T> {

    protected Context mContext;
    protected static Gson GSON;

    static {
        GSON = new Gson();
    }

    public JsonParser(Context context) {
        this.mContext = context;
    }

    protected abstract InputStream decodeJsonStream();

    protected abstract T parse(InputStream jsonStream);

    public final T parse() {
        InputStream stream = decodeJsonStream();
        if (stream != null) {
            return parse(stream);
        } else {
            Log.e("json_parse", "error: InputStream is null");
        }
        return null;
    }
}
