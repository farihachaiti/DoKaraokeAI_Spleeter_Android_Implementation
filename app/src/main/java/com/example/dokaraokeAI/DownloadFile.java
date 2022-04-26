package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class DownloadFile {
    private static AlertDialog progressDialog;
    private static DownloadManager dm;


    @SuppressLint("SetTextI18n")
    public void downloadfile(String url) {
        ShowProgress progress = new ShowProgress(progressDialog, "File Downloading ");
        progress.execute();
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(url));
        request1.setTitle(URLUtil.guessFileName(url, null, null));
        String videoName = URLUtil.guessFileName(url, null, null);
        request1.setMimeType(MyApp.getMimeFromFileName(videoName));
        //Log.d("myTagi",MyApp.getMimeFromFileName(videoName));
        request1.allowScanningByMediaScanner();
        request1.setVisibleInDownloadsUi(true);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, videoName);
        dm = (DownloadManager) MainActivity2.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        assert dm != null;
        long fileDownloadedId = dm.enqueue(request1);


    }

    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(MainActivity2.getContext()) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) throws UnsupportedEncodingException {

                List<Integer> iTags = Arrays.asList(22, 137, 18);
                for (Integer iTag : iTags) {

                    YtFile ytFile = ytFiles.get(iTag);

                    if (ytFile != null) {

                     String   url = ytFile.getUrl().trim();
                        Log.d("myTag",url);
                        //url = URLDecoder.decode(url, "UTF-8");
                        Log.d("myTag1",url);

                    }

                }

            }
        }.extract(youtubeLink, true, false);

    }
    public static int getStatus(Context context , long downloadId, Intent intent) {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);// filter your download bu download Id
        File mFile;
        int status=0;
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (id == downloadId) {
            Cursor c = downloadManager.query(query);
            if (c != null) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                status = columnIndex;
                try {
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            @SuppressLint("Range") String fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            mFile = new File(Uri.parse(fileUri).getPath());
                        } else {
                            @SuppressLint("Range") String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            mFile = new File(String.valueOf(Uri.parse(filePath)));
                        }

                    } else if (DownloadManager.STATUS_PAUSED == c.getInt(columnIndex)) {
                        ShowProgress.dialogDismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(MainActivity2.getContext(), "File Download Paused!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    } else {
                        ShowProgress progress = new ShowProgress(progressDialog, "File Downloading ");
                        progress.execute();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(MainActivity2.getContext(), "File Download Running!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("error", "Could not open the downloaded file");
                }

            }
        }
        return status;

    }
}
