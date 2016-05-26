package com.example.accessibility.parser;

import android.content.Context;
import android.util.Log;

import com.example.accessibility.bean.IntentInfo;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class IntentParser extends JsonParser<IntentParser.IntentResult> {

    private static final String INTENT_INFO_JSON_PATH = "permission/intent_info_data.json";
    private int[] mIntentIds;

    public IntentParser(Context context, int[] intentIds) {
        super(context);
        this.mIntentIds = intentIds;
    }

    @Override
    protected InputStream decodeJsonStream() {
        try {
            return mContext.getAssets().open(INTENT_INFO_JSON_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected IntentResult parse(InputStream jsonStream) {
        IntentResult intentResult = new IntentResult();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(jsonStream));
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "version": {
                        intentResult.version = reader.nextInt();
                        break;
                    }
                    case "intent_items": {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            IntentInfo info = GSON.fromJson(reader, IntentInfo.class);
                            if (info != null && contains(info.id)) {
                                intentResult.intentInfo.put(info.id, info);
                            }
                        }
                        reader.endArray();
                        break;
                    }
                    default: {
                        reader.skipValue();
                        break;
                    }
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (jsonStream != null) {
                    jsonStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("test_access", "intentResult:" + intentResult);
        return intentResult;
    }

    private boolean contains(int id) {
        for (int i : mIntentIds) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }


    public class IntentResult {
        public int version;
        public Map<Integer, IntentInfo> intentInfo = new HashMap<>();

        @Override
        public String toString() {
            return "{version:" + version + ",[" + intentInfo + "]}";
        }
    }

}
