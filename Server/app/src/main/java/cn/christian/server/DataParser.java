package cn.christian.server;

import android.util.Log;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/28.
 */
public class DataParser {


    private float scope = 10;
    private float resolution = 4096;
    private float epslon = 0.01f;
    private float needNumber = 250;
    private float baseVoltage = 0.0f;


    private List<Float> voltageDatas = Lists.newArrayList();// 确认保留或者放弃后清空该数据

    private boolean dataEnd = false;

    public DataParser(int dataLength) {
        needNumber = dataLength;
    }


    public float[] getValidateData(String record) {

        Float[] voltage = getVoltage(record);
        Float dValue = getDValue(voltage);
        Float avg = getAverage(voltage);

        int size = voltageDatas.size();

        if (dValue < epslon && avg < epslon) {
            Log.d("Parser", "测量未开始，丢弃0数据");
            dataEnd = false;
            voltageDatas.clear();
            return null;
        } else if (size < needNumber) { // measuring ...
            for (int i = 0; i < voltage.length; i++) {
                voltageDatas.add(voltage[i]);
            }
            Log.d("Parser", "add valid data ok,data size: " + size);
            return null;
        } else {
            if (dataEnd) {
                Log.d("Parser", "测量已经完成，丢弃0数据");
                return null;
            }
        }


        Log.d("Parser", "get enough data");
        dataEnd = true;
        Float validateData[] = new Float[size];
        float[] dis = getDistanceFromVoltage(voltageDatas.toArray(validateData));
        return dis;
    }

    public Float getAverage(Float voltage[]) {

        Float sum = new Float(0);
        for (int i = 0; i < voltage.length; i++) {
            sum += voltage[i];
        }
        return sum / voltage.length;

    }


    public float chanel0Voltage(String record) {

        if (record == null || record.isEmpty()) {
            throw new IllegalArgumentException("no record received ...");
        }
        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        iterator.next();// DATA
        String data0 = iterator.next();// data0

        int value = Integer.parseInt(data0, 16);

        return value / resolution * scope;
    }


    public Float[] getVoltage(String record) {

        Float voltages[];
        if (record == null || record.isEmpty()) {
            throw new IllegalArgumentException("no record received ...");
        }
        Log.i("Parser", "record: " + record);

        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        String tag = iterator.next();// TAG
        tag = tag.substring(5, 9);
        String dataStr = iterator.next();// data0
        int datalength = Integer.parseInt(tag, 16);
        voltages = new Float[datalength];
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < datalength; i++) {
            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            if (datai < 0 || datai > 4096) {
                Log.e("Parser", "data error");
                datai = 0;
            }
            voltages[i] = scope * (datai / resolution);
            sb.append(voltages[i]).append(",");
        }

        Log.d("Parser", "voltage is: " + sb.toString());
        return voltages;
    }


    public float[] getDistanceFromVoltage(Float voltage[]) {

        Log.d("Parser", "get dist from voltage");
        int datalength = voltage.length;
        float[] distancemm = new float[datalength];
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < datalength; i++) {
            distancemm[i] = (float) ((voltage[i] / ADService.micronVoltage) / 1000.0 + 2);
            sb.append(distancemm[i]).append(",");
        }
        Log.d("Parser", "distancemm is: " + sb.toString());
        return distancemm;
    }

    public static Float getDValue(Float[] datas) {

        Float max = Float.MIN_VALUE;
        Float min = Float.MAX_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] > max) {
                max = datas[i];
            }
            if (datas[i] < min) {
                min = datas[i];
            }

        }
        return max - min;

    }

    public static Float getMax(Float[] datas) {
        Float max = Float.MIN_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] > max) {
                max = datas[i];
            }
        }
        return max;
    }

    public static Float getMin(Float[] datas) {
        Float min = Float.MAX_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] < min) {
                min = datas[i];
            }
        }
        return min;
    }

}
