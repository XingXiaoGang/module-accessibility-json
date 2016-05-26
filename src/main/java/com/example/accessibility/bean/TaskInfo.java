package com.example.accessibility.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 联系rom和 每项 具体辅助操作的信息
 */
public class TaskInfo extends Base {

    @Expose
    @SerializedName("rom")
    public int romId;

    @Expose
    @SerializedName("title")
    public String title;

    @Expose
    @SerializedName("process_id")
    public int processId;

    @Expose
    @SerializedName("type")
    public int type;

    @Expose
    @SerializedName("priority")
    public int priority;
}
