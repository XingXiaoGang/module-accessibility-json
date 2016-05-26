package com.example.accessibility.hanlder;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public interface ITaskHandler extends IAccessibilityService {

    void start();

    void stop();

    void finish();

    boolean isRunning();

    //是否已经初始化json
    boolean isPrepared();
}
