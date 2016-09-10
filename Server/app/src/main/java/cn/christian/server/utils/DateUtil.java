package cn.christian.server.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/9/10.
 */
public class DateUtil {


    public static String Date2Str(Timestamp t) {

        SimpleDateFormat test = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String format = test.format(t.getTime());

        return format;
    }
}
