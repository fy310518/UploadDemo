package com.fy.upload.retrofit.up;

import com.fy.upload.retrofit.RequestBaseObserver;
import com.fy.upload.utils.TransfmtUtils;

/**
 * 自定义文件上传、下载 观察者 (增强 RequestBaseObserver)
 * Created by fangs on 2018/11/12.
 */
public abstract class LoadCallBack<T> extends RequestBaseObserver<T> {

    public LoadCallBack() {
    }

    @Override
    public void onNext(T t) {
        if (t instanceof Double) {
            String percent = TransfmtUtils.doubleToKeepTwoDecimalPlaces(((Double) t).doubleValue());
            onProgress(percent);
        } else {
            super.onNext(t);
        }
    }
}
