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
import cn.christian.server.utils.DataUtil;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MeasureFragment extends Fragment {

    //    public final static int MSG_DATA_RECEIVED = 0;
    private static LineChart mChart;
    private static boolean hide = false;
    private static float minScope;
    private static float maxScope;

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
        }
    }


    // 添加进去一个坐标点
    private static void addEntrys(float[] distance, boolean hide) {


        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);


        LineDataSet set = createLineDataSet();
        data.addDataSet(set);

        mChart.notifyDataSetChanged();

        for (int i = 0; i < distance.length; i++) {
            Entry entry = new Entry(distance[i], set.getEntryCount());
            data.addXValue(i + "");
            data.addEntry(entry, 0);
        }


        // 像ListView那样的通知数据更新
        mChart.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        mChart.setVisibleXRangeMaximum(20);

        Legend l = mChart.getLegend();

        String[] labels = {"最大值:" + DataUtil.getMax(distance)};
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
