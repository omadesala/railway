package cn.christian.server;

import android.util.Log;

import cn.christian.server.utils.DataUtil;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/28.
 */
public class DistanceParser {


    private static final String TAG = DistanceParser.class.getName();

    private static float scope = 10;
    private static float resolution = 4096;
    private static float needNumber = 250;
    private static float baseVoltage = 0.0f;
    private static float basePosition = .0f;
    private int baseDataLength = 0;
    private boolean baseConfirm = false;
    private List<Float> voltageDatas = Lists.newArrayList();// 确认保留或者放弃后清空该数据

    public int getBaseDataLength() {
        return baseDataLength;
    }

    public void setBaseDataLength(int baseDataLength) {
        this.baseDataLength = baseDataLength;
    }


    public DistanceParser(int dataLength) {
        needNumber = dataLength;
    }

    public boolean isBaseConfirm() {
        return baseConfirm;
    }

    public void setBaseConfirm(boolean baseConfirm) {
        this.baseConfirm = baseConfirm;

    }


    public float[] getValidateData(String record) {

        Float[] voltage = getVoltage(record);

        if (!baseConfirm) {

            voltageDatas = Lists.newArrayList();

            Float[] baseData = new Float[baseDataLength];
            Float[] validData = new Float[voltage.length - baseDataLength];
            validData = Arrays.copyOfRange(voltage, baseDataLength, voltage.length);
            baseData = Arrays.copyOf(voltage, baseDataLength);
            baseVoltage = getAverage(baseData);
            basePosition = getDistanceFromVoltage(baseVoltage);
            Log.d(TAG, "get base position: " + basePosition);
            baseConfirm = true;
            for (int i = 0; i < voltage.length - baseDataLength; i++) {
                voltageDatas.add(validData[i]);
            }
        } else {
            for (int i = 0; i < voltage.length; i++) {
                voltageDatas.add(voltage[i]);
            }
        }

        if (voltageDatas.size() < needNumber) {

            Log.d(TAG, "data not enough");
            return null;
        }


        Log.d(TAG, "data enough return distance ");

        Float validateData[] = new Float[voltageDatas.size()];
        Float[] validVoltage = getValidVoltage(voltageDatas.toArray(validateData));
        float[] dis = getDistanceFromVoltage(validVoltage);
        return dis;
    }

    public static Float[] getValidVoltage(Float[] voltage) {

        List<Float> validVoltage = Lists.newArrayList();

        Log.d(TAG, "getValidVoltage");

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
        if (sum == 0f) {
            return 0f;
        } else {
            return sum / voltage.length;
        }

    }

    public Float getBaseVoltage(Float voltage[]) {

        Float sum = new Float(0);
        for (int i = 0; i < voltage.length; i++) {
            sum += voltage[i];
        }
        return sum / voltage.length;

    }


    public Float[] getVoltage(String record) {

        Float voltages[];
        if (record == null || record.isEmpty()) {
            throw new IllegalArgumentException("no record received  ...");
        }

        Iterable<String> result = Splitter.on(',')
                .split(record);

        Iterator<String> iterator = result.iterator();
        String tag = iterator.next();// TAG
        tag = tag.substring(5, 9);
        int datalength = Integer.parseInt(tag, 16);
        String dataStr = iterator.next();// data0
        voltages = new Float[datalength];

        for (int i = 0; i < datalength; i++) {
            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            if (datai < 0 || datai > 4096) {
                Log.e("Parser", "data  error :" + dataitem);
                datai = 0;
            }
            voltages[i] = scope * (datai / resolution);
        }

        return voltages;
    }


    public float[] getDistanceFromVoltage(Float voltage[]) {

        int datalength = voltage.length;
        float[] distancemm = new float[datalength];
        for (int i = 0; i < datalength; i++) {
            distancemm[i] = ((voltage[i] / ADSocketService.micronVoltage) / 1000.0f);
        }
        return distancemm;
    }

    public float getDistanceFromVoltage(Float voltage) {

        float distancemm = .0f;
        distancemm = (((voltage - 5) / ADSocketService.micronVoltage) / 1000.0f);
        return distancemm;
    }


}
