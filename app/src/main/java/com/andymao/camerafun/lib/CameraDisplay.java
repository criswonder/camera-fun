package com.andymao.camerafun.lib;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;


/**
 * Created by hongyun
 * on 14/11/2017
 */

public class CameraDisplay {
    private final String TAG = "CameraDisplay";
    private static InstanceHolder instanceHolder;
    private SurfaceHolder mSurfaceHolder;
    private boolean mHasSurface;
    private boolean mHasSurfaceTexture;
    private SurfaceTexture mSurfaceTexture;

    public void setDisplayListener(DisplayListener mDisplayListener) {
        this.mDisplayListener = mDisplayListener;
    }

    private DisplayListener mDisplayListener;

    interface DisplayListener {
        void onDisplayAvailable(CameraDisplayParams displayParams);
    }


    public static class CameraDisplayParams {
        public static final int TYPE_SURFACE = 1;
        public static final int TYPE_TEXTURE = 2;
        public int type = TYPE_TEXTURE;
    }


    private static class InstanceHolder extends SingleInstanceTemplate<CameraDisplay> {

        @Override
        protected CameraDisplay create() {
            return new CameraDisplay();
        }
    }

    private CameraDisplay() {

    }

    public static CameraDisplay getInstance() {
        if (instanceHolder == null) {
            instanceHolder = new InstanceHolder();
        }
        return instanceHolder.getInstance();
    }

    public SurfaceHolder getSurfaceHolder() {
        if (mHasSurface)
            return mSurfaceHolder;
        else
            return null;
    }

    public void init(SurfaceView surfaceView) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                mSurfaceHolder = holder;
                mHasSurface = true;
                if (mDisplayListener != null) {
                    CameraDisplayParams displayParams = new CameraDisplayParams();
                    displayParams.type = CameraDisplayParams.TYPE_SURFACE;
                    mDisplayListener.onDisplayAvailable(displayParams);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
                mHasSurface = false;
            }
        });
    }

    public void init(TextureView surfaceView) {
        surfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "onSurfaceTextureAvailable");
                mSurfaceTexture = surface;
                mHasSurfaceTexture = true;
                if (mDisplayListener != null) {
                    CameraDisplayParams displayParams = new CameraDisplayParams();
                    mDisplayListener.onDisplayAvailable(displayParams);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, "onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.e(TAG, "onSurfaceTextureDestroyed");
                mHasSurfaceTexture = false;
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                Log.e(TAG, "onSurfaceTextureUpdated");
            }
        });
    }

    public SurfaceTexture getSurfaceTexture() {
        if (mHasSurfaceTexture)
            return mSurfaceTexture;
        else
            return null;
    }
}
