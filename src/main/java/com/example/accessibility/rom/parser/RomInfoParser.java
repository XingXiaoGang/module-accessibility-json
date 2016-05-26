package com.example.accessibility.rom.parser;

import android.content.Context;

import com.example.accessibility.parser.JsonParser;
import com.example.accessibility.rom.bean.RomInfo;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class RomInfoParser extends JsonParser<RomInfoParser.RomInfoResult> {

    private static final String PHONE_INFO_JSON_URL = "permission/rom_info_data.json";

    public RomInfoParser(Context context) {
        super(context);
    }

    @Override
    protected InputStream decodeJsonStream() {
        try {
            return mContext.getAssets().open(PHONE_INFO_JSON_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected RomInfoParser.RomInfoResult parse(InputStream jsonStream) {
        RomInfoParser.RomInfoResult romInfoResult = new RomInfoResult();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(jsonStream));
            reader.beginObject();
            while (reader.hasNext()) {
                String tag = reader.nextName();
                switch (tag) {
                    case "version": {
                        romInfoResult.version = reader.nextInt();
                        break;
                    }
                    case "rom_items": {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            RomInfo info = GSON.fromJson(reader, RomInfo.class);
                            romInfoResult.romInfos.add(info);
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
                if (reader != null) {
                    reader.close();
                }
                if (jsonStream != null) {
                    jsonStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return romInfoResult;
    }

    public class RomInfoResult {
        public List<RomInfo> romInfos = new ArrayList<>();
        public int version;
    }
}
