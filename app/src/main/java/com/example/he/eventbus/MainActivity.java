package com.example.he.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
    }

    @Subscribe(ThreadMode.MainThread)
    public void receive(String content) {
        System.out.println(content.toString());
        Log.e("he", content.toString());
    }

    public void jump(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

}
