package com.andymao.camerafun.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by hongyun
 * on 02/01/2018
 */

public class AVThread extends Thread {
    protected final String TAG = "GMM@AVThread";
    private boolean VERBOSE = true;
    private volatile AVThreadHandler mThreadHandler;
    private volatile boolean mThreadReady = false;
    private boolean mThreadQuit = false;
    private final Object mLock = new Object();

    private AVThread() {
        super("av_thread");
    }

    private static SingletonHolder sInstanceHolder = new SingletonHolder();

    public void destroy2() {
        if (VERBOSE) Log.e(TAG, "destroy2");
        if (mThreadHandler != null) {
            mThreadHandler.getLooper().quit();
            mThreadHandler = null;
        }
        sInstanceHolder = null;
    }


    static final class SingletonHolder extends Singleton<AVThread> {
        @Override
        protected AVThread create() {
            AVThread threadManager = new AVThread();
            threadManager.start();
            return threadManager;
        }
    }

    public static AVThread getInstance() {
        return sInstanceHolder.get();
    }

    @Override
    public void run() {
        if (!mThreadReady) {
            Looper.prepare();
            synchronized (mLock) {
                mThreadHandler = new AVThreadHandler(Looper.myLooper());
                mThreadReady = true;
                mLock.notify();
            }
            if (VERBOSE) Log.e(TAG, "run before loop thread=" + Thread.currentThread());
            Looper.loop();
            if (VERBOSE) Log.e(TAG, "run after loop thread=" + Thread.currentThread());
        }
    }

    public void quit() {
        if (VERBOSE) Log.e(TAG, "quit");
        AVThreadHandler.HandlerObj handlerObj = new AVThreadHandler.HandlerObj();
        handlerObj.callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mThreadReady = false;
                if (VERBOSE) Log.e(TAG, "handle quit message");
                return true;
            }
        };
        mThreadHandler.sendMessage(Message.obtain(mThreadHandler, AVThreadHandler.MSG_THREAD_QUIT, handlerObj));
    }

    public void doSendMessage(Message msg) {
        this.mThreadHandler.sendMessage(msg);
    }


    public void startRunning(Handler.Callback callback) {
        waitIfNotReady();
        AVThreadHandler.HandlerObj handlerObj = new AVThreadHandler.HandlerObj();
        handlerObj.callback = callback;
        mThreadHandler.sendMessage(Message.obtain(mThreadHandler, AVThreadHandler.MSG_PIPE_START_RUNNING, handlerObj));
    }

    private void waitIfNotReady() {
        if(VERBOSE) Log.e(TAG,"waitIfNotReady");
        while (!mThreadReady) {
            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (VERBOSE) Log.e(TAG, "startRunning error");
                }
            }
        }

        if(VERBOSE) Log.e(TAG,"waitIfNotReady out of while");
    }

    public void endRunning(Handler.Callback callback) {
        AVThreadHandler.HandlerObj handlerObj = new AVThreadHandler.HandlerObj();
        handlerObj.callback = callback;
        mThreadHandler.sendMessage(Message.obtain(mThreadHandler, AVThreadHandler.MSG_PIPE_END_RUNNING, handlerObj));

        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public Handler getHandler() {
        waitIfNotReady();
        if (VERBOSE) Log.e(TAG, "getHandler");
        return mThreadHandler;
    }
}
