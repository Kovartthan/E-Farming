package com.ko.efarming.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.util.Locale;

public class IntentUtils {
    static final String TAG = "IntentUtils";

    //Refer http://www.androidsnippets.com/open-any-type-of-file-with-default-intent
//    public static void openFile(final BaseCompatActivity context, final String filePath) {
//        new AsyncTask<Void, Void, String>() {
//
//            private Dialog mDialog;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                if (context.isPageVisible()) {
//                    mDialog = UiUtils.getSpinnerDialog(context, context.getString(R.string.loading));
//                    mDialog.show();
//                }
//            }
//
//            @Override
//            protected String doInBackground(Void... params) {
//                String ext = FileManager.getExtension(filePath);
//                File tmpShare = TempManager.createTempShareFile(context, ext);
//                try {
//                    FileUtils.copyStream(new File(filePath), tmpShare);
//                    return tmpShare.getAbsolutePath();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                if (mDialog != null) {
//                    mDialog.dismiss();
//                }
//
//                if (s != null) {
//                    Uri uri = Uri.fromFile(new File(s));
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    String mimeType = FileManager.getFileMimeType(filePath);
//                    intent.setDataAndTypeAndNormalize(uri, mimeType);
//                    //Log.e(TAG, "File - path:" + filePath);
//                    //Log.e(TAG, "Extension:" + mimeType);
//                    if (intent.resolveActivity(context.getPackageManager()) != null) {
//                        context.startActivity(intent);
//                    } else {
//                        AlertUtils.showToast(context, context.getString(R.string.no_application_found_to_open));
//                    }
//                }
//            }
//        }.execute();
//    }

    public static void viewUrl(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void openMapLocation(Context context, double lat, double lon, String label) {
        String url = String.format(Locale.US, "geo:0,0?q=%s,%s(%s)", lat, lon, label);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static String getMimeType(String file) {
        String type = null;
        file = file.replace(" ", "");
        //String extension = MimeTypeMap.getFileExtensionFromUrl(file);
        String extension = FileUtils.getExtension(file);
        if (!TextUtils.isEmpty(extension)) {
            extension = extension.replace(".", "");
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

//        File:VID_20160328_123217166_1459148621781.mp4
//        extension:mp4, type:video/mp4

//        File:_tmp_1459148715742
//        extension:, type:null
        Log.i(TAG, "File:" + file + "\nextension:" + extension + ", type:" + type);
        return type;
    }


//    public static String getFormattedContent(String content){
//        String transparentBgContent = "<html>\n" +
//                " <head>\n" +
//                "  <style type=\"text/css\"> \n" +
//                "   @font-face { \n" +
//                "       font-family: MyFont; \n" +
//                "       src: url(\"file:///android_asset/fonts/opensans_regular.ttf\") \n" +
//                "   } \n" +
//                "   body { \n" +
//                "       font-family: MyFont; \n" +
//                "       text-align: justify; \n" +
//                "       color: #000000; \n" +
////                "       padding-bottom : 12px" +
////                "       margin-left : 10px" +
////                "       margin-right : 10px" +
//                "   } \n" +
//                "  </style> \n" +
//                " </head>\n" +
//                "  <body><font size=6>\n" + (TextUtils.isNullOrEmpty(content) ? "" : content) +
//                "  </font></body>\n" +
//                "</html>";
//        return transparentBgContent;
//    }

}
