package com.andymao.camerafun;

import android.app.Activity;
import android.app.Application;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by hongyun
 * on 14/11/2017
 */

/**
 *
 */
public class CameraWrapper {
    private String TAG = Log.getTag("CameraWrapper");
    private Camera mCamera;
    private static SingleInstance singleInstance;
    private Activity mActivity;
    private Camera.Parameters mParameters;
    private ViewGroup mPreviewParent;
    private Handler mHandler;
    private boolean mUserSurfaceView;

    static class SingleInstance extends SingleInstanceTemplate<CameraWrapper> {

        @Override
        protected CameraWrapper create() {
            return new CameraWrapper();
        }
    }

    public static CameraWrapper getInstance() {
        if (singleInstance == null) {
            singleInstance = new SingleInstance();
        }
        return singleInstance.getInstance();
    }

    private CameraWrapper() {
    }

    public void init(Activity activity, ViewGroup previewParent) {
        mActivity = activity;
        mPreviewParent = previewParent;
        mHandler = new Handler();
        mActivity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
//                if (activity == mActivity) {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getInstance().openCamera();
//                        }
//                    }, 1000);
//
//                }

                getInstance().openCamera();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity == mActivity) {
                    getInstance().pauseCamera();
                }

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == mActivity) {
                    getInstance().releaseCamera();
                }
            }
        });

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (mUserSurfaceView) {
            SurfaceView child = new SurfaceView(mActivity);
            mPreviewParent.addView(child, layoutParams);
            CameraDisplay.getInstance().init(child);
        } else {
            TextureView child = new TextureView(mActivity);
            mPreviewParent.addView(child, layoutParams);
            CameraDisplay.getInstance().init(child);
        }
        CameraDisplay.getInstance().setDisplayListener(new CameraDisplay.DisplayListener() {
            @Override
            public void onDisplayAvailable(CameraDisplay.CameraDisplayParams displayParams) {
                Log.e(TAG, "onDisplayAvailable");
                openCamera();
            }
        });
    }

    public void initCameraConfig(Camera.Parameters parameters) {
        mParameters = parameters;
    }

    public int openCamera() {
        Log.e(TAG, "openCamera");
        mCamera = Camera.open();
        mCamera.getParameters();

        if (mUserSurfaceView) {
            try {
                SurfaceHolder surfaceHolder = CameraDisplay.getInstance().getSurfaceHolder();
                if (surfaceHolder == null) {
                    Log.e(TAG, "openCamera failed surfaceHolder == null");
                } else {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                SurfaceTexture surfaceTexture = CameraDisplay.getInstance().getSurfaceTexture();
                if (surfaceTexture == null) {
                    Log.e(TAG, "openCamera failed surfaceTexture == null");
                }
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return 0;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            Log.e(TAG, "releaseCamera");
            mCamera.release();
        }
    }

    public void pauseCamera() {
        if (mCamera != null) {
            Log.e(TAG, "pauseCamera");
            mCamera.stopPreview();

        }
    }
}
