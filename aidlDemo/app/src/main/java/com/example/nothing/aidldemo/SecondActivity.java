package com.example.nothing.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import aidl.Book;
import aidl.IBookManager;
import aidl.IOnNewBookArrivedListener;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SecondActivity extends AppCompatActivity {

    @InjectView(R.id.book_name)
    public EditText bookNameTV;
    @InjectView(R.id.book_add)
    public Button bookAddTV;
    @InjectView(R.id.book_count)
    public Button bookCountTV;
    @InjectView(R.id.book_info)
    public TextView bookInfoTV;
    private Intent bookManagerIntent;
    private boolean mIsBind = false;
    private IBookManager mBookManager;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //客户端获取代理对象
            mBookManager = IBookManager.Stub.asInterface(service);
            Log.d("nothingwxq", "start");
            try {
                mBookManager.registerListener(mIOnNewBookArrivedListener);
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBookManager == null) {
                return;
            }
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
            //
            Log.d("nothingwxq", "start again");
            bindService(bookManagerIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bookManagerIntent = new Intent(this, DemoService.class);
        mIsBind = bindService(bookManagerIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.inject(this);
        String des =  getIntent().getStringExtra("desc");
        Log.d("nothingwxq","" + des);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.book_add)
    public void add(View view) {
        addBook();
    }

    @OnClick(R.id.book_count)
    public void count(View view) {
        getBookList();
    }

    private void addBook() {
        if (mBookManager != null && !TextUtils.isEmpty(bookNameTV.getText().toString())) {
            Book book = new Book((int) SystemClock.elapsedRealtime(), bookNameTV.getText().toString());
            try {
                mBookManager.addBook(book);
                Log.d("nothingwxq", " current thread is " + Thread.currentThread());
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                bookNameTV.setText("");
            }
        }
    }

    public void getBookList() {
        try {
            if (mBookManager != null) {
                List<Book> list = mBookManager.getBookList();
                if (list != null && list.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (Book book : list) {
                        builder.append(book.toString());
                        builder.append('\n');
                    }
                    bookInfoTV.setText(builder.toString());
                } else {
                    bookInfoTV.setText("Empty~!");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrivedListener(Book book) throws RemoteException {
            Log.d("nothingwxq", " current thread is " + Thread.currentThread());
            Message message = Message.obtain();
            message.what = 1;
            message.obj = book;
            mHandler.sendMessage(message);
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Book book = (Book) msg.obj;
                    StringBuilder builder = new StringBuilder();
                    builder.append(book.toString());
                    builder.append('\n');
                    bookInfoTV.setText(builder.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.unregisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mIsBind) {
            mIsBind = false;
            unbindService(mConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.unregisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                Log.d("nothingwxq", "not found");
                e.printStackTrace();
            }
        }
    }
}
