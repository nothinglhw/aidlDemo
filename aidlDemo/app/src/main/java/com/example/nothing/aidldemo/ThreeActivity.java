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

public class ThreeActivity extends AppCompatActivity {
    @InjectView(R.id.next)
    public Button next;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        ButterKnife.inject(this);
        String des =  getIntent().getStringExtra("desc");
        Log.d("nothingwxq","" + des);
        if(!TextUtils.isEmpty(des)) {
            next.setText(des);
        }
    }

    @OnClick(R.id.next)
    public void onNextViewClicked(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
