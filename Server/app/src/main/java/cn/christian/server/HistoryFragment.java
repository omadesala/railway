package cn.christian.server;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.christian.server.application.RailWayApp;
import cn.christian.server.dao.Record;
import cn.christian.server.utils.Constants;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HistoryFragment extends Fragment {

    private String TAG = HistoryFragment.class.getName();
    private ListView list;
    private SimpleAdapter adapter;
    private String[] listData;
    private int[] dataids;
    private List<Record> record;
    private Button datePicker;
    private TextView dateScopeText;
    private Calendar cal;
    private int year, month, day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View historyLayout = inflater.inflate(R.layout.fragment_history, container, false);
        list = (ListView) historyLayout.findViewById(android.R.id.list);
        datePicker = (Button) historyLayout.findViewById(R.id.date_begin_end_pick);
        dateScopeText = (TextView) historyLayout.findViewById(R.id.date_begin_end);
        cal = Calendar.getInstance();
        datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
//                new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
//                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
//                                          int endDayOfMonth) {
//
//
//                        String textString = String.format("开始时间：%d-%d-%d\n结束时间：%d-%d-%d\n", startYear,
//                                startMonthOfYear + 1, startDayOfMonth, endYear, endMonthOfYear + 1, endDayOfMonth);
//                        dateScopeText.setText(textString);
//
//
//                    }
//                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();


                DatePickerDialog dialog = new DatePickerDialog(getActivity(), 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        cal.set(year, monthOfYear, dayOfMonth);
                        record = RailWayApp.getSqlite().getRecordByDate(cal.getTime());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月

                dialog.show();


            }
        });

        Log.i(TAG, "--------onCreateView");

        String[] datas = {"History 1", "History 2", "History 3", "History 4", "History 5", "History 6", "History 7", "History 8", "History 9", "History 10", "History 11", "History 12 ", "History 13", "History 15", "History 16"};


//        Date begin = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(begin);
//        cal.add(Calendar.DAY_OF_MONTH, -1);

//        record = RailWayApp.getSqlite().getRecordByDate(new Date());
        record = RailWayApp.getSqlite().getAllRecord();
        if (record.isEmpty()) {
            return historyLayout;
        } else

        {
//        String[] viewDatas = new String[record.size()];
            listData = new String[record.size()];
            dataids = new int[record.size()];
            for (int i = 0; i < record.size(); i++) {

                Record item = record.get(i);
                listData[i] = item.getCode();
                dataids[i] = item.getId();
            }


            list.setClickable(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Log.d("HISTORY", "ITEM ON CLICK");
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), HistoryDataActivity.class);
                    intent.setAction(Constants.SENSOR_HISTORY_DETAIL_ACTION);
                    intent.putExtra(Constants.SENSOR_HISTORY_DATA, dataids[position]);

                    startActivity(intent);
                }
            });


            list.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, listData));
        }
//
//        list.setAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, datas));
//

        return historyLayout;
    }

    //获取当前日期
    private void getDate() {
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy", "year" + year);
        month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            Date begin = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(begin);
            cal.add(Calendar.DAY_OF_MONTH, -1);

//            List<Record> record = RailWayApp.getSqlite().getRecordByDate(begin, cal.getTime());
            List<Record> record = RailWayApp.getSqlite().getAllRecord();

            String[] viewDatas = new String[record.size()];
            for (int i = 0; i < record.size(); i++) {
                viewDatas[i] = record.get(i).getCode();
            }
            list.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, viewDatas));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Log.d("HISTORY", "ITEM ON CLICK");
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), HistoryDataActivity.class);
                    intent.setAction(Constants.SENSOR_HISTORY_DETAIL_ACTION);
                    intent.putExtra(Constants.SENSOR_HISTORY_DATA, dataids[position]);

                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//    }
//
    }


}
