package com.NTQ.travelalarm.Other;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.NTQ.travelalarm.Data.FirebaseHandle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.NTQ.travelalarm.Data.FriendInfo;
import com.NTQ.travelalarm.R;

import java.text.DecimalFormat;
import java.util.List;

import static com.NTQ.travelalarm.Other.Constants.ONLINE;


public class FriendsListAdapter extends BaseAdapter {
    private List<FriendInfo> accounts;
    private LayoutInflater inflater;
    private Context context;


    public FriendsListAdapter(List<FriendInfo> accounts, Context context) {
        this.accounts = accounts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        public LinearLayout itemLayout;
        public ImageView friend_avatar;
        public TextView friend_name;
        public CheckBox friend_chkBox;
        public TextView friend_distance;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FriendsListAdapter.ViewHolder holder;

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_friends, null);
            holder = new FriendsListAdapter.ViewHolder();
            holder.friend_avatar = (ImageView) view.findViewById(R.id.img_avatar);
            holder.friend_name = (TextView) view.findViewById(R.id.friend_name);
            holder.itemLayout = (LinearLayout) view.findViewById(R.id.friend_item);
            holder.friend_chkBox = (CheckBox) view.findViewById(R.id.friend_chkbox);
            holder.friend_distance = (TextView) view.findViewById(R.id.friend_distance);
            view.setTag(holder);
        } else{
            holder = (FriendsListAdapter.ViewHolder) view.getTag();
        }

        Glide.with(context).load(accounts.get(i).getAvatarURL())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.friend_avatar);

        holder.friend_name.setText(accounts.get(i).getName());


        if(accounts.get(i).getStatus().equals(ONLINE)) {
            holder.itemLayout.setBackgroundColor(Color.WHITE);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            Double distance = FirebaseHandle.getInstance().getDistanceFromFriend(accounts.get(i).getId());
            holder.friend_distance.setText((distance < 1000) ?
                    (context.getResources().getText(R.string.distance_with_user) + decimalFormat.format(distance) + " m")
                    : (context.getResources().getText(R.string.distance_with_user)  + decimalFormat.format(distance / 1000.0) + " km"));
        }
        else {
            holder.friend_distance.setText(context.getResources().getText(R.string.friend_offline));
            holder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.colorFriendOffline));
        }

        if(accounts.get(i).isFollowing()) {
            holder.friend_chkBox.setChecked(true);
        }

        final FriendInfo friendInfo= accounts.get(i);

        holder.friend_chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FirebaseHandle.getInstance().setFollowFriend(friendInfo.getId(), b);
                if(b) {
                    Toast.makeText(context, context.getResources().getText(R.string.set_follow_friend) + " " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getText(R.string.delete_follow_friend) + " " + friendInfo.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }
}
