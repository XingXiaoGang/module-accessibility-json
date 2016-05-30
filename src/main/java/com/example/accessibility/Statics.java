package com.example.accessibility;

/**
 * Created by xingxiaogang on 2016/5/20.
 */
public class Statics {
    public static final String ACCESSIBILITY_SERVER_ACTION = "com.gang.accessibility.server.AccessibilityService";
    public static final String ACCESSIBILITY_CLIENT_ACTION = "com.gang.accessibility.client.AccessibilityService";

    public static final String ACTION_INFO_JSON_PATH = "permission/action_info.json";
    public static final String INTENT_INFO_JSON_PATH = "permission/intent_info.json";
    public static final String PROCESS_INFO_JSON_PATH = "permission/process_info.json";
    public static final String TASK_INFO_JSON_PATH = "permission/tasks_info.json";


    public static class Key {
        public static final String ACTION = "command";
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String PROGRESS_ALL = "progress_all";
        public static final String PROGRESS = "progress";
        public static final String SUCCESS = "success";
    }

    public static class Code {
        public static final int ERROR_CODE_NO_PERMISSION = 100;//没有辅助权限
        public static final int ERROR_CODE_JSON_PREPARE_FAILED = 101;//需要的json信息不完整
        public static final int ERROR_CODE_INTERRUPT = 102;//任务中断

        public static final int ERROR_CODE_ROOT_NODE_NULL = 111;//辅助功能未开启导致
        public static final int ERROR_CODE_NO_NODE = 112;//node查找失败

        public static final int ERROR_CODE_INTENT_ACTION_FAILED = 120;//执行Action失败
        public static final int ERROR_CODE_INTENT_OPEN_FAILED = 121;//包名不存在
    }
}
