package com.NTQ.travelalarm.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.NTQ.travelalarm.Data.DatabaseHelper;
import com.NTQ.travelalarm.Data.TimeInfo;
import com.NTQ.travelalarm.R;

import java.util.List;


public class TimeListAdapter extends BaseAdapter {
    private List<TimeInfo> timeList;
    private LayoutInflater inflater;
    private Context context;
    private DatabaseHelper dbHelper;

    public TimeListAdapter(List<TimeInfo> timeList, Context context) {
        this.timeList = timeList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return timeList.size();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public Object getItem(int i) {
        return timeList.get(i);
    }
    static class ViewHolder {
        public ImageView imgAlarm;
        public TextView alarmName;
        public TextView alarmTime;
        public Switch btnSetAlarm;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_time_alarm, null);
            holder = new ViewHolder();
            holder.imgAlarm = (ImageView) view.findViewById(R.id.img_Alarm);
            holder.alarmName = (TextView) view.findViewById(R.id.alarm_name);
            holder.alarmTime = (TextView) view.findViewById(R.id.alarm_time);
            holder.btnSetAlarm = (Switch) view.findViewById(R.id.switch_set_alarm);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }

        final TimeInfo timeInfo = timeList.get(i);
//        if(route.getIsEnable() == 0) {
//            holder.imgAlarm.setImageResource(R.drawable.ic_location_off);
//            holder.btnSetAlarm.setChecked(false);
//        } else {
//            holder.imgAlarm.setImageResource(R.drawable.ic_location_on);
//            holder.btnSetAlarm.setChecked(true);
//        }
        holder.alarmName.setText(timeInfo.getNote());
        holder.alarmTime.setText(timeInfo.getHour()+":"+timeInfo.getMinute());

//        holder.btnSetAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b) {
//                    //set alarm
//                    route.setIsEnable(1);
//                    dbHelper.updateRoute(route);
//
//                    FirebaseHandle.getInstance().updateRoute(route);
//                    holder.imgAlarm.setImageResource(R.drawable.ic_location_on);
//                    context.startService(new Intent(context, BackgroundService.class));
//                } else {
//                    //disable alarm
//                    route.setIsEnable(0);
//                    dbHelper.updateRoute(route);
//
//                    FirebaseHandle.getInstance().updateRoute(route);
//                    holder.imgAlarm.setImageResource(R.drawable.ic_location_off);
//                }
//            }
//        });

        return view;
    }
}
