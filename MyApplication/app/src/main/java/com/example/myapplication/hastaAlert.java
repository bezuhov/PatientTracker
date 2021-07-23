package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

public class hastaAlert extends Activity {

    Context thisActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_hasta_alert);

        thisActivityContext = this;

        Intent intent=getIntent();
        String type=intent.getStringExtra("type");

        final Button myButtonn = (Button) findViewById(R.id.myButtonn);
        final Button myButtonnn = (Button) findViewById(R.id.my_buttonnn);

        myButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myButtonnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonder(type);

                onBackPressed();
            }
        });

    }

    public void gonder(String type) {
        EventBus.getDefault().post(new MessageEvent(type));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mainMenuItem) {
        switch (mainMenuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(mainMenuItem);
    }


}