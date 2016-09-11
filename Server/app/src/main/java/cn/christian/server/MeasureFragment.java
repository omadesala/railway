package cn.christian.server;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.security.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.christian.server.application.RailWayApp;
import cn.christian.server.dao.Record;
import cn.christian.server.utils.Constants;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MeasureFragment extends Fragment {

    public final static int MSG_DATA_RECEIVED = 0;
    private static LineChart mChart;
    private static boolean hide = false;
    private static float minScope;
    private static float maxScope;
    private static float sensorScopeValue;
    private static float maxPoint;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View measureLayout = inflater.inflate(R.layout.fragment_measure, container, false);

        mChart = (LineChart) measureLayout.findViewById(R.id.chart);
        mChart.setDescription("瑞威科技");
        mChart.setNoDataTextDescription("暂时尚无数据");
        mChart.setTouchEnabled(true);

        // 可拖曳
        mChart.setDragEnabled(true);

        // 可缩放
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        // 设置图表的背景颜色
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.WHITE);

        // 先增加一个空的数据，随后往里面动态添加
        mChart.setData(data);


        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mChart.getLegend();

//        String[] labels = {"最大值100"};
//        int[] colors = {Color.RED};
//        l.setCustom(colors, labels);

        // 可以修改图表注解部分的位置
//        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);


        // 线性，也可是圆
        l.setForm(Legend.LegendForm.LINE);

        // 颜色
//        l.setTextColor(Color.WHITE);
        l.setTextColor(Color.BLUE);

        // x坐标轴
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        // 几个x坐标轴之间才绘制？
        xl.setSpaceBetweenLabels(5);

        // 如果false，那么x坐标轴将不可见
        xl.setEnabled(true);

        // 将X坐标轴放置在底部，默认是在顶部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 图表左边的y坐标轴线
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);

        // 设置x轴的LimitLine
//        LimitLine yLimitLine = new LimitLine(2.87f, "警告线");
//        yLimitLine.setLineColor(Color.RED);
//        yLimitLine.setTextColor(Color.RED);
//        leftAxis.addLimitLine(yLimitLine);

        YAxisValueFormatter format = new MyYAxisValueFormatter();
        leftAxis.setValueFormatter(format);

        if (minScope != 0 || maxScope != 0) {
            // 最大值
            leftAxis.setAxisMaxValue(maxScope);
            // 最小值
            leftAxis.setAxisMinValue(minScope);

        }

        // 不一定要从0开始
        leftAxis.setStartAtZero(false);

        leftAxis.setDrawGridLines(true);

//        leftAxis.setGridLineWidth(5f);

        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);


        return measureLayout;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataReceiver actionReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SENSOR_DATA_COMMING);
        filter.addAction(Constants.SENSOR_BASE_POSITION_NOTCORRUCET);
        getActivity().registerReceiver(actionReceiver, filter);

        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            minScope = setting.getFloat(Constants.minScope, 0);
            maxScope = setting.getFloat(Constants.maxScope, 0);
            sensorScopeValue = setting.getFloat(Constants.sensorScope, 0);
        }
    }


    // 添加进去一个坐标点
    private static void addEntry(double voltage, boolean hide) {


        LineData data = mChart.getData();

        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        LineDataSet set = data.getDataSetByIndex(0);

        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
        if (set == null) {
            set = createLineDataSet();
            data.addDataSet(set);
        }

        // 先添加一个x坐标轴的值
        // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1

        data.addXValue((data.getXValCount()) + "");

        // 生成随机测试数
//        float f = (float) ((Math.random()) * 20 + 50);

        float f = (float) voltage;
        // set.getEntryCount()获得的是所有统计图表上的数据点总量，
        // 如从0开始一样的数组下标，那么不必多次一举的加1
        Entry entry = new Entry(f, set.getEntryCount());

        // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。
        // 因为本例只有一个统计折线，那么就是第一个，其下标为0.
        // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。
        data.addEntry(entry, 0);

        // 像ListView那样的通知数据更新
        mChart.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        mChart.setVisibleXRangeMaximum(20);

        Legend l = mChart.getLegend();

        String[] labels = {"最大值 " + maxPoint};
        int[] colors = {Color.BLUE};
        l.setCustom(colors, labels);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.RED);


        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        if (!hide) {
            mChart.moveViewToX(data.getXValCount() - 5);
        }

//        mChart.moveViewToX(data.getXValCount() - 5);
        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    // 添加进去一个坐标点
    private static void addEntrys(float[] distance, boolean hide) {


        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

//        LineData data = mChart.getData();
//        if (data != null) {
//            data.removeDataSet(0);
//            mChart.notifyDataSetChanged();
//            mChart.invalidate();
//        }

        LineDataSet set = createLineDataSet();
        data.addDataSet(set);

//        LineDataSet set = data.getDataSetByIndex(0);
//        if (set == null) {
//            set = createLineDataSet();
//            data.addDataSet(set);
//        }

        int entrynum = set.getEntryCount();
        int xValueCount = data.getXValCount();
        Log.d("MEASURE", "before remove getXValCount: " + xValueCount);
        Log.d("MEASURE", "before remove data count: " + entrynum);
//        for (int i = 0; i < xValueCount; i++) {
//            data.removeXValue(i);
////            set.removeLast();
//        }

        entrynum = set.getEntryCount();

        Log.d("MEASURE", "after remove getXValCount: " + data.getXValCount());
        Log.d("MEASURE", "after remove data count: " + entrynum);

        mChart.notifyDataSetChanged();
//        mChart.invalidate();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < distance.length; i++) {
            sb.append(distance[i]).append(",");
            Entry entry = new Entry(distance[i], set.getEntryCount());
//            if (xValueCount == 0) {
//                data.addXValue(i + "");
//            }
            data.addXValue(i + "");
            data.addEntry(entry, 0);
        }

        xValueCount = data.getXValCount();
        entrynum = set.getEntryCount();
        Log.d("MEASURE", "current getXValCount: " + xValueCount);
        Log.d("MEASURE", "current data count: " + entrynum);

        Log.d("MEASURE", "data: " + sb.toString());


        // 像ListView那样的通知数据更新
        mChart.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        mChart.setVisibleXRangeMaximum(20);

        Legend l = mChart.getLegend();

        String[] labels = {"最大值 " + maxPoint};
        int[] colors = {Color.BLUE};
        l.setCustom(colors, labels);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.RED);


        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        if (!hide) {
            mChart.moveViewToX(0);
//            mChart.moveViewToX(data.getXValCount() - 5);
//            mChart.invalidate();
        }

//        mChart.moveViewToX(data.getXValCount() - 5);
        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
    private static LineDataSet createLineDataSet() {

        LineDataSet set = new LineDataSet(null, "电压值/位移值");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(ColorTemplate.getHoloBlue());

        set.setCircleColor(Color.WHITE);
        set.setLineWidth(10f);
        set.setCircleSize(5f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);


        set.setLineWidth(1.75f); // 线宽
        set.setCircleSize(3f);// 显示的圆形大小
        set.setColor(Color.BLUE);// 显示颜色
        set.setCircleColor(Color.WHITE);// 圆形的颜色
        set.setHighLightColor(Color.WHITE); // 高亮的线的颜色


        // 改变折线样式，用曲线。
        set.setDrawCubic(true);
        // 默认是直线
        // 曲线的平滑度，值越大越平滑。
        set.setCubicIntensity(0.1f);

        set.setValueFormatter(new MyValueFormatter());

        set.setDrawValues(true);
        return set;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        hide = hidden;
        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            minScope = setting.getFloat(Constants.minScope, 0);
            maxScope = setting.getFloat(Constants.maxScope, 0);

            sensorScopeValue = setting.getFloat(Constants.sensorScope, 0);

            YAxis leftAxis = mChart.getAxisLeft();
            // 最大值
            leftAxis.setAxisMaxValue(maxScope);
            // 最小值
            leftAxis.setAxisMinValue(minScope);

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            minScope = setting.getFloat(Constants.minScope, 0);
            maxScope = setting.getFloat(Constants.maxScope, 0);
//            sensorScopeValue = setting.getFloat(SettingFragment.sensorScope, 0);

            YAxis leftAxis = mChart.getAxisLeft();
            // 最大值
            leftAxis.setAxisMaxValue(maxScope);
            // 最小值
            leftAxis.setAxisMinValue(minScope);

        }
    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Constants.SENSOR_DATA_COMMING)) {
                float[] distances = intent.getFloatArrayExtra(Constants.SENSOR_DATA);
                if (distances != null) {
                    Log.d("MEASURE", "DATA RECEIVED !!! length = " + distances.length);
                    Log.d("MEASURE", "DATA: " + distances.toString());
                    addEntrys(distances, hide);

                    List<Float> data = Lists.newArrayList();
                    for (int i = 0; i < distances.length; i++) {
                        data.add(distances[i]);
                    }

                    final EditText code = new EditText(getActivity());
                    final String dataStr = Joiner.on(",").join(data);
                    Log.d("DISTANCE", dataStr);

                    new AlertDialog.Builder(getActivity()).setTitle("请输入编号").setIcon(android.R.drawable.ic_dialog_info).setView(code).setMessage("日期编号").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Record item = new Record();
                            item.setCode(code.getText().toString());
                            Log.d("DISTANCE SAVE", dataStr);
                            Log.d("DISTANCE SAVE2", dataStr.toString());
                            item.setData(dataStr.toString());
                            item.setCreatedate(new Date().getTime());
                            RailWayApp.getSqlite().addRecord(item);
                            Log.d("MEASURE_PAGE", "record has been saved !!!");

                        }
                    }).setNegativeButton("取消", null).show();
                }
            }
            if (action.equals(Constants.SENSOR_BASE_POSITION_NOTCORRUCET)) {
                String high = intent.getStringExtra(Constants.BASE_POSITION_TOO_HIGH);
                String low = intent.getStringExtra(Constants.BASE_POSITION_TOO_LOW);
                if (high != null) {
                    Toast.makeText(
                            getActivity(),
                            "传感器探头基准位置过高", Toast.LENGTH_LONG).
                            show();
                }
                if (low != null) {
                    Toast.makeText(
                            getActivity(),
                            "传感器探头基准位置过低", Toast.LENGTH_LONG).
                            show();
                }
            }

        }
    }
}
