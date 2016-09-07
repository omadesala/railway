package cn.christian.server;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HistoryFragment extends ListFragment {

    private String TAG = HistoryFragment.class.getName();
    private ListView list;
    private SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View historyLayout = inflater.inflate(R.layout.fragment_history, container, false);
        list = (ListView) historyLayout.findViewById(android.R.id.list);
        Log.i(TAG, "--------onCreateView");

        String[] datas = {"History 1", "History 2", "History 3", "History 4", "History 5", "History 6", "History 7", "History 8", "History 9", "History 10", "History 11", "History 12 ", "History 13", "History 15", "History 16"};


        list.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, datas));
        return historyLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//    }
//
    }


}
