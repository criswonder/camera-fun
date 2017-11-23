package com.andymao.camerafun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;

import com.andymao.camerafun.lib.CameraToMpegTest;

public class Mp4TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_test);

        final TextureView textureView = (TextureView) findViewById(R.id.textureView);
        findViewById(R.id.start).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread newThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CameraToMpegTest cameraToMpegTest = new CameraToMpegTest();
                                cameraToMpegTest.textureView = textureView;
                                try {
                                    cameraToMpegTest.testEncodeCameraToMp4();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        });
                        newThread.start();
                    }
                }
        );

    }
}
