package com.jqkj.gles20test;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import com.jqkj.gles20test.util.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private CameraView cameraView;

    private SurfaceTexture mCameraTexure;

    private int texure;

    float[] mtx = new float[16];

    private CVFilter CVFilter;

    public CameraRender(CameraView cameraView) {
        this.cameraView = cameraView;
        LifecycleOwner lifecycleOwner = (LifecycleOwner) cameraView.getContext();
        CameraHelper.init(lifecycleOwner, this);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 创建纹理ID，把摄像头的数据与这个纹理关联
        // 当做能在opengl用的一个图片的ID
        mCameraTexure.attachToGLContext(texure);
        // 当摄像头数据有更新回调 OnFrameAvailable
        mCameraTexure.setOnFrameAvailableListener(this);

        // 清理画布缓存
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        CVFilter = new CVFilter(cameraView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraTexure.updateTexImage();
        mCameraTexure.getTransformMatrix(mtx);
        CVFilter.onDraw(mtx, texure);
    }

    public void onSurfaceDestoryed() {
        CVFilter.release();
    }

    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        mCameraTexure = output.getSurfaceTexture();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        cameraView.requestRender();
    }

    public void setFilter(int i) {
        cameraView.queueEvent(new Runnable() {
            @Override
            public void run() {
                CVFilter.release();
                CVFilter.setFilter(i);
                cameraView.requestRender();
            }
        });
    }
}