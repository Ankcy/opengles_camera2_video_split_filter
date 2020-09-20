package com.jqkj.gles20test;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CameraActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;

    private CameraView mCameraView;

    private CameraRender mCameraRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mRadioGroup = findViewById(R.id.radiogroup);
        mCameraView = findViewById(R.id.cameraView);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                myRequetPermission();
                return;
            }
        }


        mCameraRender = mCameraView.getRenderer();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb0) {
                    mCameraRender.setFilter(0);
                } else if (checkedId == R.id.rb2) {
                    mCameraRender.setFilter(2);
                } else if (checkedId == R.id.rb3) {
                    mCameraRender.setFilter(3);
                } else if (checkedId == R.id.rb4) {
                    mCameraRender.setFilter(4);
                } else if (checkedId == R.id.rb6) {
                    mCameraRender.setFilter(6);
                } else if (checkedId == R.id.rb9) {
                    mCameraRender.setFilter(9);
                }


            }
        });

        findViewById(R.id.go2video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, VideoActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            //Toast.makeText(this,"您已经申请了权限!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


}