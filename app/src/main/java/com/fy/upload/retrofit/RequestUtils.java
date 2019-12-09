package com.fy.upload.retrofit;


import android.os.Handler;
import android.os.Looper;

import com.fy.upload.retrofit.converter.FileConverterFactory;
import com.fy.upload.retrofit.converter.FileResponseBodyConverter;
import com.fy.upload.retrofit.down.DownLoadListener;
import com.fy.upload.retrofit.up.LoadCallBack;
import com.fy.upload.utils.ConfigUtils;
import com.fy.upload.utils.FileUtils;
import com.fy.upload.utils.L;
import com.fy.upload.utils.SpfAgent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
//                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://www.wanandroid.com")
                .client(getClient())
                .build();
    }

    private OkHttpClient getClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .writeTimeout(60000, TimeUnit.MILLISECONDS)
//                .addInterceptor(getHeader())
                .addNetworkInterceptor(getResponseIntercept());
//                .hostnameVerifier((hostname, session) -> {
//                    return true;//强行返回true 即验证成功
//                })
//                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

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



    /**
     * 文件下载
     * @param url
     * @param loadListener
     */
    public static void downLoadFile(String url, DownLoadListener<File> loadListener){
        final String filePath = FileUtils.folderIsExists("wan.down", 2).getPath();
        final File tempFile = FileUtils.getTempFile(url, filePath);

        LoadOnSubscribe loadOnSubscribe = new LoadOnSubscribe();

        Observable<File> downloadObservable = Observable.just(url)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String downUrl) throws Exception {

                        File targetFile = FileUtils.getFile(downUrl, filePath);
                        if (targetFile.exists()) {
                            SpfAgent.init("").saveInt(tempFile.getName() + ConfigUtils.FileDownStatus, 4).commit(false);//下载完成
                            return "文件已下载";
                        } else {
                            return "bytes=" + tempFile.length() + "-";
                        }
                    }
                })
                .flatMap(new Function<String, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(String downParam) throws Exception {
                        L.e("fy_file_FileDownInterceptor", "文件下载开始---" + Thread.currentThread().getName());
                        if (downParam.startsWith("bytes=")) {
                            return RequestUtils.create(LoadService.class).download(downParam, url);
                        } else {
                            SpfAgent.init("").saveInt(tempFile.getName() + ConfigUtils.FileDownStatus, 4).commit(false);
                            return null;
                        }
                    }
                })
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        try {
                            //使用反射获得我们自定义的response
//                            Class aClass = responseBody.getClass();
//                            Field field = aClass.getDeclaredField("delegate");
//                            field.setAccessible(true);
//                            ResponseBody body = (ResponseBody) field.get(responseBody);
//                            if (body instanceof FileResponseBody) {
//                                FileResponseBody prBody = ((FileResponseBody) body);
                            L.e("fy_file_FileDownInterceptor", "文件下载 响应返回---" + Thread.currentThread().getName());
//                                return FileResponseBodyConverter.saveFile(loadOnSubscribe, prBody, prBody.getDownUrl(), filePath);
                            return FileResponseBodyConverter.saveFile(loadOnSubscribe, responseBody, url, filePath);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                });


        Observable.merge(Observable.create(loadOnSubscribe), downloadObservable)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadCallBack<Object>() {
                    @Override
                    protected void onProgress(String percent) {
                        loadListener.onProgress(percent);
                    }

                    @Override
                    protected void onSuccess(Object file) {
                        if (file instanceof File) {
                            loadListener.onProgress("100");

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {
                                loadListener.onSuccess((File) file);//已在主线程中，可以更新UI
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        int FileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + ConfigUtils.FileDownStatus);
                        if (FileDownStatus == 4) {
                            File targetFile = FileUtils.getFile(url, filePath);
                            loadListener.onProgress("100");
                            loadListener.onSuccess(targetFile);
                        } else {
//                            super.onError(e);
                            SpfAgent.init("").saveInt(tempFile.getName() + ConfigUtils.FileDownStatus, 3).commit(false);
                            loadListener.onFail();
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();

                        int fileDownStatus = SpfAgent.init("").getInt(tempFile.getName() + ConfigUtils.FileDownStatus);
                        if (fileDownStatus != 4){
                            SpfAgent.init("")
                                    .saveInt(tempFile.getName() + ConfigUtils.FileDownStatus, 3)
                                    .commit(false);
                        }
                    }
                });
    }
}
