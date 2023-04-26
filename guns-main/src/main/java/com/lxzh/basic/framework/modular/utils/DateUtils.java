package com.lxzh.basic.framework.modular.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Objects;

/**
 * 日期处理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月21日 下午12:53:33
 */
public class DateUtils {
    /** 年月格式(yyyyMM) */
    public final static String MONTH_PATTERN = "yyyyMM";
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN2 = "yyyy-MM-dd HH:mm";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return  返回yyyy-MM-dd格式日期
     */
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return  返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 获取date当前处于第几周
     *
     * @param date 日期
     * @return 获取date当前处于第几周
     */
    public static int weekOfWeekBasedYear(java.time.LocalDate date) {
        WeekFields weekFields = WeekFields.ISO;
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        return weekNumber;
    }

    /**
     * LocalDate转为Date
     * @param localDate
     * @return
     */
    public static Date toDate(java.time.LocalDate localDate) {
        if(null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获得指定日期是星期几
     * @param localDate
     * @return
     */
    public static String dayOfWeek(java.time.LocalDate localDate) {
        if(null == localDate) {
            return null;
        }
        Week week = DateUtil.dayOfWeekEnum(toDate(localDate));
        return week.toChinese("周");
    }

    /**
     * 计算2个时间有几个小时
     * @param startTime
     * @param endTime
     * @return
     */
    public static long betweenHours(LocalTime startTime, LocalTime endTime) {
        long minutes = startTime.until(endTime, ChronoUnit.MINUTES);
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 0, RoundingMode.HALF_UP);
        return hours.longValue();
    }

    private static final String MAX_HOUR = "24:00";
    public static final LocalTime maxTime = LocalTime.of(23, 59, 59);
    public static final LocalTime maxTime2 = LocalTime.of(23, 59);

    /**
     * 格式化时间
     * @param localTime
     * @return
     */
    public static String formatLocalTime(LocalTime localTime) {
        if (Objects.isNull(localTime)) {
            return null;
        }
        if (Objects.equals(LocalTime.MAX, localTime) || Objects.equals(maxTime, localTime) || Objects.equals(maxTime2, localTime)) {
            return MAX_HOUR;
        }
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    public static Date asDate(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)){
            return  null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        if (Objects.isNull(date)){
            return  null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 获取当前月份，格式 yyyyMM
     *
     * @return 格式化后的字符串
     */
    public static String getCurrentMonth() {
        return format(new Date(), MONTH_PATTERN);
    }

    /**
     * 格式化时间
     *
     * @param hour
     * @return
     */
    public static String formatHourTime(Integer hour) {
        if (Objects.isNull(hour)) {
            return StrUtil.EMPTY;
        }
        return String.format("%02d:00",hour);
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 24; i++) {
//            String str = String.format("%02d:00", i);
//            System.out.println("str = " + str);
            System.out.println("str0000 = " + formatHourTime(i));
        }

        org.joda.time.LocalTime localTime1 = new org.joda.time.LocalTime(0, 0, 0);
        System.out.println("localTime1 = " + localTime1);

    }
}
