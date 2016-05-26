package com.example.accessibility.parser;

import android.content.Context;

import com.example.accessibility.bean.ActionInfo;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class ActionParser extends JsonParser<ActionParser.ActionResult> {

    private static final String INTENT_INFO_JSON_PATH = "permission/action_info_data.json";
    private List<Integer> mIntentIds = new ArrayList<>();

    public ActionParser(Context context, List<int[]> intentIds) {
        super(context);
        if (intentIds != null) {
            for (int[] ids : intentIds) {
                for (int i : ids) {
                    mIntentIds.add(i);
                }
            }
        }
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
    protected ActionResult parse(InputStream jsonStream) {
        ActionResult rulesResult = new ActionResult();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(jsonStream));
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "version": {
                        rulesResult.version = reader.nextInt();
                        break;
                    }
                    case "action_items": {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            ActionInfo info = GSON.fromJson(reader, ActionInfo.class);
                            if (info != null && contains(info.id)) {
                                rulesResult.actionInfo.put(info.id, info);
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
        return rulesResult;
    }

    private boolean contains(int id) {
        for (int i : mIntentIds) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    public class ActionResult {
        public int version;
        public Map<Integer, ActionInfo> actionInfo = new HashMap<>();

        @Override
        public String toString() {
            return "{version:" + version + ",[" + actionInfo + "]}";
        }
    }
}
