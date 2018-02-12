package com.andymao.camerafun.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by hongyun
 * on 02/01/2018
 */
public class AVThreadHandler extends Handler {
    private final String TAG = "GMM@AVThreadHandler";
    private boolean VERBOSE = true;
    public static final int MSG_THREAD_QUIT = -1;
    public static final int MSG_PIPE_START_RUNNING = 1;
    public static final int MSG_PIPE_END_RUNNING = 2;

    public AVThreadHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        if (VERBOSE) Log.e(TAG, "handleMessage thread=" + Thread.currentThread());
        HandlerObj handlerObj = (HandlerObj) msg.obj;
        switch (msg.what) {
            case MSG_THREAD_QUIT:
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    looper.quit();
                    AVThread.getInstance().destroy2();
                }

                if (handlerObj != null && handlerObj.callback != null) {
                    handlerObj.callback.handleMessage(Message.obtain(this, msg.what));
                }

                if (VERBOSE) Log.e(TAG, "quit!");
                break;
            default:
                if (handlerObj != null && handlerObj.callback != null) {
                    handlerObj.callback.handleMessage(Message.obtain(this, msg.what));
                }
                Log.e(TAG, "handleMessage default, unknown");
                break;
        }
    }

    static class HandlerObj {
        public Callback callback;
    }
}
