package com.example.accessibility.bean;

import com.example.accessibility.bean.node.CheckNode;
import com.example.accessibility.bean.node.IdentifyNode;
import com.example.accessibility.bean.node.LocateNode;
import com.example.accessibility.bean.node.OperationNode;
import com.example.accessibility.bean.node.ScrollNode;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 执行时的具体操作
 */
public class ActionInfo extends Base {

    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("describe")
    public String describe;

    @Expose
    @SerializedName("need_wait_window")
    public boolean needWaitWindow;

    @Expose
    @SerializedName("need_wait_time")
    public long needWaitTime;

    /**
     * 用于定位的
     **/
    @Expose
    @SerializedName("locate_node")
    public LocateNode locateNode;

    @Expose
    @SerializedName("scroll_node")
    public ScrollNode scrollNode;

    /**
     * 用于操作的
     **/
    @Expose
    @SerializedName("check_node")
    public CheckNode checkNode;

    @Expose
    @SerializedName("operation_node")
    public OperationNode operationNode;

    @Expose
    @SerializedName("identify_node")
    public IdentifyNode identifyNode;
}
