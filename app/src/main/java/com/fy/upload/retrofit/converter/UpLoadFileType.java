package com.fy.upload.retrofit.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 定义一个方法注解 UpLoadFileType，用于上传文件时候 retrofit 匹配对应的 converter
 * Created by fangs on 2018/11/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpLoadFileType {
}
