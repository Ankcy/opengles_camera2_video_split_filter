package com.jqkj.gles20test;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.jqkj.gles20test.util.OpenGlUtils;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "VideoRender";
    private VideoView videoView;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private boolean updateSurface;
    public boolean playerPrepared;
    public boolean playerPause;
    private int screenWidth, screenHeight;

    private int texure;

    float[] mtx = new float[16];

    private CVFilter CVFilter;

    public VideoRender(VideoView videoView) {
        this.videoView = videoView;

        playerPrepared = false;
        synchronized (this) {
            updateSurface = false;
        }

        // 设置视频参数
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
    }

    public void setVideoSrc(String videoPath) throws IOException {
        mediaPlayer.setDataSource(videoView.getContext(), Uri.parse(videoPath));
    }

    public void startVideo() {
        if (!playerPrepared) {
            try {
                mediaPlayer.prepare();
                playerPrepared = true;
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }
            mediaPlayer.start();
            playerPrepared = true;
        }
    }

    public void restartVideo() {
        if (playerPrepared && playerPause) {
            mediaPlayer.start();
        }
    }

    public void pauseVideo() {
        mediaPlayer.pause();
        playerPause = true;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        texure = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texure);
        OpenGlUtils.checkGlError("glBindTexture mTextureID");
        /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处为：之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
        我们就不需要再为此写YUV转RGB的代码了*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(texure);
        surfaceTexture.setOnFrameAvailableListener(this);//监听是否有新的一帧数据到来

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();


        CVFilter = new CVFilter(videoView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: " + width + " " + height);
        screenWidth = width;
        screenHeight = height;
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();//获取新数据
                //让新的纹理和纹理坐标系能够正确的对应
                surfaceTexture.getTransformMatrix(mtx);
                CVFilter.onDraw(mtx, texure);
                updateSurface = false;
            }
        }

    }

    public void onSurfaceDestoryed() {
        CVFilter.release();
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
        videoView.requestRender();
    }

    public void setFilter(int i) {
        videoView.queueEvent(new Runnable() {
            @Override
            public void run() {
                CVFilter.release();
                CVFilter.setFilter(i);
                videoView.requestRender();
            }
        });
    }
}