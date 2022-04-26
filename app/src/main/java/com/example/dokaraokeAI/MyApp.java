package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyApp extends Application {
    private Activity currentActivity;
    public Activity getCureentActivity(){
        return currentActivity;
    }

    public void setCurrentActivity(Activity pCurrentActivity){
        this. currentActivity = pCurrentActivity;
    }

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    public static String getMimeFromFileName(String filename){
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(filename);
        return map.getMimeTypeFromExtension(ext);
    }



    public static String getmedia(String url) throws UnsupportedEncodingException {
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "https://" + url;

        }
        return url;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String walkdir(File dir, File fileparam) {
        String filename = fileparam.getName();
        Log.d("myTag0113", filename);
        File[] listFile = dir.listFiles();
        String fullDir = "";
        if (listFile != null && fileparam.exists()) {

            for (File file : listFile) {

                if (!file.isDirectory()) {
                    if (file.equals(fileparam)) {
                        fullDir = fileparam.getParentFile().getName() + filename;
                        break;
                    }
                }
                else
                    walkdir(dir,file);
            }
        }
        Log.d("myTag0114", fullDir);
        return fullDir;
        }
        @SuppressLint({"NewApi", "Recycle"})
        public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
            String selection = null;
            String[] selectionArgs = null;
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {


                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }

        public static boolean isUrlValid(String urlString) throws IOException {
            String regex = "((http|https)://)(www.)?"
                    + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                    + "{2,256}\\.[a-z]"
                    + "{2,6}\\b([-a-zA-Z0-9@:%"
                    + "._\\+~#?&//=]*)";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int code = connection.getResponseCode();
            if (m.matches() || (code ==200))
                return true;
            else
                return false;
        }

    public static int toInt32_2(byte[] bytes, int index)
    {
        int a = (int)((int)(0xff & bytes[index]) << 32 | (int)(0xff & bytes[index + 1]) << 40 | (int)(0xff & bytes[index + 2]) << 48 | (int)(0xff & bytes[index + 3]) << 56);
        // int a = (int)((int)(0xff & bytes[index]) << 56 | (int)(0xff & bytes[index + 1]) << 48 | (int)(0xff & bytes[index + 2]) << 40 | (int)(0xff & bytes[index + 3]) << 32);
        //Array.Resize;
        return a;
    }

    public static short []  byteToShortArray(byte[] arr){
        int size = arr.length;
        short[] shortArr = new short[size];
        for(int i=0; i<size; i++){
            shortArr[i] = (short) arr[i];
        }

        return shortArr;
    }


    public static boolean isInMultiWindowMode(Activity a) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return false;
        }

        return a.isInMultiWindowMode();
    }


}


