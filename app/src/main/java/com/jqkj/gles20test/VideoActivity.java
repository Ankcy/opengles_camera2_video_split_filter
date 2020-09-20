package com.jqkj.gles20test;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jqkj.gles20test.util.Constants;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VideoActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;

    private VideoView mVideoView;

    private VideoRender mVideoRender;

    private RelativeLayout mVideoContent;
    private RelativeLayout videoContentBottom;

    public String videoPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/camera/hz_trimmer_20200808_223640.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // 申请存储的读取和写入权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(VideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                myRequetPermission();
                return;
            }
        }

        Constants.init(this);

        initView();

        initData();

        initEvent();

    }

    public void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initView() {
        mRadioGroup = findViewById(R.id.radiogroup);
        mVideoView = findViewById(R.id.videoView);
        videoContentBottom = findViewById(R.id.videoContentBottom);
        mVideoContent = findViewById(R.id.videoContent);

        initVideoView();

    }

    private void initVideoView() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoContent.getLayoutParams();

        int rWidth = 0;
        int rHeight = 0;
        int videoWidth = Integer.parseInt(Constants.getMediaWidth(videoPath));
        int videoHeight = Integer.parseInt(Constants.getMediaHeight(videoPath));
        int screenWidth = Constants.getScreenWidth(this);
        int screenHeight = Constants.getScreenHeight(this) - videoContentBottom.getLayoutParams().height - 240;

        float rate = 1f;
        if (videoWidth > videoHeight) {
            rWidth = screenWidth;
            rate = videoWidth * 1f / screenWidth;
            rHeight = (int) (videoHeight * 1f / rate);
        } else {
            rHeight = screenHeight;
            rate = videoHeight * 1f / screenHeight;
            rWidth = (int) (videoWidth * 1f / rate);
        }

        layoutParams.width = rWidth;
        layoutParams.height = rHeight;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        mVideoContent.setLayoutParams(layoutParams);
    }

    private void initData() {
        mVideoRender = mVideoView.getRenderer();
        try {
            mVideoRender.setVideoSrc(videoPath);
        } catch (Exception e) {
            Log.e("=====", e.getMessage());
        }
        mVideoRender.startVideo();
    }

    private void initEvent() {

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
                finish();
            }
        });

        findViewById(R.id.go2Choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("video/*");
                VideoActivity.this.startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
            }
        });

    }

    public static final int REQUEST_CODE_PICK_GALLERY = 8;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                return;
            } else if (resultCode == RESULT_OK) {
                try {
                    Uri videoUri = data.getData();
                    videoPath = Constants.getFilePathFromUri(this, videoUri);
                    initVideoView();
                    mVideoRender.resetVideo();
                    initData();
                    mVideoRender.setFilter(0);
                } catch (Exception e) {
                    Log.e("=====", e.getMessage());
                    return;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("=====", "onResume");
        if (null != mVideoView) {
            mVideoView.onResume();
            mVideoRender.restartVideo();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("=====", "onPause");
        if (null != mVideoView) {
            mVideoView.onPause();
            mVideoRender.pauseVideo();
        }
    }


}