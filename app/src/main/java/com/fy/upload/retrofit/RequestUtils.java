package com.fy.upload.retrofit;


import com.fy.upload.retrofit.converter.FileConverterFactory;
import com.fy.upload.utils.L;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求入口
 * Created by fangs on 2018/3/13.
 */
public class RequestUtils {

    public volatile static RequestUtils instentce;

    protected Retrofit netRetrofit;

    protected CompositeDisposable mCompositeDisposable;

    private RequestUtils() {
        netRetrofit = getService();
        mCompositeDisposable = new CompositeDisposable();
    }

    public static synchronized RequestUtils getInstance() {
        if (null == instentce) {
            synchronized (RequestUtils.class) {
                if (null == instentce) {
                    instentce = new RequestUtils();
                }
            }
        }

        return instentce;
    }

    /**
     * 得到 RxJava + Retrofit 被观察者 实体类
     *
     * @param clazz 被观察者 类（ApiService.class）
     * @param <T>   被观察者 实体类（ApiService）
     * @return 封装的网络请求api
     */
    public static <T> T create(Class<T> clazz) {
        return getInstance().netRetrofit.create(clazz);
    }


    /**
     * 使用RxJava CompositeDisposable 控制请求队列
     *
     * @param d 切断订阅事件 接口
     */
    public static void addDispos(Disposable d) {
        getInstance().mCompositeDisposable.add(d);
    }

    /**
     * 使用RxJava CompositeDisposable 清理所有的网络请求
     */
    public static void clearDispos() {
        getInstance().mCompositeDisposable.clear();
    }


    private Retrofit getService(){
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new FileConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://www.wanandroid.com")
                .client(getClient())
                .build();
    }

    private OkHttpClient getClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .writeTimeout(60000, TimeUnit.MILLISECONDS)
                .addInterceptor(getHeader())
                .addNetworkInterceptor(getResponseIntercept())
                .hostnameVerifier((hostname, session) -> {
                    return true;//强行返回true 即验证成功
                })
                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

        return builder.build();
    }

    private HttpLoggingInterceptor getResponseIntercept() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.e("net 请求or响应", message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private Interceptor getHeader() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = null;

                //获取request
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "multipart/form-data;charse=UTF-8")
//                        .addHeader("Accept-Encoding", "gzip, deflate")//根据服务器要求添加（避免重复压缩乱码）
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Accept", "*/*")
                        .addHeader("Cookie", "add cookies here")
                        .addHeader("app-type", "Android")//TODO 测试微阅
                        .build();

                if (null == response) {
                    Request.Builder requestBuilder = request.newBuilder();

                    Request newRequest = requestBuilder.build();
                    response = chain.proceed(newRequest);
                }

                return response;
            }
        };
    }
}
