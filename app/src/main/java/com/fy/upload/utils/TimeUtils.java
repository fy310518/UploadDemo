package com.fy.upload.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间转换工具类
 *
 * Created by fangs on 2017/3/22.
 */
public class TimeUtils {

    private TimeUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将一个时间戳转化成时间字符串，自定义格式
     *
     * @param time
     * @param format
     *            如 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String Long2DataString(long time, String format) {
        if (time == 0) {
            return "";
        }

        Date date = new Date(time);

        return Data2String(date, format);
    }

    /**
     * 将一个时间字符串转化为 Date
     * @param timeStr
     * @param format
     * @return
     */
    public static Date string2Date(String timeStr, String format) {

        if(TextUtils.isEmpty(timeStr))return new Date(-1);

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));

            return simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date(-1);//转换出错
    }

    /**
     * 将一个时间字符串转换为时间戳，自定义格式
     * @param timeStr
     * @param format
     * @return
     */
    public static long timeString2long(String timeStr, String format){
        Date date = string2Date(timeStr, format);
        return date.getTime();
    }

    /**
     * 将一个日期对象转换成 时间字符串
     * @param date
     * @param format
     * @return
     */
    public static String Data2String(Date date, String format){

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf.format(date);
    }


    /**
     * 获得给定时间戳表示的日期 零时零分零秒零毫秒的时间戳
     * @return
     */
    public static long initDateByDay(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 计算 年龄
     * @param birthday
     * @param format
     * @return
     */
    public static int calculationAge(String birthday, String format){
        long   birthdayLong = timeString2long(birthday, format);
        if (birthdayLong == -1){
            return 0;
        }

        String currentYear  = Long2DataString(System.currentTimeMillis(), "yyyy");
        String birthdayYear = Long2DataString(birthdayLong, "yyyy");

        return Integer.parseInt(currentYear) - Integer.parseInt(birthdayYear);
    }

    /**
     * 获取指定时间戳 所在的一周的时间集合
     * @param time
     * @param isMoudel isMoudel == 0 以指定的时间戳为第一天；
     *                 isMoudel == 1 以指定的时间戳为最后一天；
     *                 isMoudel == 2 以指定的时间戳为一个礼拜其中一天；
     * @return
     */
    public static List<Date> dateToWeek(long time, int isMoudel) {
        //指定毫秒数所在的日期 零点零分零秒的毫秒数
        long zero = initDateByDay(time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(zero);

        int d = calendar.get(Calendar.DAY_OF_WEEK);
        long startTime = isMoudel == 0 ? calendar.getTimeInMillis() :
                isMoudel == 1 ? calendar.getTimeInMillis() - 7 * 24 * 3600000 : calendar.getTimeInMillis() - d * 24 * 3600000;

        Date fdate;
        List<Date> list = new ArrayList<>();
        for (int a = 0; a < 7; a++) {
            fdate = new Date();
            fdate.setTime(startTime + a * 24 * 3600000);

            list.add(fdate);
        }
        return list;
    }

    /**
     * 判断两个时间戳是否同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2){
        String date1 = TimeUtils.Long2DataString(time1, "yyyy-MM-dd");
        String date2 = TimeUtils.Long2DataString(time2, "yyyy-MM-dd");

        return date1.equals(date2);
    }

    /**
     * 计算当前时间 和 指定时间戳，的时间差
     * @return
     */
    public static String getTimeDifference(long time) {
        long timeDifference = (System.currentTimeMillis() - time);

        if (timeDifference <= 60000) {//小于一分钟
            return "刚刚";
        } else if (timeDifference < 60 * 60000) {//小于一小时
            return (timeDifference / 60000) + "分钟前";
        } else if (timeDifference < 24L * 3600000L) {//小于一天
            return (timeDifference / 3600000L) + "小时前";
        }

//        else if (timeDifference < 48L * 3600000L) {//大于一天小于两天
//            return "昨天";
//        } else if (timeDifference < 720L * 3600000L){//小于30天
//            return (timeDifference / (24L * 3600000L)) + "天前";
//        }

        else {
            return Long2DataString(time, "yyyy-MM-dd");
        }
    }

    /**
     * 根据给定时间 time 单位 秒，计算 分钟数 秒 数
     * @param time
     * @return "3'12''"
     */
    public static String getTime(int time) {
        StringBuilder sb = new StringBuilder();
        if (time > 60) {
            sb.append(time / 60)
                    .append("'");

            if (time % 60 != 0) {
                sb.append(time % 60)
                        .append("''");
            }
        } else {
            if (time % 60 != 0) {
                sb.append(time % 60)
                        .append("''");
            }
        }

        return sb.toString();
    }

    /**
     * 传入一个时间(毫秒单位),
     *
     * @param millis
     * @return 获取当前时间与传入时间,   相差的天数
     */
    public int getIntervalDada(long millis) {
        long curMillis = System.currentTimeMillis();
        long betweenTime = curMillis - millis;
        int days = (int) (betweenTime / 1000 / 60 / 60 / 24);
        return days;
    }


    /**
     * 将误差转变为d日h时m分s秒格式
     *
     * @param lTime
     * @return
     */
    public static String setLtime(long lTime) {
        long mTime = Math.abs(lTime);
        String str;
        long d = mTime / (1000 * 60 * 60 * 24);
        long h = mTime % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
        long m = mTime % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) / (1000 * 60);
        long s = mTime % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) % (1000 * 60) / 1000;
        if (d != 0) {
            if (lTime > 0) {
                str = String.format("  快%d日%d时%02d分%02d秒", d, h, m, s);
            } else {
                str = String.format("  慢%d日%d时%02d分%02d秒", d, h, m, s);
            }
        } else if (h != 0) {
            if (lTime > 0) {
                str = String.format("  快%d时%02d分%02d秒", h, m, s);
            } else {
                str = String.format("  慢%d时%02d分%02d秒", h, m, s);
            }
        } else if (h == 0 && m != 0) {
            if (lTime > 0) {
                str = String.format("  快%02d分%02d秒", m, s);
            } else {
                str = String.format("  慢%02d分%02d秒", m, s);
            }
        } else if (h == 0 && m == 0 && s != 0) {
            if (lTime > 0) {
                str = String.format("  快%02d秒", s);
            } else {
                str = String.format("  慢%02d秒", s);
            }
        } else {
            str = "  (0\")";
        }
        return str;
    }
}
