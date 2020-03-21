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
    private static ThreadLocal<DateFormat> sdfThreadLocal =  new ThreadLocal<DateFormat>(){
        @Override
        public SimpleDateFormat initialValue(){
            return  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
        }
    };

    /**
     * 转成utc格式
     * @param date date
     * @return String
     */
    public static String formatDate(Date date){
        return sdfThreadLocal.get().format(date);
    }
}
