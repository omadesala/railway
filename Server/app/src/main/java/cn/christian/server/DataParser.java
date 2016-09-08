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
    private float minValue = 0;
    private float maxValue = 0;
    private float deltaValue = 0;
    private float epslon = 1e-2;
    private int dataNumber = 0;
    private List<Float> voltageDatas= Lists.newArrayList() ;



    public boolean validate(String record){

         float voltage[]= getVoltage(record);
         int validIndex = -1;
         for(int i=0;i< voltage.length;i++){
             if( voltage[i+1]-voltage[i]>epslon){
                 continue;
             }else{
                 validIndex = i;
                 voltageDatas.add(voltage[i+1])
             }
         }

    }



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

        float distancemm[];
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
        distancemm = new float[datalength];
        for (int i = 0; i < datalength; i++) {

            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            if(datai<0 || datai>4096){
                Log.e("Parser","data error");
                datai=0;
            }

            float voltage =scope * (datai / resolution) 
            //distancemm[i] = (voltage / ADService.micronVoltage - ADService.sensorZerovalue * 1000) / 1000;
            distancemm[i] = (voltage / ADService.micronVoltage)*1000+2;
            if(distancemm[i]<minValue){
                minValue=distancemm[i];
            }
            if(distancemm[i]>maxValue){
                maxValue=distancemm[i];
            }

       }
      deltaValue=minValue-2;
      if(deltaValue>0){
            Log.i("Parser","the basis is: "+deltaValue+"mm. this value shoule be (0,0.5)");
      }else{
            Log.i("Parser","the sensor position not correct, it's too close to target");
      }

        return distancemm;
    }
    
    public float[] getDistance(String record) {

        float distancemm[];
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
        distancemm = new float[datalength];
        for (int i = 0; i < datalength; i++) {

            String dataitem = dataStr.substring(3 * i, 3 * i + 3);
            int datai = Integer.parseInt(dataitem, 16);
            if(datai<0 || datai>4096){
                Log.e("Parser","data error");
                datai=0;
            }

            float voltage =scope * (datai / resolution) 
            distancemm[i] = (voltage / ADService.micronVoltage)*1000+2;
            
       }
            return distancemm;
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
                                                                                                                                                                                                
      public static float getMin(float[] datas) {                                                                                                
          float min = Float.MAX_VALUE;                                                                                                                                                     
         for (int i = 0; i < datas.length; i++) { 
               if (datas[i] < min) {
                    min = datas[i];                                                                           
               }                                                                                                                                                                            
        }                                                                                                                                                                                
        return min;                                                                                                                                                                      
     }                                                                                                                                                                                    

}
