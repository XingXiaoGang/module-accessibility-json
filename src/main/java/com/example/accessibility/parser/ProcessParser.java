package com.example.accessibility.parser;

import android.content.Context;
import android.util.Log;

import com.example.accessibility.Statics;
import com.example.accessibility.bean.ProcessInfo;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 根据需要解析出具体的任务
 */
public class ProcessParser extends JsonParser<ProcessParser.ProcessInfoResult> {


    private int[] mProcessIds;

    /**
     * @param processId TaskInfo.processId
     **/
    public ProcessParser(Context context, int[] processId) {
        super(context);
        this.mProcessIds = processId;
    }

    @Override
    protected InputStream decodeJsonStream() {
        try {
            return mContext.getAssets().open(Statics.PROCESS_INFO_JSON_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ProcessInfoResult parse(InputStream jsonStream) {
        ProcessInfoResult processInfoResult = new ProcessInfoResult();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(jsonStream));
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "version": {
                        processInfoResult.version = reader.nextInt();
                        break;
                    }
                    case "process_items": {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            ProcessInfo info = GSON.fromJson(reader, ProcessInfo.class);
                            if (info != null && contains(info.id)) {
                                processInfoResult.processInfos.put(info.id, info);
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
        Log.e("test_access", "processInfoResult:" + processInfoResult);
        return processInfoResult;
    }

    private boolean contains(int id) {
        for (int i : mProcessIds) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    public class ProcessInfoResult {
        public int version;
        public Map<Integer, ProcessInfo> processInfos = new HashMap<>();

        @Override
        public String toString() {
            return "{version:" + version + ",[" + processInfos + "]}";
        }
    }

}
