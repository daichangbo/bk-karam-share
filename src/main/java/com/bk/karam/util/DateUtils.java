package com.bk.karam.util;

import com.bk.karam.constant.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


/**
 * @author daichangbo
 * @date 2019-10-29 20:40
 */
@Slf4j
public class DateUtils implements Serializable {

    private static final long serialVersionUID = 4921818892578066954L;

    /**
     * 字符串转日期
     * @param param
     * @param format
     * @return
     * @throws Exception
     */
    public static Date parseDate (String param ,String format) throws Exception {
        Optional.of ( param );
        return new SimpleDateFormat ( StringUtils.isEmpty ( format ) ? BaseConstant.DEFAULT :format ).parse ( param );
    }

    /**
     * 日期转字符串
     * @param param
     * @param format
     * @return
     * @throws Exception
     */
    public static String dateStr (Date param,String format) throws Exception {
        Optional.of ( param );
        return new SimpleDateFormat ( StringUtils.isEmpty ( format ) ? BaseConstant.DEFAULT :format ).format ( param );
    }

    /**
     * 比较日期时间大小
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate (String date1,String date2) {
        DateFormat df = new SimpleDateFormat ( BaseConstant.DEFAULT );
        try {
           Date dt1 = df.parse ( date1 );
           Date dt2 = df.parse ( date2 );
           if (dt1.getTime () > dt2.getTime ()) {
               return 1;
           } else if (dt1.getTime () < dt2.getTime ()) {
               return -1;
           } else {
               return 0;
           }
        } catch (Exception e) {
            log.error("compareDate error", e);
        }
        return 0;
    }

    /**
     * 比较日期时间大小
     * @param date1
     * @param date2
     * @return
     * @description
     */
    public static int compareDate(Date date1, Date date2) {
        try {
            if (date1.getTime() > date2.getTime()) {
                return 1;
            } else if (date1.getTime() < date2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            log.error("error", e);
        }
        return 0;
    }

    /**
     * 增加分钟
     * @param date
     * @param secondNum
     * @return
     */
    public static Date addSeconds (Date date ,Long secondNum) {
        Calendar calendar = Calendar.getInstance ();
        calendar.setTime ( date );
        Long currentMills = calendar.getTimeInMillis () ;
        Long targetMills = currentMills + (secondNum * 1000 ) ;
        calendar.setTimeInMillis ( targetMills );
        return calendar.getTime () ;
    }

    /**
     * 传入日期基础上增加/减少月数  @warn  暂时不支持跨年
     * @param date 传入日期
     * @param monthNum 增加/减少月数
     * @return
     */
    public static Date addMonths(Date date,int monthNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentMonth = calendar.get(Calendar.MONTH);
        int newMonth = currentMonth + monthNum;
        if(newMonth>=0&&newMonth<=11){
            calendar.set(Calendar.MONTH, newMonth);
        }
        return calendar.getTime();
    }

    /**
     * 计算当前时间离当天还有多少秒
     * @return
     */
    public static int calcTodayEndSecond () {
        Calendar todayEnd = Calendar.getInstance ();
        todayEnd.set ( Calendar.HOUR_OF_DAY, 23 );
        todayEnd.set ( Calendar.MINUTE, 59 );
        todayEnd.set ( Calendar.SECOND, 59 );
        return (int) ((todayEnd.getTime ().getTime () - (new Date().getTime ())) / 1000);
    }

    /**
     * 获取当月的开始时间
     * @param date
     * @return
     */
    public static Date getEndOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取当天结束时间
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当天开始时间
     * @param date
     * @return
     */
    public static Date getStartOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static void main ( String[] args ) {
        System.out.println (new Date());
        System.out.println (addSeconds(new Date(),100L));
    }

}
