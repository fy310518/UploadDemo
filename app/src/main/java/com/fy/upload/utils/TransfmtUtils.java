package com.fy.upload.utils;

import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 工具类
 * Created by fangs on 2018/11/12.
 */
public class TransfmtUtils {

    private TransfmtUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * double类型数字  保留一位小数(四舍五入)
     * DecimalFormat转换最简便
     *
     * @param doubleDigital
     * @return String
     */
    public static String doubleToKeepTwoDecimalPlaces(double doubleDigital) {
        DecimalFormat df = new DecimalFormat("##0.0");

        return df.format(doubleDigital);
    }

    /**
     * 获取 内置SD卡路径
     *
     * @return "/mnt/sdcard
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }
}
