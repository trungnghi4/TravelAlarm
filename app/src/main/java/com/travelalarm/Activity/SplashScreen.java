package com.travelalarm.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.travelalarm.R;

public class SplashScreen extends AppCompatActivity {
    protected boolean _active = true;
    protected int _splashTime = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try{
                    int waited = 0;
                    while (_active && (waited <_splashTime)){
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                }
                catch (InterruptedException e){}
                finally {
                    finish();

                    Intent mainIntent = new Intent(SplashScreen.this,FacebookConnection.class);
                    SplashScreen.this.startActivity(mainIntent);
                }

            }
        };
        splashThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
