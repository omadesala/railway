package cn.christian.server;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.common.collect.Maps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.christian.server.application.RailWayApp;
import cn.christian.server.dao.Record;
import cn.christian.server.utils.Constants;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HistoryFragment extends ListFragment {

    private String TAG = HistoryFragment.class.getName();
    private ListView list;
    private SimpleAdapter adapter;
    private String[] listData;
    private int[] dataids;
//    private Map<String, String> idWithName = Maps.newHashMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View historyLayout = inflater.inflate(R.layout.fragment_history, container, false);
        list = (ListView) historyLayout.findViewById(android.R.id.list);
        Log.i(TAG, "--------onCreateView");

        String[] datas = {"History 1", "History 2", "History 3", "History 4", "History 5", "History 6", "History 7", "History 8", "History 9", "History 10", "History 11", "History 12 ", "History 13", "History 15", "History 16"};


        Date begin = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(Calendar.DATE, -1);

//        List<Record> record = RailWayApp.getSqlite().getRecordByPeriod(begin, cal.getTime());
        List<Record> record = RailWayApp.getSqlite().getAllRecord();

//        String[] viewDatas = new String[record.size()];
        listData = new String[record.size()];
        dataids = new int[record.size()];
        for (int i = 0; i < record.size(); i++) {

            Record item = record.get(i);
            listData[i] = item.getCode();
            dataids[i] = item.getId();
        }


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

//
//        list.setAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, datas));
//

        return historyLayout;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            Date begin = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(begin);
            cal.add(Calendar.DAY_OF_MONTH, -1);

//            List<Record> record = RailWayApp.getSqlite().getRecordByPeriod(begin, cal.getTime());
            List<Record> record = RailWayApp.getSqlite().getAllRecord();

            String[] viewDatas = new String[record.size()];
            for (int i = 0; i < record.size(); i++) {
                viewDatas[i] = record.get(i).getCode();
            }
            list.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, viewDatas));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//    }
//
    }


}
