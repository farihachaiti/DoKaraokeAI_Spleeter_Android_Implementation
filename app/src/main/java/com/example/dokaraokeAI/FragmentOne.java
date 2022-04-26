package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOne extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_PARAM1 = "param1";
    private static String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String url;
    private String mParam2;
    private View mWebView;
    private File path;
    private ListView fileList;

    public FragmentOne() {
        // Required empty public constructor
        this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/doKaraokeAI/result/");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param args Parameter 1.
     */
    // TODO: Rename and change types and number of parameters
    public void newInstance(Bundle args) {

        if (getArguments() != null) {
            args = getArguments();
            url = args.getString("Url");

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one, container, false);
      /*  mWebView = (WebView) v.findViewById(R.id.webView);
        mWebView.setVisibility(View.VISIBLE);
        ((WebView) mWebView).loadUrl("file:///android_res/raw/voiceout.html");
        ((WebView) mWebView).getSettings().setJavaScriptEnabled(true);
        ((WebView) mWebView).setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String loc) {
                view.loadUrl("javascript:makeKaraoke('"+url+"')");
            }
        });*/

        String[] fileList = path.list();
        ImageView playBtn= (ImageView) v.findViewById(R.id.playBtn);
        ImageView closeBtn= (ImageView) v.findViewById(R.id.closeBtn);
        ImageView pauseBtn= (ImageView) v.findViewById(R.id.pauseBtn);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.getContext(),
                R.layout.list_view, R.id.list_item, fileList);
        ListView listView = (ListView)v.findViewById(R.id.listView1);
        listView.setAdapter(adapter);


        closeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                closefragment();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something

            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something

            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBtn.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }

    private void closefragment() {
        requireActivity().getFragmentManager().popBackStack();
    }
}