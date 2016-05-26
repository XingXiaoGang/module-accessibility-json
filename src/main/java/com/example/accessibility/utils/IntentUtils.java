package com.example.accessibility.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by xingxiaogang on 2016/5/24.
 */
public class IntentUtils {

    public static boolean startActivity(Context context, Intent intent) {
        if (existIntent(context, intent)) {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public static Intent qureyLaunchIntent(Context context, String pkName) {
        final PackageManager packageManager = context.getPackageManager();
        return packageManager.getLaunchIntentForPackage(pkName);
    }

    public static boolean startApp(Context context, String packageName) {
        Intent intent = qureyLaunchIntent(context, packageName);
        if (intent != null) {
            return startActivity(context, intent);
        }
        return false;
    }

    public static boolean existIntent(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return infos != null && !infos.isEmpty();
    }

}
