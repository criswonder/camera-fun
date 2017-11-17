package com.andymao.camerafun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.andymao.camerafun.lib.CameraToMpegTest;

public class Mp4TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_test);

        CameraToMpegTest cameraToMpegTest = new CameraToMpegTest();
        try {
            cameraToMpegTest.testEncodeCameraToMp4();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
