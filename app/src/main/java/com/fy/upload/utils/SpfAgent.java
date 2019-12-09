package com.fy.upload.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

/**
 * describe： SharedPreferences 代理工具类
 * Created by fangs on 2018/12/24 16:39.
 */
final public class SpfAgent {
//    创建的Preferences文件存放位置可以在Eclipse中查看：
//	  DDMS->File Explorer /<package name>/shared_prefs/setting.xml

    private volatile static SpfAgent instance;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private SpfAgent(String fileName) {
        SharedPreferences spf = TextUtils.isEmpty(fileName) ? getSpf() : getSpf(fileName);
        this.editor = spf.edit();
    }


    public static synchronized SpfAgent init(final String fileName) {
        if (null == instance) {
            synchronized (SpfAgent.class) {
                if (null == instance) {
                    instance = new SpfAgent(fileName);
                }
            }
        }

        return instance;
    }

    /**
     * 通过 application 获取 指定名称的 SharedPreferences
     * @param fileName 文件名称
     * @return         SharedPreferences
     */
    public static SharedPreferences getSpf(String fileName){
        Context ctx = ConfigUtils.getAppCtx();
        return ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSpf(){
        return getSpf("SPFDefaultName");
    }



    /**
     * 向 SharedPreferences 保存String 数据
     * @param key   保存的键
     * @param value 保存的内容
     * @return SpfAgent
     */
    public SpfAgent saveString(String key, String value){
        this.editor.putString(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 int 数据
     * @param key   保存的键
     * @param value 保存的内容
     * @return SpfAgent
     */
    public SpfAgent saveInt(String key, int value){
        this.editor.putInt(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 long 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveLong(String key, long value){
        this.editor.putLong(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存boolean 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveBoolean(String key, boolean value){
        this.editor.putBoolean(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 float 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveFloat(String key, float value){
        this.editor.putFloat(key, value);
        return this;
    }


    /**
     * 删除 指定 key 的内容
     * @param key      The key of sp.
     * @param isCommit True to use {@link SharedPreferences.Editor#commit()},
     *                 false to use {@link SharedPreferences.Editor#apply()}
     */
    public void remove(@NonNull final String key, final boolean isCommit) {
        this.editor.remove(key);
        commit(isCommit);
    }

    /**
     * 清除所有数据
     * @param isCommit True to use {@link SharedPreferences.Editor#commit()},
     *                 false to use {@link SharedPreferences.Editor#apply()}
     */
    public void clear(final boolean isCommit) {
        this.editor.clear();
        commit(isCommit);
    }

    /**
     * 提交
     * @param isCommit 是否同步提交
     */
    public void commit(final boolean isCommit){
        if (isCommit) {
            this.editor.commit();
        } else {
            this.editor.apply();
        }
    }



    /**
     * 从 SharedPreferences 取String数据
     * @param fileName 文件名称
     * @param key key
     * @return   没有对应的key 默认返回的""
     */
    public String getString(String fileName, String key){
        return getSpf(fileName).getString(key, "");
    }

    public String getString(String key){
        return getSpf().getString(key, "");
    }

    /**
     * 从 SharedPreferences 取int数据
     * @param fileName 文件名称
     * @param key key
     * @return   没有对应的key  默认返回 -1
     */
    public int getInt(String fileName, String key){
        return getSpf(fileName).getInt(key, -1);
    }

    public int getInt(String key){
        return getSpf().getInt(key, -1);
    }

    /**
     * 从 SharedPreferences 取 long 数据
     * @param fileName 文件名称
     * @param key key
     * @return   没有对应的key  默认返回 0
     */
    public long getLong(String fileName, String key){
        return getSpf(fileName).getLong(key, 0);
    }

    public long getLong(String key){
        return getSpf().getLong(key, 0);
    }

    /**
     * 从 SharedPreferences 获取 float 数据
     * @param fileName 文件名称
     * @param key       key
     * @return          没有对应的key 默认返回 0f
     */
    public float getFloat(String fileName, String key){
        return getSpf(fileName).getFloat(key, 0f);
    }

    public float getFloat(String key){
        return getSpf().getFloat(key, 0f);
    }

    /**
     * 从 SharedPreferences 获取 boolean数据
     * @param fileName 文件名称
     * @param key   key
     * @return      没有对应的key 默认返回false
     */
    public boolean getBoolean(String fileName, String key){
        return getSpf(fileName).getBoolean(key, false);
    }

    public boolean getBoolean(String key){
        return getSpf().getBoolean(key, false);
    }

    /**
     * 同上
     * @return
     */
    public boolean getBoolean(String fileName, String key, boolean def){
        return getSpf(fileName).getBoolean(key, def);
    }

    public boolean getBoolean(String key, boolean def){
        return getSpf().getBoolean(key, def);
    }

    /**
     * 获取所有键值对
     * @return
     */
    public Map<String, ?> getAll(String fileName){
        return getSpf(fileName).getAll();
    }

}
