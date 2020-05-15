package com.andymao.camerafun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.andymao.camerafun.opengl.Filter;
import com.andymao.camerafun.opengl.Filter4;
import com.andymao.camerafun.opengl.FilterNV21;
import com.andymao.camerafun.opengl.GLHelper;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLActivity extends AppCompatActivity {
    private GLSurfaceView mSurfaceView;
    private int mTextureId = GLHelper.NO_TEXTURE;
    private int mTextureId2 = GLHelper.NO_TEXTURE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ViewGroup container = (ViewGroup) findViewById(R.id.main);
        mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLContextClientVersion(2);
//        mSurfaceView.setBackgroundColor(Color.parseColor("#ffffff"));
        container.addView(mSurfaceView);

        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_web);
        final Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.crop);
        final Bitmap bitmap3[] = new Bitmap[1];
        try {
            bitmap3[0] = BitmapFactory.decodeStream(getResources().getAssets().open("IMG_20180614_170038.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        final Bitmap bitmap2 = BitmapFactory.decodeFile("/sdcard/idlefish_video/sticker.png");
//        final Filter3 filter = new Filter3();
//        final FilterNV21 filter = new FilterNV21();
//        final Filter4 filter = new Filter4();
        final Filter filter = new Filter();

        mSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                mTextureId = GLHelper.loadTexture(bitmap, mTextureId);
//                mTextureId2 = GLHelper.loadTexture(bitmap2, mTextureId2);
                filter.init();
                long start = System.currentTimeMillis();
//                filter.loadYUV();
                Log.e("andymao", "onSurfaceCreated use time=" + (System.currentTimeMillis() - start));
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                GLES20.glViewport(0, 0, width, height);
                GLES20.glViewport(0, 0, 1080, 1080);
//                GLES20.glViewport(0, 0, 1080, 1080);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                long start = System.currentTimeMillis();
                filter.drawFrame(mTextureId);
                Log.e("andymao","onDrawFrame use time="+(System.currentTimeMillis()-start));
            }
        });

//        mSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//                for (; ; ) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    int[] textures = new int[1];
//                    GLES20.glGenTextures(1, textures, 0);
//
//                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
//
//                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
//                            GLES20.GL_CLAMP_TO_EDGE);
//                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
//                            GLES20.GL_CLAMP_TO_EDGE);
//                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
//                            GLES20.GL_NEAREST);
//                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
//                            GLES20.GL_NEAREST);
//
//                    if(bitmap3[0]!=null)
//                        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap3[0], 0);
//                }
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl, int width, int height) {
//
//            }
//
//            @Override
//            public void onDrawFrame(GL10 gl) {
//
//            }
//        });
    }

}
