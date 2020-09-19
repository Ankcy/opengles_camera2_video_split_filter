package com.jqkj.gles20test.util;

import android.util.Size;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

public class CameraHelper {

    private static CameraX.LensFacing currentFacing = CameraX.LensFacing.BACK;

    public static void init(LifecycleOwner lifecycleOwner, Preview.OnPreviewOutputUpdateListener listener) {
        CameraX.bindToLifecycle(lifecycleOwner, getPreView(listener));
    }

    private static Preview getPreView(Preview.OnPreviewOutputUpdateListener listener) {
        // 分辨率不是最终的分辨率，camerax会自动根据设备的支持情况，结合你的参数，设置一个最为接近的分辨率
        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetResolution(new Size(640,480)).setLensFacing(currentFacing).build();
        Preview preview = new Preview((previewConfig));
        preview.setOnPreviewOutputUpdateListener(listener);
        return preview;
    }


}