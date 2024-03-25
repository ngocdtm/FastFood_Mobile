package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodnhanh.R;

//====================================Màn hình chờ====================================//
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }catch(Exception e){

                }
            }
        };thread.start();
    }
}