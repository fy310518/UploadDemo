package com.fy.upload.retrofit;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 文件上传，下载 进度观察者 发射器（计算上传百分比）
 * Created by fangs on 2018/5/21.
 */
public class LoadOnSubscribe implements ObservableOnSubscribe<Object> {

    private ObservableEmitter<Object> mObservableEmitter;//进度观察者 发射器
    public long mSumLength = 0L;//总长度
    public AtomicLong uploaded = new AtomicLong();//已经上传 长度

    private double mPercent = 0;//已经上传进度 百分比

    public LoadOnSubscribe() {
    }

    @Override
    public void subscribe(ObservableEmitter<Object> e) throws Exception {
        this.mObservableEmitter = e;
    }

    public void setmSumLength(long mSumLength) {
        this.mSumLength = mSumLength;
    }

    public void onRead(long read) {
        uploaded.addAndGet(read);

        if (mSumLength <= 0) {
            onProgress(0);
        } else {
            onProgress(100d * uploaded.get() / mSumLength);
        }
    }

    private void onProgress(double percent) {
        if (null == mObservableEmitter) return;
        if (percent == mPercent) return;

        mPercent = percent;
        mObservableEmitter.onNext(percent);
    }


    //上传完成 清理进度数据
    public void clean() {
        this.mPercent = 0;
        this.uploaded = new AtomicLong();
        this.mSumLength = 0L;

        mObservableEmitter.onComplete();
    }
}
