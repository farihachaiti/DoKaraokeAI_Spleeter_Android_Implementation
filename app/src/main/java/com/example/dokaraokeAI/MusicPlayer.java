package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class MusicPlayer {
    public static MediaPlayer player;
    @SuppressLint("StaticFieldLeak")
    private static ImageView pauseBtn,playBtn,stopBtn;

    public MusicPlayer(ImageView pauseBtn, ImageView playBtn, ImageView stopBtn){
        MusicPlayer.pauseBtn = pauseBtn;
        MusicPlayer.playBtn = playBtn;
        MusicPlayer.stopBtn = stopBtn;
    }

    public static void SoundPlayer(Context ctx, Uri uri){
        player = MediaPlayer.create(ctx, uri);
        player.setLooping(false); // Set looping
        player.setVolume(100, 100);

        player.setOnPreparedListener(mp -> {
            MusicPlayer.pauseBtn.setVisibility(View.VISIBLE);
            MusicPlayer.stopBtn.setVisibility(View.VISIBLE);
            MusicPlayer.playBtn.setVisibility(View.INVISIBLE);
        });

        player.setOnCompletionListener(mp -> {
            mp.stop();
            mp.reset();
            if(mp!=null)
                mp.release();
            mp = null;
        });

        //player.release();
        player.start();

    }


}
