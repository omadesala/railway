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
import java.util.Locale;

import cn.christian.server.application.RailWayApp;
import cn.christian.server.dao.Record;
import cn.christian.server.utils.Constants;
import cn.christian.server.utils.DateUtil;

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
    private Calendar cal = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View historyLayout = inflater.inflate(R.layout.fragment_history, container, false);
        list = (ListView) historyLayout.findViewById(android.R.id.list);
        datePicker = (Button) historyLayout.findViewById(R.id.date_begin_end_pick);
        datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        record = RailWayApp.getSqlite().getRecordByDate(cal.getTime());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月

                dialog.show();
            }
        });

        Date begin = new Date();
//        cal = Calendar.getInstance();
//        cal.getTime();
//        cal.setTime(begin);
//        cal.add(Calendar.DAY_OF_MONTH, -7);

//        record = RailWayApp.getSqlite().getRecordByInterval(begin, cal.getTime());
        record = RailWayApp.getSqlite().getRecordByInterval(DateUtil.plusDays(begin, -7), begin);
//        record = RailWayApp.getSqlite().getAllRecord();
        if (record.isEmpty()) {
            return historyLayout;
        } else {
            listData = new String[record.size()];
            dataids = new int[record.size()];
            for (int i = 0; i < record.size(); i++) {
                Record item = record.get(i);
                Log.d("RECORD", item.toString());
                listData[i] = item.getCode();
                dataids[i] = item.getId();
            }


            list.setClickable(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        return historyLayout;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            Date begin = new Date();
//            Calendar cal = Calendar.getInstance();
//            cal.getTime();
//            cal.setTime(begin);
//            cal.add(Calendar.DAY_OF_MONTH, -7);
            record = RailWayApp.getSqlite().getRecordByInterval(DateUtil.plusDays(begin, -7), begin);
//            List<Record> record = RailWayApp.getSqlite().getRecordByInterval(begin, cal.getTime());
//            List<Record> record = RailWayApp.getSqlite().getAllRecord();

            String[] viewDatas = new String[record.size()];
            for (int i = 0; i < record.size(); i++) {
                viewDatas[i] = record.get(i).getCode();
                Log.d("History", record.toString());
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
//        cal = Calendar.getInstance(Locale.getDefault());
    }


}
