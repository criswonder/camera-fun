package com.andymao.camerafun;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.andymao.camerafun.executor.AVThread;
import com.andymao.camerafun.google.OpenGLES20Activity;
import com.andymao.camerafun.lib.CameraToMpegTest;
import com.andymao.camerafun.lib.CameraWrapper;
import com.andymao.camerafun.lib.EncodeAndMuxTest;
import com.andymao.camerafun.lib.Log;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        } else {
            CameraWrapper.getInstance().init(this, (ViewGroup) findViewById(R.id.rootView));
        }


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Thread newThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CameraToMpegTest cameraToMpegTest = new CameraToMpegTest();
//                        cameraToMpegTest.mCamera = CameraWrapper.getInstance().getCamera();
                        try {
                            cameraToMpegTest.testEncodeCameraToMp4();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });

                newThread.start();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Thread newThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EncodeAndMuxTest test = new EncodeAndMuxTest();
                        try {
                            test.testEncodeVideoToMp4();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });

                newThread.start();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVThread.getInstance().getHandler().sendEmptyMessage(1);
            }
        });


        findViewById(R.id.btn_opengl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenGLActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.opengl_es20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenGLES20Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 50) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CameraWrapper.getInstance().init(this, (ViewGroup) findViewById(R.id.rootView));
            }
        }
    }
}
