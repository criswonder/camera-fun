package com.andymao.camerafun;

/**
 * Created by hongyun
 * on 14/11/2017
 */

public abstract class SingleInstanceTemplate<T> {
    private T instance;

    protected abstract T create();

    public final T getInstance() {
        synchronized (this) {
            if (instance == null)
                instance = create();
            return instance;
        }
    }
}
