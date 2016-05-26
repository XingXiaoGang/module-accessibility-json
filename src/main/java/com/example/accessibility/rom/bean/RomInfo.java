package com.example.accessibility.rom.bean;

import com.example.accessibility.bean.Base;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xingxiaogang on 2016/5/23.
 * rom 信息
 */
public class RomInfo extends Base {

    @Expose
    @SerializedName("rom_id")
    public int romId;

    @Expose
    @SerializedName("rom_name")
    public String romName;

    @Expose
    @SerializedName("feature_items")
    public List<Feature> features;

}
