package com.ko.efarming.util;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;


import com.ko.efarming.R;

import java.io.File;

public class TempManager {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static final String TEMP_DIR = "temp";
    private static final String TEMP_FILE_NAME = "VideoAndroid.mp4";
    private static final String TEMP_IMAGE_FILE = "PictureAndroid.jpg";
    private static final String TEMP_DOCUMENT = "DocumentAndroid.pdf";
    private static final String TEMP_AUDIO = "AudioAndroid.mp3" ;
    private static final String TEMP_AUDIO_AMR = "AudioAndroid.amr";
    private static final String COMMON_FILE = "cmndir";
    private static final String PIC_DIR = "3";

    /**
     * Note: this is delete existing and create new file
     */
    public static File createTempImageFile(Context context) {
        File f = getTempImageFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    public static File createTempPictureFile(Context context) {
        File dir = new File(getDirectory(context), PIC_DIR);
        dir.mkdirs();
        String name = String.valueOf(System.currentTimeMillis());
        return new File(dir, name + ".jpg");
    }
    public static String getOutputFilePath(Context context) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), ""+context.getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }
    public static File getTempPictureFile(Context context) {
        File dir = new File(getDirectory(context), PIC_DIR);
        long max = 0;
        String result = null;
        for (File file : dir.listFiles()) {
            String path = file.getAbsolutePath();
            String fname = file.getName();
            String name = fname.substring(0, fname.indexOf("."));
            long date = Long.parseLong(name);
            if (date > max) {
                max = date;
                result = path;
            }
        }

        if (result != null) {
            return new File(result);
        }
        return null;
    }

    private static File getDirectory(Context context) {
        // File dir = new File(context.getExternalCacheDir(), TEMP_DIR);
//        Log.d("ss","temp file create 3");
        File dir = new File(context.getExternalCacheDir(), TEMP_DIR);
        dir.mkdirs();
//        Log.d("ss","temp file create 3 end");
        return dir;
    }

    public static File getTempFile(Context context) {
        return new File(getDirectory(context), "_tmp");
    }

    public static File getTempAudioFile(Context context) {
        return new File(getDirectory(context), TEMP_AUDIO);
    }

    public static File getTempAudioAMR(Context context) {
        return new File(getDirectory(context), TEMP_AUDIO_AMR);
    }

    public static File createTempAMRAudioFile(Context context) {
        File file = getTempAudioAMR(context);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static File createTempAudioFile(Context context) {
        File file = getTempAudioFile(context);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static File getTempImageFile(Context context) {
        return new File(getDirectory(context), TEMP_IMAGE_FILE);
    }

    public static File getTempPdfDocumentFile(Context context) {
        return new File(getDirectory(context), TEMP_DOCUMENT);
    }

    public static File createNewDocumentFile(Context context) {
        File file = getTempPdfDocumentFile(context);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    //public static File createNewDrawingFile(C)

    public static File createTempVideoFile(Context context) {
        File f = getTempVideoFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    public static File createTempVideoFile(Context context, String tempFileName) {
//        Log.d("ss","temp file create 1");
        File f = getTempVideoFile(context, tempFileName);

        if (f.exists()) {
            f.delete();
        }
//        Log.d("ss","temp file create 1 end");
        return f;
    }

    public static File getTempVideoFile(Context context) {
        return new File(getDirectory(context), TEMP_FILE_NAME);
    }

    public static File getTempVideoFile(Context context, String tempFileName) {
//        Log.d("ss","temp file create 2");
        return new File(getDirectory(context), tempFileName);
    }

    private static File getTempShareDir(Context context) {
        File dir = new File(getDirectory(context), "tmp_share");
        dir.mkdirs();
        return dir;
    }

    public static File getTempShareFile(Context context, String ext) {
        File file = new File(getTempShareDir(context), "tooteet_share" + ext);
        return file;
    }

    public static File createTempShareFile(Context context, String ext) {
        FileUtils.delete(getTempShareDir(context));
        File file = getTempShareFile(context, ext);
        /*if (file.exists()) {
            file.delete();
        }*/
        return file;
    }

    public static File getVideoThumbFile(Context context, String fileName) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            File thumb = new File(getDirectory(context), fileName+"_" + i + ".png");
            if (!thumb.exists()) {
                return thumb;
            }
        }
        return null;
    }

    public static File getThumbFile(Context context,String fileName) {
        if(TextUtils.isEmpty(fileName)) {
            fileName="Thumb";
        }
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if(fileName.contains(".")){
                fileName = fileName.substring(0, fileName.indexOf("."));
            }
            File thumb = new File(getDirectory(context), fileName+"_" +i+ ".png");
            if (!thumb.exists()) {
                return thumb;
            }
        }
        return null;
    }

    public static void clearTempDir(Context context) {
        File dir = getDirectory(context);
        for (File f : dir.listFiles()) {
            if (f.exists()) {
                f.delete();
            }
        }

        File picDir = new File(dir, PIC_DIR);
        if (picDir.exists()) {
            for (File file : picDir.listFiles()) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public static File createCommonFile(Context context, String encryptedFileName) {
        File dir = new File(getDirectory(context), COMMON_FILE);
        dir.mkdir();

        if (dir.list() != null && dir.list().length != 0) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
        return new File(dir, encryptedFileName);
    }

    public static File createTempVideoFileForCamera(Context context) {

        File f = getTempVideoFileForCamera(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    public static File getTempVideoFileForCamera(Context context) {
        return new File(getDirectory(context), SystemClock.currentThreadTimeMillis()+TEMP_FILE_NAME);
    }

}
