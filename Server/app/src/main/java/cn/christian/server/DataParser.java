package cn.christian.server;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/28.
 */
public class DataParser {

    private String tag = "+YAV";
    private float data0;
    //    private double scope=10.0;
    private float scope = 10;
    private float resolution = 4096;

    public float chanel0Voltage(String record) {

        float voltage = 0;
        if (record == null || record.isEmpty()) {
            throw new IllegalArgumentException("no record received ...");
        }
        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        iterator.next();// tag
        String data0 = iterator.next();// data0

//        Log.i("Parser","AD value in hex: "+data0);
        int value = Integer.parseInt(data0, 16);
//        Log.i("Parser","AD value in decimal : "+value);

        float coeffcient = value / resolution;
        voltage = coeffcient * scope;
        return voltage;
    }

    public float[] chanel0Voltages(String record) {

        float voltages[];
        if (record == null || record.isEmpty()) {
            throw new IllegalArgumentException("no record received ...");
        }

        Log.i("Parser", "record: " + record);


        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        String tag = iterator.next();// tag
        tag = tag.substring(5, 9);
        String dataStr = iterator.next();// data0
        int datalength = Integer.parseInt(tag, 16);
        voltages = new float[datalength];
        for (int i = 0; i < datalength; i++) {

            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            voltages[i] = (scope * (datai / resolution) / ADService.micronVoltage - ADService.sensorZerovalue * 1000) / 1000;

        }


//        Log.i("Parser","AD value in hex: "+data0);
//        int value = Integer.parseInt(data0, 16);
//        Log.i("Parser","AD value in decimal : "+value);

//        float coeffcient  = value / resolution;
//        voltages =coeffcient*scope;

        return voltages;
    }


}
