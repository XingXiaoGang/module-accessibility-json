package com.example.accessibility.rom;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.accessibility.rom.bean.Feature;
import com.example.accessibility.rom.bean.RomInfo;
import com.example.accessibility.rom.parser.RomInfoParser;

import java.util.List;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class RomInfoMatcher {

    private static RomInfoMatcher mInstance;
    private RomInfo mCachedRomInfo;

    public static synchronized RomInfoMatcher getInstance() {
        if (mInstance == null) {
            mInstance = new RomInfoMatcher();
        }
        return mInstance;
    }

    private RomInfoMatcher() {

    }

    //匹配（带缓存机制）
    public RomInfo match(Context context) {

        RomInfo res = mCachedRomInfo;
        if (res != null) {
            return mCachedRomInfo;
        }

        final RomInfoParser.RomInfoResult result = parseRomInfoList(context);
        final List<RomInfo> romInfos = result.romInfos;

        if (romInfos != null && romInfos.size() > 0) {
            out:
            for (RomInfo romInfo : romInfos) {
                final List<Feature> features = romInfo.features;
                //规则都匹配成功则rom匹配成功
                if (features != null && features.size() > 0) {
                    boolean allOk;
                    boolean hasMatch = false;
                    for (Feature feature : features) {
                        hasMatch = true;
                        final String key = feature.key;
                        final String condition = feature.condition;
                        String value = getKeyValue(key);
                        allOk = matchValue(condition, value, feature.value);
                        //如果有一个不成则不成功
                        if (!allOk) {
                            continue out;
                        }
                    }
                    //匹配成功
                    if (hasMatch) {
                        res = romInfo;
                        mCachedRomInfo = res;
                        break;
                    }
                }
                break;
            }
        }
        return res;
    }

    private RomInfoParser.RomInfoResult parseRomInfoList(Context context) {
        return new RomInfoParser(context).parse();
    }

    //获取值
    private String getKeyValue(String key) {
        String res = null;
        if (key.startsWith("ro.")) {
            try {
                Class localClass = Class.forName("android.os.SystemProperties");
                try {
                    res = (String) localClass.getDeclaredMethod("get", new Class[]{String.class}).invoke(localClass.newInstance(), new Object[]{key});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException localClassNotFoundException) {
                localClassNotFoundException.printStackTrace();
                return "";
            }
        } else if ("SDK_INT".equals(key)) {
            res = String.valueOf(Build.VERSION.SDK_INT);
        } else if ("BRAND".equals(key)) {
            res = Build.BRAND;
        } else if ("DEVICE".equals(key)) {
            res = Build.DEVICE;
        } else if ("DISPLAY".equals(key)) {
            res = Build.DISPLAY;
        } else if ("ID".equals(key)) {
            res = Build.ID;
        } else if ("MANUFACTURER".equals(key)) {
            res = Build.MANUFACTURER;
        } else if ("RELEASE".equals(key)) {
            res = Build.VERSION.RELEASE;
        } else if ("PRODUCT".equals(key)) {
            res = Build.PRODUCT;
        }
        return res;
    }

    //比较值
    private boolean matchValue(String condition, String value, String valueToMatch) {
        boolean success = false;
        if (value != null && condition != null) {
            int valueInt = -1;
            int valueMatch = -1;
            try {
                valueInt = Integer.valueOf(value);
                valueMatch = Integer.valueOf(valueToMatch);
            } catch (Exception ignored) {
            }

            switch (condition) {
                case "equal": {
                    success = valueToMatch.equalsIgnoreCase(value);
                    break;
                }
                case "greater": {
                    success = valueInt != -1 && valueInt > valueMatch;
                    break;
                }
                case "less": {
                    success = valueInt != -1 && valueInt < valueMatch;
                    break;
                }
                case "ge": {
                    success = valueInt != -1 && valueInt >= valueMatch;
                    break;
                }
                case "le": {
                    success = valueInt != -1 && valueInt <= valueMatch;
                    break;
                }
                case "ne": {
                    success = valueInt != -1 && valueInt != valueMatch;
                    break;
                }
                default: {
                    throw new RuntimeException("un support condition");
                }
            }
        }
        Log.d("test_access", "matchValue:" + value + "-->" + condition + "-->" + valueToMatch + ", res:" + success);
        return success;
    }
}
