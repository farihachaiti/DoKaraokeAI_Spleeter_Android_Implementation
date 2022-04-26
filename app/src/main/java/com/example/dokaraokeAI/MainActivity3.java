package com.example.dokaraokeAI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dokaraokeAI.ui.main.MainFragment;

public class MainActivity3 extends AppCompatActivity {
    private ImageView playBtn;
    private ImageView pauseBtn;
    private ImageView stopBtn;
    @SuppressLint("StaticFieldLeak")
    private static TextView emptyTextView;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity3 context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity3);
        context = this;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commitNow();


        playBtn = findViewById(R.id.playBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        stopBtn = findViewById(R.id.stopBtn);
        ImageView closeBtn = findViewById(R.id.closeBtn);
        emptyTextView = findViewById(R.id.emptyView);
        pauseBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);

        MusicPlayer mp = new MusicPlayer(pauseBtn,playBtn,stopBtn);

        pauseBtn.setOnClickListener(v->{
            try {
                if (MusicPlayer.player != null && MusicPlayer.player.isPlaying()) {
                    MusicPlayer.player.pause();
                    playBtn.setVisibility(View.VISIBLE);
                    stopBtn.setVisibility(View.VISIBLE);
                    pauseBtn.setVisibility(View.INVISIBLE);
                    MainFragment.isPlaying = false;
                    MainFragment.changeControlFragment();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });

        stopBtn.setOnClickListener(v->{
            try {
                if (MusicPlayer.player != null ||
                        MusicPlayer.player.isPlaying()) {
                    MusicPlayer.player.stop();
                    MusicPlayer.player.release();
                    MusicPlayer.player = null;
                    playBtn.setVisibility(View.INVISIBLE);
                    stopBtn.setVisibility(View.INVISIBLE);
                    pauseBtn.setVisibility(View.INVISIBLE);
                    MainFragment.isPlaying = false;
                    MainFragment.changeControlFragment();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });

        playBtn.setOnClickListener(v->{
            try {
                if (MusicPlayer.player != null && !MusicPlayer.player.isPlaying()) {
                    MusicPlayer.player.start();
                    playBtn.setVisibility(View.INVISIBLE);
                    stopBtn.setVisibility(View.VISIBLE);
                    pauseBtn.setVisibility(View.VISIBLE);
                    MainFragment.isPlaying = true;
                    MainFragment.changeControlFragment();

                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });

        closeBtn.setOnClickListener(v->{
            finish();
        });
    }


    public static Context getContext() {
        return context;
    }


    public static void setEmpView(ListView list){
        list.setEmptyView(emptyTextView);
    }


    @Override
    public void onBackPressed()
    {
        if(MusicPlayer.player!=null && MusicPlayer.player.isPlaying()){
            MusicPlayer.player.stop();
            MusicPlayer.player.release();
            MusicPlayer.player=null;
        }

        super.onBackPressed();
    }


    protected void onDestroy() {
        if(MusicPlayer.player!=null && MusicPlayer.player.isPlaying()){
            MusicPlayer.player.stop();
            MusicPlayer.player.release();
            MusicPlayer.player=null;
        }

        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(MusicPlayer.player!=null && MusicPlayer.player.isPlaying()){
                MusicPlayer.player.stop();
                MusicPlayer.player.release();
                MusicPlayer.player=null;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}