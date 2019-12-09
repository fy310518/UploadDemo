package com.fy.upload.retrofit.down;

/**
 * DESCRIPTION：文件下载 监听接口
 * Created by fangs on 2019/10/16 17:56.
 */
public interface DownLoadListener<T> {

    /**
     * 请求成功 回调
     */
    void onSuccess(T t);

    /**
     * 下载失败
     */
    void onFail();

    /**
     * 上传、下载 需重写此方法，更新进度
     * @param percent 进度百分比 数
     */
    void onProgress(String percent);
}
