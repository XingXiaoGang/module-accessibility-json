package com.example.accessibility.rom.bean;

import com.example.accessibility.bean.Base;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 * rom识别的特征
 */
public class Feature extends Base {
    @Expose
    @SerializedName("key")
    public String key;

    @Expose
    @SerializedName("value")
    public String value;

    @Expose
    @SerializedName("condition")
    public String condition;
}
