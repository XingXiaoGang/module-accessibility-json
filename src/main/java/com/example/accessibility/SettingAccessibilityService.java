package com.example.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.example.accessibility.hanlder.TaskHandler;

/**
 * Created by xingxiaogang on 2016/5/20.
 */
public class SettingAccessibilityService extends android.accessibilityservice.AccessibilityService {

    private TaskHandler mTaskHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Statics.ACCESSIBILITY_SERVER_ACTION);
        //要用application
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplication());
        localBroadcastManager.registerReceiver(new ActionReceiver(), filter);

        mTaskHandler = new TaskHandler(this);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        setServiceInfo();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {

    }

    public void setServiceInfo() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.notificationTimeout = 100;
        info.packageNames = new String[]{getPackageName(), "com.android.settings"};
        setServiceInfo(info);
    }

    private class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            int command = intent.getIntExtra(Statics.Key.ACTION, -1);
            if (command == R.id.action_start) {
                setServiceInfo();
                if (mTaskHandler != null) {
                    mTaskHandler.start();
                }
            } else if (command == R.id.action_stop) {
                if (mTaskHandler != null) {
                    mTaskHandler.stop();
                }
            } else if (command == R.id.action_finish) {
                if (mTaskHandler != null) {
                    mTaskHandler.finish();
                }
            }
        }
    }
}
