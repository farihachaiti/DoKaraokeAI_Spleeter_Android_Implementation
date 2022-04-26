package com.example.dokaraokeAI.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.LauncherActivity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dokaraokeAI.MainActivity2;
import com.example.dokaraokeAI.MainActivity3;
import com.example.dokaraokeAI.MusicPlayer;
import com.example.dokaraokeAI.R;

import java.io.File;
import java.io.IOException;

public class MainFragment extends Fragment {

    //private MainViewModel mViewModel;
    private final File path;
    public static boolean isPlaying;
    @SuppressLint("StaticFieldLeak")
    public static ListView listViewParent;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment(){
        this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/doKaraokeAI/result/");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.main_fragment, container, false);
        ListView listView = v.findViewById(R.id.listView);
        String[] fileList = path.list();
        listViewParent = listView;
        changeControlFragment();
        if(fileList == null || fileList.length==0){
            MainActivity3.setEmpView(listView);
        }
        else{
            introduceAdapterView(listView,fileList);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                // TODO Auto-generated method stub

                File targetFile = new File(path + "/" + parent.getItemAtPosition(position).toString());
                Uri outUri = Uri.parse(targetFile.toString().replaceAll("\\s","%20"));
                playSong(outUri);
                isPlaying = true;
                changeControlFragment();
            });

        }

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    public void playSong(Uri outUri){
        MusicPlayer.SoundPlayer(MainActivity3.getContext(),outUri);
        MusicPlayer.player.start();
    }

    public void introduceAdapterView(ListView listView, String[] fileList){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.getContext(),
                R.layout.list_view, R.id.list_item, fileList);

        listView.setAdapter(adapter);

        if (adapter.getCount() == 0)
            MainActivity3.setEmpView(listView);
    }

    public static void changeControlFragment(){
        if(isPlaying)
            disableView(listViewParent);
        else
            enableView(listViewParent);
    }


    public static void disableView(ListView listViewParent){
        listViewParent.setClickable(false);
        listViewParent.setEnabled(false);

    }


    public static void enableView(ListView listViewParent){
        listViewParent.setClickable(true);
        listViewParent.setEnabled(true);
    }
}