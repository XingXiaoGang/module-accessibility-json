package com.example.accessibility.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 执行一个辅助过程的总信息
 */
public class ProcessInfo extends Base {

    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("describe")
    public String describe;

    @Expose
    @SerializedName("intent_id")
    public int intentId;

    @Expose
    @SerializedName("action_id")
    public int[] actionId;

}
