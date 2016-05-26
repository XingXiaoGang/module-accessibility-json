package com.example.accessibility.hanlder;

import android.view.accessibility.AccessibilityEvent;

import com.example.accessibility.SettingAccessibilityService;
import com.example.accessibility.hanlder.impl.JsonTaskHandlerImpl;

/**
 * Created by xingxiaogang on 2016/5/24.
 */
public class TaskHandler implements ITaskHandler {

    private ITaskHandler mHandler;

    public TaskHandler(SettingAccessibilityService service) {
        mHandler = new JsonTaskHandlerImpl(service);
    }

    @Override
    public void start() {
        if (mHandler != null) {
            mHandler.start();
        }
    }

    @Override
    public void stop() {
        if (mHandler != null) {
            mHandler.stop();
        }
    }

    @Override
    public void finish() {
        if (mHandler != null) {
            mHandler.finish();
        }
    }

    @Override
    public boolean isRunning() {
        return mHandler != null && mHandler.isRunning();
    }

    @Override
    public boolean isPrepared() {
        return mHandler != null && mHandler.isPrepared();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (mHandler != null) {
            mHandler.onAccessibilityEvent(event);
        }
    }
}
