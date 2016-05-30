package com.example.accessibility.hanlder.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessibility.AccessibilityClient;
import com.example.accessibility.BuildConfig;
import com.example.accessibility.R;
import com.example.accessibility.SettingAccessibilityService;
import com.example.accessibility.Statics;
import com.example.accessibility.bean.ActionInfo;
import com.example.accessibility.bean.IntentInfo;
import com.example.accessibility.bean.ProcessInfo;
import com.example.accessibility.bean.TaskInfo;
import com.example.accessibility.bean.node.LocateNode;
import com.example.accessibility.bean.node.OperationNode;
import com.example.accessibility.bean.node.ScrollNode;
import com.example.accessibility.hanlder.BaseTaskHandler;
import com.example.accessibility.parser.ActionParser;
import com.example.accessibility.parser.IntentParser;
import com.example.accessibility.parser.ProcessParser;
import com.example.accessibility.parser.TasksParser;
import com.example.accessibility.rom.bean.RomInfo;
import com.example.accessibility.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xingxiaogang on 2016/5/23.
 */
public class JsonTaskHandlerImpl extends BaseTaskHandler {

    protected static final String TAG = "test_access";

    private List<TaskInfo> mTaskInfo;
    private Map<Integer, ProcessInfo> mProcessInfoMap;
    private Map<Integer, IntentInfo> mIntentInfoMap;
    private Map<Integer, ActionInfo> mActionInfoMap;

    private ExecuteTask mExecuteTask;
    private boolean isRunning;

    public JsonTaskHandlerImpl(SettingAccessibilityService service) {
        super(service);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void start() {
        new ParseJsonTask(getContext()).start();
    }

    @Override
    public void stop() {
        if (mExecuteTask != null) {
            mExecuteTask.interrupt = true;
        }
    }

    @Override
    public void finish() {
        if (mExecuteTask != null) {
            mExecuteTask.interrupt = true;
            isRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isPrepared() {
        boolean isOk = true;
        final List<TaskInfo> taskInfos = mTaskInfo;
        final Map<Integer, ProcessInfo> processInfosMap = mProcessInfoMap;
        final Map<Integer, IntentInfo> intentInfosMap = mIntentInfoMap;
        final Map<Integer, ActionInfo> actionInfosMap = mActionInfoMap;
        //合法性查检
        if (taskInfos != null && !taskInfos.isEmpty() && processInfosMap != null && !processInfosMap.isEmpty() && intentInfosMap != null && !intentInfosMap.isEmpty() && actionInfosMap != null && !actionInfosMap.isEmpty()) {
            for (TaskInfo taskInfo : taskInfos) {
                ProcessInfo processInfo = processInfosMap.get(taskInfo.processId);
                if (processInfo != null) {
                    //检查intent
                    if (processInfo.intentId == null || processInfo.intentId.length == 0) {
                        isOk = false;
                        if (BuildConfig.DEBUG) {
                            throw new RuntimeException("accessibility json error :ProcessInfo 里的Intent未配置");
                        }
                    } else {
                        for (int i : processInfo.intentId) {
                            final IntentInfo intentInfo = intentInfosMap.get(i);
                            if (intentInfo == null) {
                                isOk = false;
                                if (BuildConfig.DEBUG) {
                                    throw new RuntimeException("accessibility json error :Id为 " + taskInfo.processId + " 的IntentInfo未配置");
                                }
                            }
                        }
                    }
                    //检查action
                    if (processInfo.actionId == null || processInfo.actionId.length == 0) {
                        isOk = false;
                        if (BuildConfig.DEBUG) {
                            throw new RuntimeException("accessibility json error :ProcessInfo 里的Action未配置");
                        }
                    } else {
                        for (int i : processInfo.actionId) {
                            final ActionInfo actionInfo = actionInfosMap.get(i);
                            if (actionInfo == null) {
                                isOk = false;
                                if (BuildConfig.DEBUG) {
                                    throw new RuntimeException("accessibility json error :Id为 " + i + " 的ActionInfo未配置");
                                }
                            }
                        }
                    }
                } else {
                    isOk = false;
                    if (BuildConfig.DEBUG) {
                        throw new RuntimeException("accessibility json error :Id为 " + taskInfo.processId + " 的ProcessInfo未配置");
                    }
                }
            }
        } else {
            isOk = false;
            if (BuildConfig.DEBUG) {
                throw new RuntimeException("accessibility json error:json信息不完整");
            }
        }
        return isOk;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == R.id.parse_json_return) {
            Log.e(TAG, "验证json结果...");
            if (isPrepared()) {
                //加载json成功 进入执行入口
                Log.e(TAG, "开始执行辅助操作...");
                mExecuteTask = new ExecuteTask(this);
                mExecuteTask.start();
                isRunning = true;
            } else {
                //信息初始化失败
                Log.e(TAG, "中断：json配置信息初始化失败");
                sendErrorMsg(Statics.Code.ERROR_CODE_JSON_PREPARE_FAILED, "ERROR_CODE_JSON_PREPARE_FAILED:json配置信息初始化失败");
            }
        } else if (msg.what == R.id.action_error) {
            Intent intent = new Intent(Statics.ACCESSIBILITY_CLIENT_ACTION);
            intent.putExtra(Statics.Key.ACTION, R.id.action_error);
            intent.putExtra(Statics.Key.CODE, msg.arg1);
            intent.putExtra(Statics.Key.MESSAGE, String.valueOf(msg.obj));
            sendBroadcastToClient(intent);
        } else if (msg.what == R.id.action_progress_update) {
            Intent intent = new Intent(Statics.ACCESSIBILITY_CLIENT_ACTION);
            intent.putExtra(Statics.Key.ACTION, R.id.action_progress_update);
            intent.putExtra(Statics.Key.PROGRESS_ALL, msg.arg1);
            intent.putExtra(Statics.Key.PROGRESS, msg.arg2);
            intent.putExtra(Statics.Key.MESSAGE, String.valueOf(msg.obj));
            sendBroadcastToClient(intent);
        } else if (msg.what == R.id.action_finish) {
            Intent intent = new Intent(Statics.ACCESSIBILITY_CLIENT_ACTION);
            intent.putExtra(Statics.Key.ACTION, R.id.action_finish);
            intent.putExtra(Statics.Key.SUCCESS, msg.arg1 == 0);
            sendBroadcastToClient(intent);
        }
    }

    //通信
    private void sendErrorMsg(int code, String msg) {
        Message message = obtainMessage(R.id.action_error);
        message.arg1 = code;
        message.obj = msg;
        sendMessage(message);
    }

    //通信
    private void sendBroadcastToClient(Intent intent) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getService().getApplication());
        localBroadcastManager.sendBroadcast(intent);
    }

    //根据romId执行后续的json解析
    private class ParseJsonTask extends Thread {

        private Context mContext;

        public ParseJsonTask(Context context) {
            this.mContext = context;
        }

        @Override
        public void run() {
            super.run();
            //获得rom信息
            final RomInfo romInfo = AccessibilityClient.getInstance(getService().getApplication()).getRomInfo();
            if (romInfo != null) {
                int romId = romInfo.romId;
                //获得对应rom的辅助任务信息 TaskInfo
                final TasksParser.TasksResult result = new TasksParser(mContext, romId).parse();
                if (result != null) {
                    JsonTaskHandlerImpl.this.mTaskInfo = result.taskInfos;
                    int[] taskIds = new int[mTaskInfo.size()];
                    for (int i = 0; i < mTaskInfo.size(); i++) {
                        taskIds[i] = mTaskInfo.get(i).processId;
                    }
                    //获得对应辅助任务的具体执行信息 ProcessInfo
                    final ProcessParser.ProcessInfoResult result1 = new ProcessParser(mContext, taskIds).parse();
                    if (result1 != null) {
                        JsonTaskHandlerImpl.this.mProcessInfoMap = result1.processInfos;

                        //待解析的Intent,Action id集合
                        List<int[]> intentIds = new ArrayList<>();
                        List<int[]> actionIds = new ArrayList<>();

                        Set<Map.Entry<Integer, ProcessInfo>> set = mProcessInfoMap.entrySet();
                        for (Map.Entry<Integer, ProcessInfo> entry : set) {
                            final ProcessInfo info = entry.getValue();
                            intentIds.add(info.intentId);
                            actionIds.add(info.actionId);
                        }
                        //获得具体执行的具体intent 和 关键字等信息
                        final IntentParser.IntentResult result2 = new IntentParser(mContext, intentIds).parse();
                        final ActionParser.ActionResult result3 = new ActionParser(mContext, actionIds).parse();

                        if (result2 != null) {
                            JsonTaskHandlerImpl.this.mIntentInfoMap = result2.intentInfo;
                        }
                        if (result3 != null) {
                            JsonTaskHandlerImpl.this.mActionInfoMap = result3.actionInfo;
                        }
                    }
                }
            }
            sendEmptyMessage(R.id.parse_json_return);
        }
    }

    //执行核心
    private class ExecuteTask extends Thread {

        private int current;
        private boolean interrupt;
        private Handler mHandler;

        public ExecuteTask(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void run() {
            super.run();

            final List<TaskInfo> taskInfos = mTaskInfo;
            final Map<Integer, ProcessInfo> processInfosMap = mProcessInfoMap;
            final Map<Integer, IntentInfo> intentInfosMap = mIntentInfoMap;
            final Map<Integer, ActionInfo> actionInfosMap = mActionInfoMap;

            boolean hasErro = false;
            for (int i = 0; i < taskInfos.size(); i++) {
                final TaskInfo taskInfo = taskInfos.get(i);
                //通知进度
                current = i + 1;
                Message message = obtainMessage(R.id.action_progress_update, taskInfos.size(), current, taskInfo.title);
                mHandler.sendMessage(message);

                final ProcessInfo processInfo = processInfosMap.get(taskInfo.processId);
                Log.w(TAG, "开始辅助项目:" + processInfo.describe);
                //最终执行要用到的信息
                final List<IntentInfo> intentInfos = new ArrayList<>();
                for (int in : processInfo.intentId) {
                    intentInfos.add(intentInfosMap.get(in));
                }
                final List<ActionInfo> actions = new ArrayList<>();
                for (int ac : processInfo.actionId) {
                    actions.add(actionInfosMap.get(ac));
                }

                if (!startAction(intentInfos, actions)) {
                    hasErro = true;
                    //通知错误
                    sendErrorMsg(Statics.Code.ERROR_CODE_INTENT_ACTION_FAILED, "task exec failed,current:" + current + ",action:" + actions);
                    Log.w(TAG, "辅助项目执行失败：" + actions);
                }

                if (interrupt) {
                    //通知中断
                    sendErrorMsg(Statics.Code.ERROR_CODE_INTERRUPT, "taskInterrupt.");
                    Log.e(TAG, "辅助功能中断");
                    break;
                }
            }
            //通知完成
            Message message = obtainMessage(R.id.action_finish, hasErro ? 1 : 0, current);
            mHandler.sendMessage(message);
            isRunning = false;
            Log.e(TAG, "辅助操作执行完毕.... has error:" + hasErro);
        }

        /**
         * todo 有待改善一下json结构 以更好的适匹一个操作涉及多个包名的情况，即 ProcessInfo里面的 intent_id也改为数组
         **/
        private boolean startAction(final List<IntentInfo> intentInfo, final List<ActionInfo> actionInfo) {
            Log.d(TAG, "====startAction====" + intentInfo);
            //todo ActionInfo里面应该配置是否中断信息，来判断是否在执行不成功的时候执行整个任务的中断
            if (intentInfo.size() == 1) {
                final Intent intent = createIntent(intentInfo.get(0));
                if (!runAction(intent, actionInfo)) {
                    Log.e(TAG, "runAction 失败1：" + actionInfo);
                }
                if (interrupt) {
                    return false;
                }
            } else if (intentInfo.size() == actionInfo.size()) {
                for (int i = 0; i < intentInfo.size(); i++) {
                    final Intent intent = createIntent(intentInfo.get(i));
                    if (!runAction(intent, actionInfo)) {
                        Log.e(TAG, "runAction 失败2：" + actionInfo);
                    }
                }
            } else {
                throw new RuntimeException("不支持的对应关系：intent数量和action数量要满足1对n 或 n对n关系!");
            }
            Log.w(TAG, "辅助项目完成:");
            return true;
        }

        private boolean runAction(Intent intent, List<ActionInfo> actionInfos) {
            Log.w(TAG, "runAction：" + actionInfos);
            IntentUtils.startActivity(getService(), intent);
            for (int i = 0; i < actionInfos.size(); i++) {
                final ActionInfo actionInfo = actionInfos.get(i);
                //窗口等待
                if (actionInfo.needWaitWindow) {
                    Log.d(TAG, "sleep:" + actionInfo.needWaitTime);
                    try {
                        Thread.sleep(actionInfo.needWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //为了增加兼容性 强制等待300毫秒
                    Log.d(TAG, "sleep：300");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //============================查找node================================
                AccessibilityNodeInfo rootInfo = getService().getRootInActiveWindow();
                if (rootInfo == null) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rootInfo = getService().getRootInActiveWindow();
                }

                if (rootInfo != null) {
                    LocateNode locateNode = actionInfo.locateNode;
                    ScrollNode scrollNode = actionInfo.scrollNode;
                    AccessibilityNodeInfo nodeInfo = findNodeInRoot(locateNode);

                    //找不到时的 重试机制
                    int tryTimes = 5;
                    while (nodeInfo == null) {
                        if (scrollNode != null) {
                            if (!scrollNode(scrollNode)) {
                                break;
                            }
                        } else {
                            if (tryTimes == 0) {
                                break;
                            }
                            tryTimes--;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nodeInfo = findNodeInRoot(locateNode);
                        Log.d(TAG, "查找重试 time:" + tryTimes + ",res:" + (nodeInfo != null));
                        if (nodeInfo != null) {
                            break;
                        }
                    }

                    if (nodeInfo != null) {
                        //=========================执行node最终操作===============================
                        if (!performNodeAction(nodeInfo, actionInfo)) {
                            Log.e(TAG, "======执行action失败=========");
                            interrupt = true;
                            return false;
                        }
                    } else {
                        sendErrorMsg(Statics.Code.ERROR_CODE_NO_NODE, "未找到node信息：" + actionInfo.toJsonString());
                    }
                } else {
                    Log.d(TAG, "======AccessibilityNodeInfo root null=========");
                    sendErrorMsg(Statics.Code.ERROR_CODE_ROOT_NODE_NULL, "ERROR_CODE_ROOT_NODE_NULL");
                    interrupt = true;
                    return false;
                }
            }
            return true;
        }

        private boolean performNodeAction(AccessibilityNodeInfo nodeInfo, ActionInfo actionInfo) {
            boolean res = false;
            //先验证checkNode
            if (actionInfo.checkNode != null) {
                res = intelligentCheckNode(nodeInfo);
            }
            if (!res && actionInfo.operationNode != null) {
                OperationNode operationNode = actionInfo.operationNode;
                switch (operationNode.behavior) {
                    case "click": {
                        res = intelligentClickNode(nodeInfo);
                        break;
                    }
                    default: {
                        throw new RuntimeException("不支持的node behavior");
                    }
                }
            }
            return res;
        }

        //查找匹配node
        private AccessibilityNodeInfo findNodeInRoot(LocateNode locateNode) {
            AccessibilityNodeInfo nodeInfo = null;
            final String className = locateNode.className;
            final String[] keys = locateNode.findTexts;
            final String id = locateNode.idName;
            final int index = locateNode.itemIndex;
            if (keys != null) {
                for (String key : keys) {
                    nodeInfo = findNodeByText(key, index, 0);
                    if (nodeInfo != null) {
                        break;
                    }
                }
            }
            if (nodeInfo == null && id != null) {
                nodeInfo = finNode(id, className);
            }
            if (nodeInfo != null) {
                Log.d(TAG, "findNodeInRoot res:" + nodeInfo.getClassName() + "," + nodeInfo.getText());
            } else {
                Log.d(TAG, "findNodeInRoot res: null");
            }
            return nodeInfo;
        }

        private Intent createIntent(IntentInfo intentInfo) {
            Intent intent = null;
            //有可能涉及多个包名
            if (intentInfo.packageName != null) {
                intent = new Intent();
                if (intentInfo.activityName != null) {
                    intent.setComponent(new ComponentName(intentInfo.packageName, intentInfo.activityName));
                } else {
                    intent.setPackage(intentInfo.packageName);
                }
                if (intentInfo.action != null) {
                    intent.setAction(intentInfo.action);
                }
                if (intentInfo.data != null) {
                    intent.setData(Uri.parse(intentInfo.data));
                }
                if (intentInfo.extra != null) {
                    String[] extr = intentInfo.extra.split("=");
                    intent.putExtra(extr[0], extr[1]);
                }
            }
            return intent;
        }
    }
}
