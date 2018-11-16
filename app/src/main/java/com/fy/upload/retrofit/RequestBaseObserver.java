package com.fy.upload.retrofit;

import com.fy.upload.utils.L;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 自定义 基础功能 Observer
 * Created by fangs on 2017/8/28.
 */
public abstract class RequestBaseObserver<V> implements Observer<V> {

    private Disposable disposed;

    public RequestBaseObserver() {}

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        this.disposed = d;
    }

    @Override
    public void onNext(V t) {
        L.e("net", "onNext()");
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        L.e("net", "onError()");
    }

    @Override
    public void onComplete() {
        L.e("net", "onComplete()");
    }


    /**
     * 上传、下载 需重写此方法，更新进度
     * @param percent 进度百分比 数
     */
    protected void onProgress(String percent){

    }

    /**
     * 请求成功 回调
     *
     * @param t 请求返回的数据
     */
    protected abstract void onSuccess(V t);

}
