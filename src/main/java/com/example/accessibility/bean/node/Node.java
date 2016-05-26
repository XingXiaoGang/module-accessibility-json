package com.example.accessibility.bean.node;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public abstract class Node {

    //className
    @Expose
    @SerializedName("class_name")
    public String className;

    //要匹配的文本
    @Expose
    @SerializedName("find_texts")
    public String[] findTexts;

    //要匹配的id
    @Expose
    @SerializedName("id_name")
    public String idName;

    //className
    @Expose
    @SerializedName("item_index")
    public int itemIndex;
}
