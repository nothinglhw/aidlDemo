package com.example.nothing.aidldemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    int notifyId = 1;
    private NotificationManager nManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notify("one", OneActivity.class);
        notify("two", OneActivity.class);
        notify("three", OneActivity.class);

    }

    @OnClick(R.id.next)
    public void onNextViewClicked(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.start_four)
    public void onStartFourViewClicked(View view) {
        Intent intent = new Intent(this, FourActivity.class);
        startActivity(intent);
    }

    public void notify(String desc, Class clazz) {
        Intent perIntent = new Intent(this, clazz);
        perIntent.putExtra("desc", desc);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, perIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo_small)//
               .setContentText(desc)//
               .setContentTitle(desc)//
               .setTicker(desc)//
               .setContentIntent(pendingIntent);//
        Notification notification = null;
        notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nManager.notify("tag", 1, notification);// id是应用中通知的唯一标识
    }

}
