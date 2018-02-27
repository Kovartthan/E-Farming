package com.ko.efarming.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static final String TEMP_DIR = "temp";
    private static final String TEMP_FILE_NAME = "shared_video.mp4";
    private static final String TEMP_IMAGE_FILE = "shared_image.png";
    private static final String TAG = "FileUtils";

   /* public static File createTempImageFile(Context context) {
        File f = getTempImageFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }


    private static File getDirectory(Context context) {
        File dir = new File(context.getExternalCacheDir(), TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getTempImageFile(Context context) {
        return new File(getDirectory(context), TEMP_IMAGE_FILE);
    }

    public static File createTempVideoFile(Context context) {
        File f = getTempVideoFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    public static File getTempVideoFile(Context context) {
        return new File(getDirectory(context), TEMP_FILE_NAME);
    }

    public static String getFileName(Context context, Uri uri, boolean isPicture) {
        String path = isPicture ? getRealPathFromURI_Images(context, uri) : getRealPathFromURI_Videos(context, uri);
        int index = path.lastIndexOf(File.separator);
        if (index != -1 && (index + 1) < path.length()) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf(File.separator);
        if (index != -1 && (index + 1) < path.length()) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static String getRealPathFromURI_Images(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getRealPathFromURI_Videos(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/
   public static File createNewTempProfileFile(Context context, String type) {
       File dir = new File(context.getExternalCacheDir(), type);
       dir.mkdir();
       File f = new File(dir, "tmp_" + type + ".png");
       return f;
   }


    public static String getFileName(String path) {
        int index = path.lastIndexOf(File.separator);
        if (index != -1 && (index + 1) < path.length()) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static void copyStream(File source, File destination) throws IOException {
        InputStream inputStream = new FileInputStream(source);
        OutputStream outputStream = new FileOutputStream(destination);
        copyStream(inputStream, outputStream);
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();
    }

//    public static String getExtension(String file) {
//        int startIndex = file.lastIndexOf(".");
//        if (startIndex != -1) {
//            return file.substring(file.lastIndexOf("."));
//        }
//        return "";
//    }

    public static String getExtension(String file) {
        int extensionStartIndex = file.lastIndexOf(".");
        int fileNameStartIndex = file.lastIndexOf(File.separator);
        if (extensionStartIndex != -1 && extensionStartIndex > fileNameStartIndex) {
            return file.substring(file.lastIndexOf("."));
        }
        return "";
    }

    public static boolean isExtensionAvailable(String name) {
        return !TextUtils.isEmpty(name);
    }

    public static void delete(File file) {
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }

        if (file.exists()) {
            file.delete();
        }
    }

    public static File saveTempFile(String fileName, Context context, Uri uri, File cacheFile) {
        File mFile = null;
        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = resolver.openInputStream(uri);

//            mFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR, fileName);
            mFile = new File(cacheFile.getParent(), "temp_"+fileName);
            out = new FileOutputStream(mFile, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return mFile;
    }


    public static boolean isImagePath(String path){
        if(path.endsWith(".png")||path.endsWith(".PNG")||path.endsWith(".jpg")||path.endsWith(".JPG")||path.endsWith(".jpeg")||path.endsWith(".JPEG")){
            return true;
        } else {
            return false;
        }
    }


    public static File saveBitmapToFile(Context context, Bitmap bm) {

        File dir = new File(context.getExternalCacheDir(), "default");
        dir.mkdir();
        File imageFile = new File(dir, "default" + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(Bitmap.CompressFormat.PNG, 60, fos);

            fos.close();

            return imageFile;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
