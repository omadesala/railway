package cn.christian.server;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import cn.christian.server.application.RailWayApp;
import cn.christian.server.dao.Record;
import cn.christian.server.utils.Constants;
import cn.christian.server.view.CustomMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 */
public class HistoryDataActivity extends Activity {


    private ImageButton back;
    private LineChart mChart;
    private CustomMarkerView mv;
    int widthPixels;
    int heightPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.historydata);
        initView();

        Intent intent = getIntent();
        int dataid = intent.getIntExtra(Constants.SENSOR_HISTORY_DATA, -1);

        Log.d("DATAACTIVITY", "dataid:" + dataid);
        Record recordById = RailWayApp.getSqlite().getRecordById(dataid);

        if (recordById != null) {
            String data = recordById.getData();
            if (!Strings.isNullOrEmpty(data)) {

                Log.d("DATAACTIVITY", "data" + data);

                Iterable<String> split = Splitter.on(",").split(data);

                Iterator<String> iterator = split.iterator();
                List<Float> datas = Lists.newArrayList();

                while (iterator.hasNext()) {
                    String item = iterator.next();
                    float v = Float.parseFloat(item);
                    datas.add(v);
                }


                float[] dis = new float[datas.size()];

                for (int i = 0; i < datas.size(); i++) {
                    dis[i] = datas.get(i);
                }

                addEntrys(dis, false);
            }
        }
    }

    private void initView() {

        mChart = (LineChart) findViewById(R.id.chart_history);
        back = (ImageButton) findViewById(R.id.detail_exit);
        mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initLineChart();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;

    }

    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;

        Intent intent = getIntent();
        int dataid = intent.getIntExtra(Constants.SENSOR_HISTORY_DATA, -1);

//        RailWayApp.getSqlite().deleteAllRecord();
        Record recordById = RailWayApp.getSqlite().getRecordById(dataid);

        if (recordById != null) {
            String data = recordById.getData();
            if (!Strings.isNullOrEmpty(data)) {

                Iterable<String> split = Splitter.on(",").split(data);

                Iterator<String> iterator = split.iterator();
                List<Float> datas = Lists.newArrayList();

                while (iterator.hasNext()) {
                    String item = iterator.next();
                    float v = Float.parseFloat(item);
                    datas.add(v);
                }


                float[] dis = new float[datas.size()];

                for (int i = 0; i < datas.size(); i++) {
                    dis[i] = datas.get(i);
                }

                addEntrys(dis, false);
            }
        }
    }

    private void initLineChart() {

        mChart.setDescription("历史回看 第二行");
        mChart.setDescriptionColor(Color.BLACK);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
        Log.d("DATA", "x: " + widthPixels + " y:" + heightPixels);
        mChart.setDescriptionPosition(widthPixels - 100, heightPixels / 6);


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
        data.setValueTextColor(Color.BLACK);

        // 先增加一个空的数据，随后往里面动态添加
        mChart.setData(data);


        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mChart.getLegend();

//        String[] labels = {"最大值"+ DataUtil.getMax()};
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


        YAxisValueFormatter format = new MyYAxisValueFormatter();
        leftAxis.setValueFormatter(format);

        // 不一定要从0开始
        leftAxis.setStartAtZero(false);

        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);
    }

    private void addEntrys(float[] distance, boolean hide) {


        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.BLACK);

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
        mChart.setVisibleXRangeMaximum(250);

        Legend l = mChart.getLegend();

        String[] labels = {"历史回看"};
        int[] colors = {Color.BLUE};
        l.setCustom(colors, labels);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(Color.BLUE);


        DecimalFormat mFormat = new DecimalFormat("#.###");
        float yMax = mChart.getYMax();
        float yMin = mChart.getYMin();
        mChart.setDescription("最大值: " + mFormat.format(yMax) + " 最小值: " + mFormat.format(yMin));
        mChart.setDescriptionTextSize(20.0f);

        mChart.setMarkerView(mv);


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

//        set.setCircleColor(Color.WHITE);
//        set.setLineWidth(2f);
//        set.setCircleSize(1f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.GREEN);
//        set.setValueTextColor(Color.WHITE);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);


        set.setLineWidth(0.75f); // 线宽
        set.setCircleSize(1f);// 显示的圆形大小
        set.setColor(Color.BLUE);// 显示颜色
        set.setCircleColor(Color.MAGENTA);// 圆形的颜色
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

}
