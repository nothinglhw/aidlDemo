package com.example.nothing.aidldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FourActivity extends AppCompatActivity {
    @InjectView(R.id.next)
    public Button next;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.next)
    public void onNextViewClicked(View view) {
        boolean started = AlipayService.trigger(this);
        next.setText(started ? "Stop" : "Start");
    }
}
