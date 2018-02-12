package com.andymao.camerafun.executor;

public abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }

    public final void destroy() {
        synchronized (this) {
            mInstance = null;
        }
    }
}