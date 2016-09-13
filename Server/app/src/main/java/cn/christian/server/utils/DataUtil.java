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

 public float[] getValidData(float[] data, int length) {

             int startindex = 0;
             int endindex = 0;
             float delta = 0.01f;

             for (int i = 0; i < data.length - 1; i++) {
                  boolean forward = data[i + 1] - data[i] < delta;
                  boolean forback = data[data.length - i - 1] - data[data.length - i - 2] < delta;

                 if (forward && forback) {
                          continue;
                 }

                 if (startindex == 0) {
                   if (!forward) {
                        startindex = i;
                    }
                 }

                 if (endindex == 0) {
                    if (forward && !forback) {
                       endindex = data.length - 1 - i;
                    }
                 }
             }

             System.out.println("startindex  is :" + startindex);
             System.out.println("endindex  is :" + endindex);

             int datasize = endindex - startindex;
             System.out.println("data size is :" + datasize);
             if (datasize < length) {
                    System.out.println("data not enough");

                 int offset=  datasize - length;
                 startindex = startindex + offset/2;
                 endindex = endindex + offset/2;


             } else {
                    System.out.println("data is ok");
             }

             return Arrays.copyOfRange(data, startindex, endindex);

  }


}
