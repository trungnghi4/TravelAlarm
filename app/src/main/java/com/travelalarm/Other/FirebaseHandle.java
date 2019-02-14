package com.travelalarm.Other;

import android.util.Log;

import com.travelalarm.Data.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.travelalarm.Other.Constants.FB_ACCOUNT;


public class FirebaseHandle {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    public FirebaseHandle() {
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    public void updateRoute(Route route){
        mRef.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid())
                .child("listRoute").child(String.valueOf(route.getId())).setValue(route);
    }

    public void updateCurPos(String latitude, String longitude) {
        mRef.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid())
                .child("curPos").child("latitude").setValue(latitude);
        mRef.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid())
                .child("curPos").child("longitude").setValue(longitude);
    }

    public void removeRoute(String id) {
        mRef.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid())
                .child("listRoute").child(id).removeValue();
    }

    public void updateStatus(String status) {
        mRef.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid())
                .child("status").setValue(status);
    }
}
