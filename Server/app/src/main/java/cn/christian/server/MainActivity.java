package cn.christian.server;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;


public class MainActivity extends Activity implements View.OnClickListener {

    private MeasureFragment measureFragment;
    private HistoryFragment historyFragment;
    private StatFragment statFragment;
    private SettingFragment settingFragment;

    private View measureLayout;
    private View historyLayout;
    private View statLayout;
    private View settingLayout;


    private ImageView measureImage;
    private ImageView historyImage;
    private ImageView statImage;
    private ImageView settingImage;

    private TextView measureText;
    private TextView historyText;
    private TextView statText;
    private TextView settingText;

    private FragmentManager fragmentManager;

    private void initViews() {

        measureLayout = findViewById(R.id.fragment_measure);
        historyLayout = findViewById(R.id.fragment_history);
        statLayout = findViewById(R.id.fragment_stat);
        settingLayout = findViewById(R.id.fragment_setting);

        measureImage = (ImageView) findViewById(R.id.measure_image);
        historyImage = (ImageView) findViewById(R.id.history_image);
        statImage = (ImageView) findViewById(R.id.stat_image);
        settingImage = (ImageView) findViewById(R.id.setting_image);

        measureText = (TextView) findViewById(R.id.measure_text);
        historyText = (TextView) findViewById(R.id.history_text);
        statText = (TextView) findViewById(R.id.stat_text);
        settingText = (TextView) findViewById(R.id.setting_text);

        measureLayout.setOnClickListener(this);
        historyLayout.setOnClickListener(this);
        statLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);


        Intent intent = new Intent(MainActivity.this, ADService.class);
        startService(intent);

        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getApplication().onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏， 在此处添加额外的处理代码
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏， 在此处添加额外的处理代码
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_measure:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.fragment_history:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.fragment_stat:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.fragment_setting:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }


    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                measureImage.setImageResource(R.drawable.measure);
                measureImage.setBackgroundColor(getResources().getColor(R.color.goldenrod));
                measureText.setTextColor(Color.WHITE);
                if (measureFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    measureFragment = new MeasureFragment();
                    transaction.add(R.id.content, measureFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(measureFragment);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                historyImage.setImageResource(R.drawable.history);
                historyImage.setBackgroundColor(getResources().getColor(R.color.goldenrod));
                historyText.setTextColor(Color.WHITE);
                if (historyFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    historyFragment = new HistoryFragment();
                    transaction.add(R.id.content, historyFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(historyFragment);
                }
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                statImage.setImageResource(R.drawable.statistics);
                statImage.setBackgroundColor(getResources().getColor(R.color.goldenrod));
                statText.setTextColor(Color.WHITE);
                if (statFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    statFragment = new StatFragment();
                    transaction.add(R.id.content, statFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(statFragment);
                }
                break;
            case 3:
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                settingImage.setImageResource(R.drawable.setting);
                settingImage.setBackgroundColor(getResources().getColor(R.color.goldenrod));
                settingText.setTextColor(Color.WHITE);
                if (settingFragment == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(settingFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        measureImage.setImageResource(R.drawable.measure);
        measureImage.setBackgroundColor(getResources().getColor(R.color.black));
        measureText.setTextColor(Color.parseColor("#82858b"));
        historyImage.setImageResource(R.drawable.history);
        historyImage.setBackgroundColor(getResources().getColor(R.color.black));
        historyText.setTextColor(Color.parseColor("#82858b"));
        statImage.setImageResource(R.drawable.statistics);
        statImage.setBackgroundColor(getResources().getColor(R.color.black));
        statText.setTextColor(Color.parseColor("#82858b"));
        settingImage.setImageResource(R.drawable.setting);
        settingImage.setBackgroundColor(getResources().getColor(R.color.black));
        settingText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (measureFragment != null) {
            transaction.hide(measureFragment);
        }
        if (historyFragment != null) {
            transaction.hide(historyFragment);
        }
        if (statFragment != null) {
            transaction.hide(statFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
    }
}

class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("##.###"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
//        return mFormat.format(value) + " $"; // e.g. append a dollar-sign
        return mFormat.format(value); // e.g. append a dollar-sign
    }
}

class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("##.###"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }

}





