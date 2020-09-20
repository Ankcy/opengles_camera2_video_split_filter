package com.jqkj.gles20test.util;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {

    public static final File APP_MAIN_DIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/opengles_cv");

    public static final File TEMP_DIR = new File(APP_MAIN_DIR.getAbsolutePath() + "/temp");

    public static void init(Context context) {
        if (null != mmrs) {
            mmrs.clear();
        }
        if (!TEMP_DIR.exists()) {
            TEMP_DIR.mkdirs();
        } else {
            try {
                // 清空
                File[] files = Constants.TEMP_DIR.listFiles();
                for (int i = files.length - 1; i >= 0; i--) {
                    files[i].delete();
                }
            } catch (Exception e) {

            }
        }
    }

    public static String getFilePathFromUri(Activity activity, Uri uri) {
        String uriStr = uri.toString();
        String suffix = ".mp4";
        if (uriStr.indexOf(".") > -1 && uriStr.lastIndexOf(".mp4") > 0) {
            suffix = uriStr.substring(uriStr.lastIndexOf("."));
        }
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + suffix;
        final String dstDir = Constants.TEMP_DIR.getPath() + File.separator;
        final String filePath = dstDir + "temp_" + timeStamp;
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            File fileImage = new File(filePath);
            is2File(inputStream, fileImage);
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return filePath;
    }

    public static void is2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        return mDisplayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        return mDisplayMetrics.heightPixels;
    }

    public static Map<String, MediaMetadataRetriever> mmrs = new ConcurrentHashMap<>(); // 缓存视频参数

    public static MediaMetadataRetriever getMmr(String mmrPath) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = Constants.mmrs.get(mmrPath);
            if (mmr != null) {
                //log.e("Get mmr mmrPath: " + mmrPath);
                return mmr;
            } else {
                mmr = new MediaMetadataRetriever();
                mmr.setDataSource(mmrPath);
                Constants.mmrs.put(mmrPath, mmr);
            }
        } catch (Exception e) {
            Log.e("======", "getMmr file error = " + e.getMessage());
        }
        return mmr;
    }

    public static String getMediaDuration(String mmrPath) {//单位：毫秒
        MediaMetadataRetriever mmr = getMmr(mmrPath);
        if (null != mmr)
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        else
            return "0";
    }

    public static String getMediaWidth(String mmrPath) {
        //log.e("Get mmr width: " + mmrPath);
        MediaMetadataRetriever mmr = getMmr(mmrPath);
        if (null != mmr)
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        else
            return "0";
    }

    public static String getMediaHeight(String mmrPath) {
        //log.e("Get mmr height: " + mmrPath);
        MediaMetadataRetriever mmr = getMmr(mmrPath);
        if (null != mmr)
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        else return "0";
    }

    public static String getRotation(String mmrPath) {
        //log.e("Get mmr height: " + mmrPath);
        MediaMetadataRetriever mmr = getMmr(mmrPath);
        if (null != mmr)
            return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        else return "0";
    }
}
