package com.NTQ.travelalarm.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.NTQ.travelalarm.Data.RingtoneModel;
import com.NTQ.travelalarm.R;

import java.util.ArrayList;

public class RingtoneAdapter extends ArrayAdapter<RingtoneModel> implements View.OnClickListener {

    private static class ViewHolder {
        TextView nameRingtone;
        TextView timeRingtone;
    }
    private ArrayList<RingtoneModel> dataRingtone;
    private Context mContext;


    public RingtoneAdapter(ArrayList<RingtoneModel> data, Context context) {
        super(context, R.layout.row_item_ringtone, data);
        this.dataRingtone = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        RingtoneModel dataModel=(RingtoneModel)object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_item_ringtone, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameRingtone = (TextView) convertView.findViewById(R.id.nameRingtone);
            viewHolder.timeRingtone = (TextView) convertView.findViewById(R.id.timeRingtone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RingtoneModel ringtoneModel = dataRingtone.get(position);
        viewHolder.nameRingtone.setText(ringtoneModel.getNameRingtone());
        viewHolder.timeRingtone.setText(ringtoneModel.getTimeRingtone());
        return convertView;
    }
}
