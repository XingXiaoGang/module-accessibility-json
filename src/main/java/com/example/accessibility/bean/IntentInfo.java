package com.example.accessibility.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 * 要打开的程序的intent信息
 */
public class IntentInfo extends Base {

    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("describe")
    public String describe;

    @Expose
    @SerializedName("package")
    public String packageName;

    /**
     * 有的过程是在多个apk中完成的，涉及到多个包名
     **/
    @Expose
    @SerializedName("package1")
    public String packageName1;

    @Expose
    @SerializedName("package2")
    public String packageName2;

    @Expose
    @SerializedName("activity")
    public String activityName;

    @Expose
    @SerializedName("extra")
    public String extra;

    @Expose
    @SerializedName("data")
    public String data;

    @Expose
    @SerializedName("action")
    public String action;

}
