package com.NTQ.travelalarm.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.NTQ.travelalarm.Activity.LocationFriendActivity;
import com.NTQ.travelalarm.Data.FirebaseHandle;
import com.NTQ.travelalarm.Data.FriendInfo;
import com.NTQ.travelalarm.Other.FriendsListAdapter;
import com.NTQ.travelalarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.NTQ.travelalarm.Other.Constants.ONLINE;


public class FriendsListFragment extends Fragment {

    private ListView listView;
    private Context context;
    private Timer timer;

    public FriendsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(timer == null)
            setUpdateListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        context = view.getContext();

        listView = (ListView) view.findViewById(R.id.list_friends);
        listView.setAdapter(new FriendsListAdapter(getListAccount(), getContext()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getItemAtPosition(i);
                FriendInfo friendInfo = (FriendInfo) obj;

                if(friendInfo.getStatus().equals(ONLINE) && friendInfo.getLatitude() != null &&
                        friendInfo.getLongitude() != null) {
                    Intent intent = new Intent(context, LocationFriendActivity.class);
                    intent.putExtra("account", friendInfo);
                    context.startActivity(intent);
                }
            }
        });

        setUpdateListView();

        return view;
    }

    private void setUpdateListView() {
        timer = new Timer();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter(null);
                listView.setAdapter(new FriendsListAdapter(getListAccount(), context));
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };

        timer.schedule(timerTask, 3000, 1500);
    }

    private List<FriendInfo> getListAccount() {
        List<FriendInfo> account = FirebaseHandle.getInstance().getListFriends();
        if(account == null) {
            account = new ArrayList<>();
        }
        return account;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_action) {
            listView.setAdapter(null);
            listView.setAdapter(new FriendsListAdapter(getListAccount(), context));
            Toast.makeText(context, getResources().getText(R.string.refresh_list_friends), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        timer.cancel();
        timer = null;
        super.onStop();
    }
}
