package com.wiblog.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author pwm
 * @date 2019/10/24
 */
public class DateUtil {

    /**
     * 直接使用SimpleDateFormat有线程安全问题
     * 使用ThreadLocal, 将共享变量变为独享
     */
    private static final ThreadLocal<DateFormat> SDF_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00"));

    /**
     * 直接使用SimpleDateFormat有线程安全问题
     * 使用ThreadLocal, 将共享变量变为独享
     */
    private static final ThreadLocal<DateFormat> SIMPLE_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 转成utc格式
     *
     * @param date date
     * @return String
     */
    public static String formatDate(Date date) {
        return SDF_THREAD_LOCAL.get().format(date);
    }

    /**
     * 转成格式
     *
     * @param date date
     * @return String
     */
    public static String formatSimpleDate(Date date) {
        return SIMPLE_THREAD_LOCAL.get().format(date);
    }
}
