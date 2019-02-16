package com.travelalarm.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.travelalarm.Data.DatabaseHelper;
import com.travelalarm.Data.FirebaseHandle;
import com.travelalarm.Other.Account;
import com.travelalarm.Other.FriendsListAdapter;
import com.travelalarm.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FriendsListFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ListView listView;


    public FriendsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        //dbHelper = new DatabaseHelper(getContext());
        listView = (ListView) view.findViewById(R.id.list_friends);
        listView.setAdapter(new FriendsListAdapter(getistAccount(), getContext()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


        return view;
    }

    private List<Account> getistAccount() {
        List<Account> account = FirebaseHandle.getInstance().getListFriends();
        if(account == null) {
            account = new ArrayList<>();
        }
        return account;
    }

}
