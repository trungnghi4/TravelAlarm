package com.NTQ.travelalarm.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.NTQ.travelalarm.Activity.LocationFriendActivity;
import com.NTQ.travelalarm.Data.FriendInfo;
import com.NTQ.travelalarm.Other.AlertsListAdapter;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Service.AppService;

import java.util.ArrayList;
import java.util.List;

public class AlertsListFragment extends Fragment {

    private ListView listView;
    private Context context;

    public AlertsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<FriendInfo> getListAccount() {
        List<FriendInfo> account = AppService.friendNear;
        if(account == null) {
            account = new ArrayList<>();
        }

        return account;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts_list, container, false);
        context = view.getContext();

        //dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_alerts);
        listView.setAdapter(new AlertsListAdapter(getListAccount(), getContext()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                FriendInfo friendInfo = (FriendInfo) obj;

                if(friendInfo.getStatus().equals("online")) {
                    Intent intent = new Intent(context, LocationFriendActivity.class);
                    intent.putExtra("account", friendInfo);
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

}
