package com.NTQ.travelalarm.Other;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.NTQ.travelalarm.Data.DatabaseHelper;
import com.NTQ.travelalarm.Data.Route;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Service.BackgroundService;

import java.text.DecimalFormat;
import java.util.List;


public class RouteListAdapter extends BaseAdapter {
    private List<Route> routeList;
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHelper dbHelper;

    public RouteListAdapter(List<Route> routeList, Context context) {
        this.routeList = routeList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Object getItem(int i) {
        return routeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        public ImageView imgAlarm;
        public TextView alarmName;
        public TextView alarmDistance;
        public Switch btnSetAlarm;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_alarm, null);
            holder = new ViewHolder();
            holder.imgAlarm = (ImageView) view.findViewById(R.id.img_Alarm);
            holder.alarmName = (TextView) view.findViewById(R.id.alarm_name);
            holder.alarmDistance = (TextView) view.findViewById(R.id.alarm_distance);
            holder.btnSetAlarm = (Switch) view.findViewById(R.id.switch_set_alarm);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }

        final Route route = routeList.get(i);
        if(route.getIsEnable() == 0) {
            holder.imgAlarm.setImageResource(R.drawable.ic_location_off);
            holder.btnSetAlarm.setChecked(false);
        } else {
            holder.imgAlarm.setImageResource(R.drawable.ic_location_on);
            holder.btnSetAlarm.setChecked(true);
        }
        holder.alarmName.setText(route.getName());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Double distance = route.getDistance();
        holder.alarmDistance.setText((distance < 1000) ?
                (context.getResources().getText(R.string.current_distance) + decimalFormat.format(distance) + "m"):
                (context.getResources().getText(R.string.current_distance) + decimalFormat.format(distance / 1000.0) + "km"));

        holder.btnSetAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    //set alarm
                    route.setIsEnable(1);
                    dbHelper.updateRoute(route);

//                    FirebaseHandle.getInstance().updateRoute(route);
                    holder.imgAlarm.setImageResource(R.drawable.ic_location_on);
                    context.startService(new Intent(context, BackgroundService.class));
                } else {
                    //disable alarm
                    route.setIsEnable(0);
                    dbHelper.updateRoute(route);

//                    FirebaseHandle.getInstance().updateRoute(route);
                    holder.imgAlarm.setImageResource(R.drawable.ic_location_off);
                }
            }
        });
        return view;
    }
}
