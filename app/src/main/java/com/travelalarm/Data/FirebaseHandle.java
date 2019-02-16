package com.travelalarm.Data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.travelalarm.Other.Constants.FB_ACCOUNT;
import static com.travelalarm.Other.Constants.FB_FRIENDS;


public class FirebaseHandle {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String userID;
    private static FirebaseHandle instance;
    private List<FriendInfo> listFriends;

    private FirebaseHandle() {
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseHandle getInstance() {
        if(instance == null) {
            instance = new FirebaseHandle();
        }
        return instance;
    }

    public void setUserID(String id) {
        this.userID = id;
        setAccountListener();
    }

    public void setStatusChange() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected) {
                    try {
                        mRef.child(FB_ACCOUNT).child(userID)
                                .child("status").setValue("online");

                        mRef.child(FB_ACCOUNT).child(userID)
                                .child("status").onDisconnect().setValue("offline");
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateRoute(Route route){
        mRef.child(FB_ACCOUNT).child(userID)
                .child("listRoute").child(String.valueOf(route.getId())).setValue(route);
    }

    public void updateCurPos(Double latitude, Double longitude) {
        try {
            mRef.child(FB_ACCOUNT).child(userID)
                    .child("curPos").child("latitude").setValue(latitude);
            mRef.child(FB_ACCOUNT).child(userID)
                    .child("curPos").child("longitude").setValue(longitude);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void removeRoute(String id) {
        mRef.child(FB_ACCOUNT).child(userID)
                .child("listRoute").child(id).removeValue();
    }

    public void setAccountListener() {
        if(mRef.child(FB_ACCOUNT).child(userID).child("friends") != null)
            mRef.child(FB_ACCOUNT).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listFriends = new ArrayList<>();
                    for (DataSnapshot postSnapshot: dataSnapshot.child(userID).child("friends").getChildren()) {
                        if(listFriends.size() <  dataSnapshot.child(userID).child("friends").getChildrenCount())
                        {
                            FriendInfo tempAccount = new FriendInfo();
                            String id = postSnapshot.child("id").getValue(String.class);
                            tempAccount.setId(id);
                            if(postSnapshot.hasChild("isFollowing"))
                                tempAccount.setFollowing(postSnapshot.child("isFollowing").getValue(Boolean.class));
                            else
                                tempAccount.setFollowing(false);
                            if(postSnapshot.hasChild("minDis")) {
                                tempAccount.setMinDis(postSnapshot.child("minDis").getValue(Integer.class));
                            } else {
                                tempAccount.setMinDis(100);
                            }
                            if(postSnapshot.hasChild("isNotifying")) {
                                tempAccount.setNotifying(postSnapshot.child("isNotifying").getValue(Boolean.class));
                            } else {
                                tempAccount.setNotifying(false);
                            }
                            tempAccount.setName(dataSnapshot.child(id).child("name").getValue(String.class));
                            tempAccount.setAvatarURL(dataSnapshot.child(id).child("avatarURL").getValue(String.class));
                            tempAccount.setStatus(dataSnapshot.child(id).child("status").getValue(String.class));
                            tempAccount.setLatitude(dataSnapshot.child(id).child("curPos").child("latitude").getValue(Double.class));
                            tempAccount.setLongitude(dataSnapshot.child(id).child("curPos").child("longitude").getValue(Double.class));
                            listFriends.add(tempAccount);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    public List<FriendInfo> getListFriends()
    {
        return listFriends;
    }

    public void setFollowFriend(String id, boolean isFollowing) {
        mRef.child(FB_ACCOUNT).child(userID).child("friends").child(id)
                .child("isFollowing").setValue(isFollowing);
    }

    public void updateNotiOfFriend(String id, int minDis) {
        mRef.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                .child("minDis").setValue(minDis);
    }

    public void setNotifyFriend(String id, boolean isNotifying) {
        mRef.child(FB_ACCOUNT).child(userID).child("friends").child(id)
                .child("isNotifying").setValue(isNotifying);
    }
}
