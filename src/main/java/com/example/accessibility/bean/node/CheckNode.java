package com.example.accessibility.bean.node;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class CheckNode extends Node {

    @Expose
    @SerializedName("correct_status")
    public boolean correctStatus;
}
