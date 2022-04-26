package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import it.sauronsoftware.jave.MyDefaultFFMPEGLocator;


public class FFMPEG extends MyDefaultFFMPEGLocator{
    private static File ffmpeg;
    private final FFbinaryContextProvider context;
    private static FFMPEG instance = null;


    FFMPEG(FFbinaryContextProvider context) {
        this.context = context;
        ffmpeg = FileUtils.getFFmpeg(this.context.provide());

    }


    public static FFMPEG getInstance(final Context context) {
        if (instance == null) {
            instance = new FFMPEG(() -> context);
        }
        return instance;
    }

    @SuppressLint("LongLogTag")
    public boolean isSupported() {

            Log.d("error", "file does not exist, creating it...");
            Log.d("file00path", ffmpeg.getAbsolutePath());
            try {
                InputStream inputStream = context.provide().getAssets().open("ffmpeg");
                MyDefaultFFMPEGLocator.path = ffmpeg.getAbsolutePath();
                if (!FileUtils.inputStreamToFile(inputStream, ffmpeg)) {
                    return false;
                }
                inputStream.close();
            } catch (IOException e) {
                Log.e("error while opening assets", String.valueOf(e));
                return false;
            }
     //   }
        // check if ffmpeg can be executed
        if (!ffmpeg.canExecute()) {
            // try to make executable

            try {
                try {
                    Runtime.getRuntime().exec("chmod -R 777 " + ffmpeg.getAbsolutePath()).waitFor();
                } catch (InterruptedException e) {
                    Log.e("interrupted exception", String.valueOf(e));
                    return false;
                } catch (IOException e) {
                    Log.e("io exception", String.valueOf(e));
                    return false;
                }

                if (!ffmpeg.canExecute()) {
                    // our last hope!
                    if (!ffmpeg.setExecutable(true)) {
                        Log.e("error", "unable to make executable");
                        return false;
                    }
                }
            } catch (SecurityException e) {
                Log.e("security exception", String.valueOf(e));
                return false;
            }
        }

        Log.d("myTagffmpeg", "ffmpeg is ready!");

        return true;
    }

}
