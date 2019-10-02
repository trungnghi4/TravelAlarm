package com.NTQ.travelalarm.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.R;


public class SplashScreen extends BaseActivity {
    protected boolean _active = true;
    protected int _splashTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

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
    protected void onPause() {
        super.onPause();
        finish();

    }
}