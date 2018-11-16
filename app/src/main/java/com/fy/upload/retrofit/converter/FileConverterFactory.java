package com.fy.upload.retrofit.converter;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 上传文件 转换器工厂
 * Created by fangs on 2018/11/12.
 */
public class FileConverterFactory extends Converter.Factory {

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        //进行条件判断，如果传进来的 methodAnnotations 不包含 UpLoadFileType，则匹配失败
        for( Annotation annotation : methodAnnotations) {
            if( annotation instanceof UpLoadFileType) {
                return new FileRequestBodyConverter();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return super.responseBodyConverter(type, annotations, retrofit);
    }
}
