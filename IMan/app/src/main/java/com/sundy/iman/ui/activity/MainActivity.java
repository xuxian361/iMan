package com.sundy.iman.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sundy.iman.R;
import com.sundy.iman.paperdb.PaperUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        //设置第一次启动APP标志，设置后表示启动过APP
        PaperUtils.setFirstLaunchApp();
    }
}
