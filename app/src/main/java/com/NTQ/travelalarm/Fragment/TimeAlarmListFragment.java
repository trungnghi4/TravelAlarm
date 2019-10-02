package com.NTQ.travelalarm.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.NTQ.travelalarm.Data.DatabaseHelper;
import com.NTQ.travelalarm.Data.TimeInfo;
import com.NTQ.travelalarm.Other.TimeListAdapter;
import com.NTQ.travelalarm.R;

import java.util.List;


public class TimeAlarmListFragment extends Fragment {

    private Context context;
    private ListView listView;

    private DatabaseHelper dbHelper;


    public TimeAlarmListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_alarm_list, container, false);
        context = view.getContext();

        dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_time_view);
        listView.setAdapter(new TimeListAdapter(getListTime(), context));

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Object obj = adapterView.getItemAtPosition(i);
//                Route route = (Route) obj;
//
//                //move edit alarm activity
//                Intent intent = new Intent(getContext(), EditAlarmActivity.class);
//                intent.putExtra("route", (Serializable) route);
//                getContext().startActivity(intent);
//            }
//        });

        return view;
    }
    private List<TimeInfo> getListTime() {
        return dbHelper.getListTimeInfo("SELECT * FROM " + DatabaseHelper.TABLE_TIMEHISTORY);
    }
}
