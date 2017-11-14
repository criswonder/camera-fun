package com.andymao.camerafun;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Log {
    private static volatile boolean sLogSwitch = true;

    private static HashMap<String, String> sTags = new HashMap<String, String>(8);
    private static String sTagSuffix = "Cam3@";
    private static ExecutorService sSingleThreadPool;
    private static Handler sHandler;

    public static String getTag(String clazzName) {
        String tag = sTags.get(clazzName);
        if (tag != null) {
            return tag;
        } else {
            if (clazzName.equals("CameraWrapper")) {
                tag = sTagSuffix + "CameraWrapper";
            } else {
                tag = sTagSuffix + clazzName;
            }

            if (tag.length() > 23) {
                tag = tag.substring(0, 22);
            }

            sTags.put(clazzName, tag);
        }
        return tag;
    }

    public static void setLogSwitch(boolean flag) {
        sLogSwitch = flag;
    }

    public static void v(String tag, String msg) {
        if (sLogSwitch) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (sLogSwitch) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (sLogSwitch) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void dWithLatency(String tag, String msg, int latency) {
        if (sLogSwitch) {
            if (sSingleThreadPool == null) {
                sSingleThreadPool = Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return null;
                    }
                });

            }
            sSingleThreadPool.submit(new Runnable() {
                @Override
                public void run() {

                }
            });
            long l = System.currentTimeMillis();
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sLogSwitch) {
            android.util.Log.d(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sLogSwitch) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sLogSwitch) {
            android.util.Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (sLogSwitch) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sLogSwitch) {
            android.util.Log.e(tag, msg, tr);
        }
    }

}