package com.jqkj.gles20test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;

    private VideoView mVideoView;

    private VideoRender mVideoRender;

    public String videoPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/camera/hz_extract_20200908_112015.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mRadioGroup = findViewById(R.id.radiogroup);
        mVideoView = findViewById(R.id.videoView);

        mVideoRender = mVideoView.getRenderer();

        try {
            mVideoRender.setVideoSrc(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideoRender.startVideo();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb0) {
                    mVideoRender.setFilter(0);
                } else if (checkedId == R.id.rb2) {
                    mVideoRender.setFilter(2);
                } else if (checkedId == R.id.rb3) {
                    mVideoRender.setFilter(3);
                } else if (checkedId == R.id.rb4) {
                    mVideoRender.setFilter(4);
                } else if (checkedId == R.id.rb6) {
                    mVideoRender.setFilter(6);
                } else if (checkedId == R.id.rb9) {
                    mVideoRender.setFilter(9);
                }


            }
        });

        findViewById(R.id.go2camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("=====", "onResume");
        mVideoView.onResume();
        mVideoRender.restartVideo();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("=====", "onPause");
        mVideoView.onPause();
        mVideoRender.pauseVideo();
    }


}