package com.NTQ.travelalarm.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.R;
import com.google.firebase.auth.FirebaseAuth;


public class SplashScreen extends BaseActivity {
    protected boolean _active = true;
    protected int _splashTime = 3000;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splashscreen;
    }

    @Override
    public void addControl() {
    }

    @Override
    public void addEvent() {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();

                    Intent mainIntent = new Intent(SplashScreen.this, SignIn.class);
                    SplashScreen.this.startActivity(mainIntent);
                }
            }
        };
        splashTread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}