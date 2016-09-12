package cn.christian.server.utils;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/9/12.
 */
public class DataUtil {

    public static float getMin(float[] datas) {

        float min = Float.MAX_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] < min) {
                min = datas[i];
            }
        }
        return min;
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

    public static Float getMax(Float[] datas) {
        Float max = Float.MIN_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] > max) {
                max = datas[i];
            }
        }
        return max;
    }

    public static float getMax(float[] datas) {
        float max = Float.MIN_VALUE;
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] > max) {
                max = datas[i];
            }
        }
        return max;
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

    public static Float getMidNumber(Float[] datas) {
        Arrays.sort(datas);   // 数组从小到大排序
        return datas[datas.length / 2]; // 找出排序后中间的数组值
    }
}
