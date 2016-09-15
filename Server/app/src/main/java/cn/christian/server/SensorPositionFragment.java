package cn.christian.server;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import cn.christian.server.utils.Constants;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SensorPositionFragment extends Fragment {

    private static LineChart mChart;
    private static boolean hide = false;
//    private static float minScope;
//    private static float maxScope;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View statLayout = inflater.inflate(R.layout.fragment_sensorposition, container, false);

        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
//        if (setting != null) {
//            minScope = setting.getFloat(Constants.minScope, 0);
//            maxScope = setting.getFloat(Constants.maxScope, 0);
//        }
        mChart = (LineChart) statLayout.findViewById(R.id.position_chart);
        mChart.setDescription("传感器基准位置核对");
        mChart.setNoDataTextDescription("暂时尚无数据");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();

        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.BLUE);

        // x坐标轴
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
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
        leftAxis.setTextColor(Color.BLACK);

        // 设置x轴的LimitLine
        LimitLine ySumLimitLine = new LimitLine(0.1f, "基准上限 0.1mm");
        ySumLimitLine.setLineColor(Color.RED);
        ySumLimitLine.setTextColor(Color.RED);
        leftAxis.addLimitLine(ySumLimitLine);
        LimitLine yInfLimitLine = new LimitLine(-0.1f, "基准下限-0.1mm");
        yInfLimitLine.setLineColor(Color.RED);
        yInfLimitLine.setTextColor(Color.RED);
        leftAxis.addLimitLine(yInfLimitLine);

        YAxisValueFormatter format = new MyYAxisValueFormatter();
        leftAxis.setValueFormatter(format);

//        if (minScope != 0 || maxScope != 0) {
        // 最大值
        leftAxis.setAxisMaxValue(1f);
        // 最小值
        leftAxis.setAxisMinValue(-1f);

//        }

        // 不一定要从0开始
        leftAxis.setStartAtZero(false);

        leftAxis.setDrawGridLines(true);

//        leftAxis.setGridLineWidth(5f);

        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);


        return statLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasePositionDataReceiver actionReceiver = new BasePositionDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SENSOR_POSITION_CHECK_ACTION);
        getActivity().registerReceiver(actionReceiver, filter);

        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
//        if (setting != null) {
//            minScope = setting.getFloat(Constants.minScope, 0);
//            maxScope = setting.getFloat(Constants.maxScope, 0);
//        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        hide = hidden;
        Intent intent = new Intent();
        intent.setAction(Constants.SENSOR_POSITION_CHECK);
        intent.putExtra(Constants.SENSOR_POSITION_CHECK, hide);
        getActivity().sendBroadcast(intent);
    }

    protected void sendBroadcastToService(int state) {

        Intent intent = new Intent();
        intent.setAction(Constants.SENSOR_POSITION_CHECK);
        intent.putExtra(Constants.SENSOR_POSITION_CHECK, Constants.SENSOR_POSITION_CHECK_OPEN);
        //向后台Service发送播放控制的广播
        getActivity().sendBroadcast(intent);

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


        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        if (!hide) {
            mChart.moveViewToX(data.getXValCount() - 5);
        }
    }

    // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
    private static LineDataSet createLineDataSet() {

        LineDataSet set = new LineDataSet(null, "传感器位置");
//        LineDataSet set = new LineDataSet();
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(ColorTemplate.getHoloBlue());

        set.setCircleColor(Color.WHITE);
        set.setLineWidth(10f);
        set.setCircleSize(5f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.BLACK);
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

    class BasePositionDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Constants.SENSOR_POSITION_CHECK_ACTION)) {
                float position = intent.getFloatExtra(Constants.SENSOR_POSITION_DATA, 0);
                Log.d("POSITION_DATA", "received sensor position: " + position);
                addEntry(position, hide);
            }

        }

    }
}
