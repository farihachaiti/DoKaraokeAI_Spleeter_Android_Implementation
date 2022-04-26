package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class ExtractAudio extends AsyncTask<String, String, String> {
    public static String url;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar bar;
    @SuppressLint("StaticFieldLeak")
    public static TextView label;
    public static AlertDialog progressDialog;
    public static Boolean isDownloaded;


    public ExtractAudio(String paramurl, AlertDialog pd,Boolean isDnloaded){
       url = paramurl;
       progressDialog = pd;
       isDownloaded = isDnloaded;


    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onPreExecute() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MyApp.getContext(), "Extracting Audio...", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        //label.setText("Audio Extracting");
        //label.setVisibility(View.VISIBLE);
        //bar.setVisibility(View.VISIBLE);

        ShowProgress progress = new ShowProgress(progressDialog, "Audio Extracting...");
        progress.execute();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String videoName = URLUtil.guessFileName(url, null, null);
        Log.d("myTag2", url);
        Log.d("myTag", isDownloaded.toString());
        String dwnFileDir;
        if (isDownloaded) {
            dwnFileDir = url;
        }
        else {
            File downloadedFile = new File(url);
            dwnFileDir = downloadedFile.getAbsolutePath();
        }
        Log.d("myTag0", dwnFileDir);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/doKaraoke/");
        String newFile;
        newFile = videoName.replace(videoName.substring(videoName.lastIndexOf(".")),".mp3");
        File file;
        file = new File(path, newFile);
        int num = 0;
        Log.d("myTag3", dwnFileDir);
        while(file.exists()) {
            newFile = videoName.replace(videoName.substring(videoName.lastIndexOf(".")),(num++) +".mp3");
            file = new File(path, newFile);
        }
        String dir = file.getAbsolutePath();
        Log.d("myTag4", dwnFileDir);
        Log.d("myTag5", dir);
        try {
            new AudioExtractor().genVideoUsingMuxer(dwnFileDir, dir, -1, -1, true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "done";
    }


    protected void onPostExecute(String s) {
      //  bar.setVisibility(View.GONE);
      //  label.setVisibility(View.GONE);
        ShowProgress.dialogDismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MyApp.getContext(),"Audio Extraction Complete!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        super.onPostExecute("hi");

    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }



    @Override
    protected void onCancelled() {
        try {
            handleCancelMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleCancelMethod() throws IOException {
        cancel(true);
        //finishAffinity();

    }


}
