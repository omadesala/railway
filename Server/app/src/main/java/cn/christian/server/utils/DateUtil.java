package cn.christian.server.utils;

import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/10.
 */
public class DateUtil {

    private DateUtil() {

    }


    public static String Date2Str(Timestamp t) {

        SimpleDateFormat test = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String format = test.format(t.getTime());

        return format;
    }

    //获取当前日期
    private void getDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        int month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        int day = cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Date plusDays(Date date, int days) {

        Date newDate = new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
        return newDate;
    }
}
