package com.fy.upload.retrofit;

import android.util.ArrayMap;

import com.fy.upload.retrofit.converter.UpLoadFileType;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 通用的 上传文件 下载文件 api
 * Created by fangs on 2018/11/12.
 */
public interface LoadService {

    /**
     * 多图片上传 方式一 （参数注解：@Body；参数类型：MultipartBody）
     * @param params
     * @return
     */
    @UpLoadFileType
    @POST("http://192.168.100.123:8080/hfs/")
    Observable<Object> uploadFile(@Body ArrayMap<String, Object> params);

    /**
     * 多图片上传 方式二（@Multipart：方法注解；@Part：参数注解；参数类型；MultipartBody.Part）
     * @return
     */
    @Multipart
    @POST("http://192.168.100.123:8080/hfs/")
    Observable<Object> uploadFile2(@Part List<MultipartBody.Part> files);


    /**
     * 断点下载
     *
     * @param downParam 下载参数，传下载区间使用 "bytes=" + startPos + "-"
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String downParam, @Url String url);

}
