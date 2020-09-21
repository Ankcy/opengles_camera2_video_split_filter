package com.jqkj.gles20test;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

    private ImageView playVideoBtn;

    private int filter;

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

        initVideoData(true);

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
        playVideoBtn = findViewById(R.id.playVideoBtn);

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

    private void initVideoData(boolean start) {
        mVideoRender = mVideoView.getRenderer();
        try {
            mVideoRender.setVideoSrc(videoPath);
        } catch (Exception e) {
            Log.e("=====", e.getMessage());
        }
        if (start) {
            mVideoRender.startVideo();
        }
    }

    private void initEvent() {

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb0) {
                    filter = 0;
                } else if (checkedId == R.id.rb2) {
                    filter = 2;
                } else if (checkedId == R.id.rb3) {
                    filter = 3;
                } else if (checkedId == R.id.rb4) {
                    filter = 4;
                } else if (checkedId == R.id.rb6) {
                    filter = 6;
                } else if (checkedId == R.id.rb9) {
                    filter = 9;
                } else if (checkedId == R.id.grey) {
                    filter = 10;
                }

                mVideoRender.setFilter(filter);

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

        playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    playVideoBtn.setImageResource(R.drawable.playbtn);
                    mVideoRender.pauseVideo();
                } else {
                    playVideoBtn.setImageResource(R.drawable.pausebtn);
                    mVideoRender.startVideo();
                }
            }
        });

        mVideoRender.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideoBtn.setImageResource(R.drawable.playbtn);
                mVideoRender.resetVideo();
                initVideoData(false);
                mVideoRender.setFilter(filter);
            }
        });

    }

    private boolean isPlaying() {
        if (mVideoRender.getMediaPlayer().isPlaying()) {
            return true;
        } else {
            return false;
        }
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
                    initVideoData(true);
                    mVideoRender.setFilter(filter);
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