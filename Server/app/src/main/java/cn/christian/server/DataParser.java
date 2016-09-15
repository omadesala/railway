package cn.christian.server;

import android.util.Log;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.christian.server.utils.Constants;
import cn.christian.server.utils.DataUtil;

/**
 * Created by Administrator on 2016/8/28.
 */
public class DataParser {


    private static float scope = 10;
    private static float resolution = 4096;
    private static float epslon = 0.01f;
    private static float needNumber = 250;
    private static float baseVoltage = 0.0f;
    private static float basePosition = .0f;

    private boolean baseConfirm = false;


    private List<Float> voltageDatas = Lists.newArrayList();// 确认保留或者放弃后清空该数据

    private boolean dataEnd = false;

    public DataParser(int dataLength) {
        needNumber = dataLength;
    }

    public boolean isBaseConfirm() {
        return baseConfirm;
    }

    public void setBaseConfirm(boolean baseConfirm) {
        this.baseConfirm = baseConfirm;
    }


    public float getSensorBasePosition() {
        basePosition = getDistanceFromVoltage(baseVoltage);
        return basePosition;
    }

    public float[] getValidateData(String record) {

        Float[] voltage = getVoltage(record);
        Float dValue = DataUtil.getDValue(voltage);
        Float avg = getAverage(voltage);
        Log.d("Parser", "avg: " + avg + "  d-value: " + dValue);


        int size = voltageDatas.size();
        baseVoltage = avg;
        if (dValue < 2 * epslon && avg < epslon) {// 数据平均值等于0，数据无波动
            Log.d("Parser", "传感器未上电工作，丢弃0数据");
            dataEnd = false;
            voltageDatas.clear();
            return null;
        }

        if (dValue < 5 * epslon && avg > 10 * epslon) { // 数据平均值大于0，数据无波动

            Log.d("Parser", "传感器未移动，丢弃数据 ");
            dataEnd = false;
            voltageDatas.clear();
            return null;
        }

        // 波动数据为移动传感器的测量数据
        if (size < needNumber) { // measuring ...
            for (int i = 0; i < voltage.length; i++) {
                voltageDatas.add(voltage[i]);
            }
            Log.d("Parser", "add valid data ok,data size: " + size);
            return null;
        } else {
            if (dataEnd) {
                Log.d("Parser", "测量已经完成，丢弃数据");
                return null;
            }
        }


        Log.d("Parser", "测量已经完成");
        dataEnd = true;
        Float validateData[] = new Float[size];
        Float[] validVoltage = getValidVoltage(voltageDatas.toArray(validateData));
        float[] dis = getDistanceFromVoltage(validVoltage);
        return dis;
    }

    public static Float[] getValidVoltage(Float[] voltage) {

        List<Float> validVoltage = Lists.newArrayList();
        for (int i = 0; i < voltage.length; i++) {
            Float volt = (voltage[i] - baseVoltage);
            validVoltage.add(volt);
        }
        int dataLen = validVoltage.size();
        Float ret[] = new Float[dataLen];
        return validVoltage.toArray(ret);
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
//        Log.i("Parser", "record: " + record);

        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        String tag = iterator.next();// TAG
        tag = tag.substring(5, 9);
        String dataStr = iterator.next();// data0
        int datalength = Integer.parseInt(tag, 16);
        voltages = new Float[datalength];

        for (int i = 0; i < datalength; i++) {
            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            if (datai < 0 || datai > 4096) {
                Log.e("Parser", "data error :" + dataitem);
                datai = 0;
            }
            voltages[i] = scope * (datai / resolution);
//            sb.append(voltages[i]).append(",");
        }

//        Log.d("Parser", "voltage is: " + sb.toString());
        return voltages;
    }


    public float[] getDistanceFromVoltage(Float voltage[]) {

        int datalength = voltage.length;
        float[] distancemm = new float[datalength];
        for (int i = 0; i < datalength; i++) {
            distancemm[i] = ((voltage[i] / ADService.micronVoltage) / 1000.0f);
        }
        return distancemm;
    }

    public float getDistanceFromVoltage(Float voltage) {

//        Log.d("Parser", "get dist from voltage");
        float distancemm = .0f;
        distancemm = (((voltage - 5) / ADService.micronVoltage) / 1000.0f);
        return distancemm;
    }


}
