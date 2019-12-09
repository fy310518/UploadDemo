package com.fy.upload.utils;

import android.content.Context;

/**
 * 应用框架基础配置工具类 （应用启动时候初始化）
 * Created by fangs on 2018/7/13.
 */
public class ConfigUtils {

    /**
     * 下载状态 key
     * 1：正在下载；2：暂停；3：取消下载；4：下载完成 or 已下载
     */
    public static final String FileDownStatus = "FileDownStatus";

    public static Context ctx;

    public static Context getAppCtx() {
        return ctx;
    }
}
