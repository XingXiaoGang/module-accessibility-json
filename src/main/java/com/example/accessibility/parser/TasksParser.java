package com.example.accessibility.parser;

import android.content.Context;
import android.util.Log;

import com.example.accessibility.Statics;
import com.example.accessibility.bean.TaskInfo;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 根据romId解析出对应的所有任务
 */
public class TasksParser extends JsonParser<TasksParser.TasksResult> {

    private int mRomIdToFind;

    public TasksParser(Context context, int romId) {
        super(context);
        this.mRomIdToFind = romId;
    }

    @Override
    protected InputStream decodeJsonStream() {
        try {
            return mContext.getAssets().open(Statics.TASK_INFO_JSON_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected TasksResult parse(InputStream jsonStream) {
        TasksResult tasksResult = new TasksResult();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(jsonStream));
            reader.setLenient(true);
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "version": {
                        tasksResult.version = reader.nextInt();
                        break;
                    }
                    case "task_items": {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            TaskInfo info = GSON.fromJson(reader, TaskInfo.class);
                            if (info != null && info.romId == mRomIdToFind) {
                                tasksResult.taskInfos.add(info);
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

        Log.e("test_access", "taskResult:" + tasksResult);
        return tasksResult;
    }

    public class TasksResult {
        public int version;
        //任务集合
        public List<TaskInfo> taskInfos = new ArrayList<>();

        @Override
        public String toString() {
            return "{version:" + version + ",[" + taskInfos + "]}";
        }
    }
}
