package com.example.nothing.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import aidl.Book;
import aidl.IBookManager;
import aidl.IOnNewBookArrivedListener;

/**
 * @Date: 17/3/10
 * @Time: 上午10:44
 * @Description:
 */

public class DemoService extends Service {

    //支持并发读写
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mIOnNewBookArrivedListeners = new RemoteCallbackList<>();
    private AtomicInteger mAtomicInteger = new AtomicInteger(1000);

    private final Object[] mObjects = new Object[1];
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.d("nothingwxq", "calling getBookList~!");
            return mBookList;
        }

        @Override
        public void addBook(final Book book) throws RemoteException {
            Log.d("nothing", " current thread is " + Thread.currentThread());
            mBookList.add(book);
            updateBook(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mIOnNewBookArrivedListeners.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mIOnNewBookArrivedListeners.unregister(listener);
        }
    };

    private void updateBook(final Book book) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int n = mIOnNewBookArrivedListeners.beginBroadcast();
                    for (int i = 0; i < n; i++) {
                        IOnNewBookArrivedListener listener = mIOnNewBookArrivedListeners.getBroadcastItem(i);
                        if (listener != null) {
                            listener.onNewBookArrivedListener(book);
                        }
                    }
                } catch (RemoteException | IllegalStateException e) {
                    e.printStackTrace();
                } finally {
                    mIOnNewBookArrivedListeners.finishBroadcast();
                }

            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("nothing", "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Book book = new Book(getId(), "new book #" + getId());
                updateBook(book);
            }
        }).start();
    }

    public int getId() {
        return mAtomicInteger.addAndGet(1);
    }
}
